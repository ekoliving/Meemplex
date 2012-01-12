package org.meemplex.internet.gwt.client.gwtrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.meemplex.internet.gwt.client.FacetBinding;
import org.meemplex.internet.gwt.client.FacetEventHub;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.client.binding.BindingBuilder;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.ErrorEvent;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetHealthListener;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.KnockOutEvent;
import org.meemplex.internet.gwt.shared.MeemEvent;
import org.meemplex.internet.gwt.shared.NewSessionException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


/**
 * This is a client for handling Meemplex GWT-RPC communication.
 * 
 * queue up unregistered facets until they are registered. periodically try to re-register them
 * 
 * TODO (MAYBE) queue up outbound events until a session is obtained
 * 
 * @author Warren Bloomer
 *
 */
public class GwtRpcFacetEventHub implements FacetEventHub, BindingFactory {

	private static final boolean DEBUG = true;
			
	private MeemGwtRpcServiceAsync rpcService;

	/**
	 * Listeners for FacetEvents
	 */
	private final HashSet<FacetEventListener> facetEventListeners = new HashSet<FacetEventListener>();

	/**
	 * Listeners for FacetHealth
	 */
	private final HashSet<FacetHealthListener> facetHealthListeners = new HashSet<FacetHealthListener>();

	/**
	 * A set of FacetBindings that have not yet been Registered with the server.
	 */
	private final HashSet<FacetBinding> unregisteredBindings = new HashSet<FacetBinding>();

	/**
	 * 
	 */
	private final HashSet<FacetBinding> registeredBindings = new HashSet<FacetBinding>();
	
	/**
	 * Number of bindings requested for a Facet Reference
	 */
	private final HashMap<FacetReference, BindingCount> bindingCounts = new HashMap<FacetReference, BindingCount>();

	/**
	 * Receives MeemEvents from the server
	 */
	private final MyReceiver receiver = new MyReceiver();

	/**
	 * A flag to determine whether debug logs are written
	 */
	private int logLevel = 1;

	/** 
	 * The number of receive failures.  This gets reset to 0 when successfully receive
	 * events
	 */
	private int receiveErrorCount = 0;

	/** 
	 * number of receive failure before sending OFFLINE health event
	 */
	private int tolerableErrors = 3;

	/**
	 * A timer for tempering retries of registraiton of Facet interest.
	 */
	private Timer registerTimer;
	
	/**
	 * The number of times allowed for unsuccessful registering.
	 */
	private int registerRetries = 10;
	

	/**
	 * Constructor
	 */
	public GwtRpcFacetEventHub() {
		MeemRpcRequestBuilder requestBuilder = new MeemRpcRequestBuilder();
		this.rpcService = (MeemGwtRpcServiceAsync) GWT.create(MeemGwtRpcService.class);
		((ServiceDefTarget) rpcService).setRpcRequestBuilder(requestBuilder);
		//((ServiceDefTarget) service).setServiceEntryPoint( GWT.getModuleBaseURL() + "/meem/FacetEventService");
		
		start();
	}
	
	public void setLogLevel(int level) {
		this.logLevel = level;
	}

	/**
	 * Start the message receiver.
	 */
	public void start() {
		if (logLevel > 0) {
			GWT.log("MeemGwtRpcClient: starting");
		}
		receiver.start();
	}

	/**
	 * Stop receiving messages from the server.
	 */
	public void stop() {
		if (logLevel > 0) {
			GWT.log("MeemGwtRpcClient: stopping");
		}
		receiver.stop();
	}
	
	/* ------------------ BindingFactory interface ----------------------- */
	
	/**
	 * 
	 * @param facetRef
	 * @return
	 */
	@Override
	public OutboundBinding createOutboundBinding(FacetReference facetRef) {
		// if binding exists, reuse it.
		OutboundBinding binding = (OutboundBinding) getBinding(facetRef);
		if (binding == null) {
			binding = BindingBuilder.createOutbound(facetRef, this);
			unregisteredBindings.add(binding);
			registerUnregisteredListeners();
		}
		incrementBindingRef(binding);
		return binding;
	}
	
	/**
	 * TODO also allow for option Filter spec to be passed in.
	 * 
	 * @param facetRef
	 * @return
	 */
	@Override
	public InboundBinding createInboundBinding(FacetReference facetRef) {
		// if binding exists, reuse it.
		InboundBinding binding = (InboundBinding) getBinding(facetRef); 
		if (binding == null) {		
			binding = BindingBuilder.createInbound(facetRef, this);
			unregisteredBindings.add(binding);
			registerUnregisteredListeners();
		}
		incrementBindingRef(binding);
		return binding;
	}
	
	@Override
	public void releaseBinding(FacetReference facetRef) {
		FacetBinding binding = decrementBindingRef(facetRef);
		if (binding != null) {	// last use of the binding, cleanup
			unregisteredBindings.remove(binding);
			registeredBindings.remove(binding);
			binding.release();	// removes itself from listening to events from the hub
			deregister(binding);	// deregister interest/dependency on the server
		}
	}

	/**
	 * 
	 * @param facetRef
	 * @return
	 */
	private FacetBinding getBinding(FacetReference facetRef) {
		FacetBinding binding = null;
		BindingCount bindingCount = bindingCounts.get(facetRef);
		if (bindingCount != null) {
			binding = bindingCount.getBinding();
		}
		return binding;
	}
	
	/**
	 * Increment the reference count for the binding.
	 * 
	 * @param facetRef
	 * @param binding
	 */
	private void incrementBindingRef(FacetBinding binding) {
		BindingCount bindingCount = bindingCounts.get(binding.getFacetReference());
		if (bindingCount == null) {
			bindingCount = new BindingCount(binding);
			bindingCounts.put(binding.getFacetReference(), bindingCount);
		}
		bindingCount.increment();
	}

	/**
	 * 
	 * @param facetRef
	 * @return FacetBinding, if it exists and reference count is 0.
	 */
	private FacetBinding decrementBindingRef(FacetReference facetRef) {
		FacetBinding binding = null;
		BindingCount bindingCount = this.bindingCounts.get(facetRef);
		if (bindingCount != null) {
			bindingCount.decrement();
			if (bindingCount.count <= 0) {
				binding = bindingCount.getBinding();
				this.bindingCounts.remove(facetRef);
			}
		}
		return binding;
	}

	/* ---------------- FacetEventSender interface ----------------- */

	/**
	 * Add a FacetEventListener to this RPC client.
	 * 
	 * @param listener the listener to add.
	 */
	public void addFacetEventListener(FacetEventListener listener) {

		synchronized (facetEventListeners) {
			facetEventListeners.add(listener);
			//unregisteredListeners.add(listener);
		}
		
		// attempt to register any currently unregistered entries
		registerUnregisteredListeners();
	}

	/**
	 * Remove a FacetEventListener from this RPC client.
	 */
	public void removeFacetEventListener(FacetEventListener listener) {

		synchronized (facetEventListeners) {
			facetEventListeners.remove(listener);
		}

		if (listener instanceof InboundBinding) {
			InboundBinding binding = (InboundBinding) listener;

			// deregister interest for the {Meem, Facet} value pair				
			deregister(binding);
		}
	}

	public void addFacetHealthListener(FacetHealthListener listener) {

		synchronized (facetHealthListeners) {
			//GWT.log("!!! adding health listener: " + listener);
			facetHealthListeners.add(listener);
			//unregisteredListeners.add(listener);
		}

		// attempt to register any currently unregistered entries
		registerUnregisteredListeners();
	}

	public void removeFacetHealthListener(FacetHealthListener listener) {

		synchronized (facetHealthListeners) {
			//GWT.log("!!! adding health listener: " + listener);
			facetHealthListeners.remove(listener);
		}

		// only remove outbound bindins.  an Inbound binding will be removed when it
		// is removed as a FacetEventListener
		if (listener instanceof OutboundBinding) {
			// deregister interest for the {Meem, Facet} value pair				
			deregister((OutboundBinding) listener);
		}
	}
	
	/**
	 * Send an event to the server
	 */
	@Override
	public void facetEvent(FacetEvent event) {
		// make sure unregistered listeners are registered.
		registerUnregisteredListeners();

		// send the event over GwtRPC
		send(event);
	}
	
	@Override
	public void facetHealthEvent(FacetHealthEvent event) {
		// ignore. we do not send FacetHealthEvents to the server as yet.
	}

	/**
	 * Register interest in facets that are outbound from the server.
	 * A list of facets that we are interested in is determined from FacetEventListners that 
	 * have been added to this RPC client.
	 */
	private void registerInterest() {		
		registerUnregisteredListeners();
	}

	private void registerUnregisteredListeners() {
		registerRetries = 10;
		
		if (registerTimer == null) {
			registerTimer = new Timer() {
				public void run() {
					if (!unregisteredBindings.isEmpty() && registerRetries > 0) {
						try {
							Object[] bindings = unregisteredBindings.toArray();
							for (int i = 0; i < bindings.length; i++) {
								Object object = bindings[i];
								
								if (object instanceof InboundBinding) {
									InboundBinding binding = (InboundBinding) object;
									registerInbound(binding);
								}
								else if (object instanceof OutboundBinding) {
									OutboundBinding binding = (OutboundBinding) object;
									registerOutbound(binding);
								}
							}
						}
						finally {
							registerRetries--;
						}
						schedule(3000);
					}
				}
			};
		}
		registerTimer.schedule(5);
	}


	private void handleNewSession() {
		if (logLevel > 1) {
			GWT.log("got new session");
		}

		// set unregisteredBindings with that have previously been registered, and re-register them all.
		unregisteredBindings.addAll(registeredBindings);
		//registeredBindings.clear();

		registerInterest(); // register interest in facets outbound from server
	}

	/**
	 * Finish a server session.
	 */
	protected void endSession() {
		stop();
		
		// de-register interest in facets
		rpcService.bye(new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
			}
			
			public void onFailure(Throwable caught) {
			}
		});
	}

	/**
	 * Register interest in an Outbound Facet on the Meem Server.
	 * 
	 * @param meemPath The path of the meem whose facet we are interested in.
	 * @param facetId the identifier of the facet.
	 * @param facetClass The class of the facet.
	 */
	protected void registerInbound(final InboundBinding binding) 
	{
		if (logLevel > 1) {
			GWT.log("register inbound: " + binding);
		}
		
		rpcService.registerDependency(binding.getFacetReference(), new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				registeredBindings.add(binding);
				unregisteredBindings.remove(binding);
			}
			
			public void onFailure(Throwable caught) {
				if (caught instanceof NewSessionException) {
					handleNewSession();
				}
				else {
					if (logLevel > 0) {
						GWT.log("problem registering binding", caught);
					}
				}
			}
		});

	}
	
	protected void registerOutbound(final OutboundBinding binding) 
	{
		if (logLevel > 1) {
			GWT.log("register outbound: " + binding);
		}
		
		rpcService.registerDependency(binding.getFacetReference(), new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				registeredBindings.add(binding);
				unregisteredBindings.remove(binding);
			}
			
			public void onFailure(Throwable caught) {
				if (caught instanceof NewSessionException) {
					handleNewSession();
				}
				else {
					if (logLevel > 0) {
						GWT.log("problem registering outbound binding", caught);
					}
				}
			}
		});

	}
	
	/**
	 * De-register interest in a facet outbound from a meem on the server.
	 * 
	 * @param meemPath The path of the meem whose facet we are interested in.
	 * @param facetId The identifier of the facet.
	 */
	protected void deregister(final FacetBinding binding) {
		rpcService.removeDependency(binding.getFacetReference(), 
			new AsyncCallback<Void>() {
				public void onSuccess(Void result) {
				}
				
				public void onFailure(Throwable caught) {
					if (caught instanceof NewSessionException) {
						handleNewSession();
					}
					else {
						if (logLevel > 0) {
							GWT.log("problem deregistering interest in facet", caught);
						}
					}
				}
			});
	}

	/**
	 * Send a message on a particular facet.
	 */
//	protected void send(FacetReference facetRef, String method, Serializable[] params) {
//		FacetEvent facetEvent = new FacetEvent();
//	}
	
	protected void send(MeemEvent event) {
		List<MeemEvent> events = new ArrayList<MeemEvent>();
		events.add(event);
		send(events);
	}
	
	protected void send(List<MeemEvent> events) {
		
		rpcService.send(events.toArray(new MeemEvent[]{}), new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				// OK
			}

			public void onFailure(Throwable caught) {
				if (caught instanceof NewSessionException) {
					handleNewSession();
				}
				else {
					if (logLevel > 0) {
						GWT.log("problem sending FacetEvent", caught);
					}
				}
			}
		});
	}


	private void receive() {
		
		if (logLevel > 1) {
			GWT.log("reading MeemEvents... ");
		}

		rpcService.receive(new AsyncCallback<MeemEvent[]>() {
			public void onSuccess(MeemEvent[] events) {
				if (logLevel > 1) {
					GWT.log("got list of MeemEvents... ");
				}
				if (events == null || events.length == 0) {
					handleReceivedEvents(null);
					return;
				}
				if (logLevel > 1) {
					for (MeemEvent event : events) {
						GWT.log("read event: " + event);
						if (event instanceof ErrorEvent) {
							GWT.log("ErrorEvent received while fetching MeemEvents: " + ((ErrorEvent)event).getMessage());
						}
						else if (event instanceof KnockOutEvent) {
							GWT.log("KnockedOut");
						}
					}
				}
				handleReceivedEvents(events);
			}
			
			public void onFailure(Throwable caught) {
				if (caught instanceof NewSessionException) {
					// handle new session
					handleNewSession();
					receiver.schedule(5000);
				}
				else if (caught instanceof RequestTimeoutException) {
					if (logLevel > 1) {
						GWT.log("Timeout while fetching MeemEvents");
					}
					// keep receiving
					receiver.schedule(5);
				}
				else {
					if (logLevel > 0) {
						GWT.log("Problem while fetching MeemEvents", caught);
					}
					
					receiveErrorCount++;
					if (receiveErrorCount > tolerableErrors) {	// too many errors - change LifeCycle state of all Facets
						if (logLevel > 0) {
							GWT.log("Sending offline health state");
						}
						receiver.schedule(5000);	// schedule event receiving in 5secs
						
						// send "offline" HealthEvent to listeners
						FacetHealthEvent facetHealthEvent = new FacetHealthEvent(FacetHealthEvent.OFFLINE, null);
						deliverEvent(facetHealthEvent);
					}
					else {
						receiver.schedule(1000);	// try receiving again in 1sec
					}
				}
			}
		});
	}
	
	/**
	 * Interrupt a blocked receive call in the server
	 *
	 */
	protected void interrupt() {
		
		rpcService.interrupt(new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
			}
			
			public void onFailure(Throwable caught) {
				if (caught instanceof NewSessionException) {
					handleNewSession();
				}
				else {
					if (logLevel > 0) {
						GWT.log("problem interrupting session", caught);
					}
				}
			}
		});
	}

	/**
	 * Send a Facet event to listeners
	 * @param event
	 */
	protected void deliverEvent(FacetEvent event) {
		if (DEBUG) {
			GWT.log("delivering event: " + event);
		}
		Object[] listenerArr = null;
		synchronized (facetEventListeners) {
			listenerArr = facetEventListeners.toArray();
		}
		for (int i = 0; i < listenerArr.length; i++) {
			FacetEventListener listener = (FacetEventListener) listenerArr[i];
			try {
				listener.facetEvent(event);
			}
			catch (Exception e) {
				GWT.log("problem sending facet event", e);
			}
		}
	}

	protected void deliverEvent(FacetHealthEvent event) {
		Object[] listenerArr = null;
		synchronized (facetHealthListeners) {
			listenerArr = facetHealthListeners.toArray();
		}
		for (int i = 0; i < listenerArr.length; i++) {
			FacetHealthListener listener = (FacetHealthListener) listenerArr[i];
			try {
				listener.facetHealthEvent(event);
			}
			catch (Exception e) {
				GWT.log("problem sending facet health event", e);
			}
		}
	}
	
	private void handleReceivedEvents(MeemEvent[] events) {
		if (events != null) {
			receiveErrorCount = 0;			// reset error count
			receiver.schedule(5);			// get more events (after handling these ones)
			for (MeemEvent event : events) {
				if (DEBUG) {
					GWT.log("FacetEventHub: handling received event: " + event);
				}

				if (event instanceof FacetEvent) {
					FacetEvent facetEvent = (FacetEvent) event;
					deliverEvent(facetEvent);
				}
				else if (event instanceof FacetHealthEvent) {
					FacetHealthEvent facetHealthEvent = (FacetHealthEvent) event;
					deliverEvent(facetHealthEvent);
				}
				else if (event instanceof KnockOutEvent) {
					return;
				}
				else {
					// unhandled event
				}
			}
		}
		else {
			// wait before trying to receive more events
			receiver.schedule(1000);
		}
	}

	/**
	 * An object to contiuously make "receive" invocations to "pull"
	 * Facet events from the RPC server.
	 */
	private class MyReceiver {
		private boolean running = false;

		private Timer receiveTimer = new Timer() {
			public void run() {
				if (running) {
					receive();
				}
			}
		};
		
		private void schedule(int delayMillis) {
			if (running) {
				receiveTimer.schedule(delayMillis);
			}
		}

		public void start() {
			running = true;
			schedule(1);
		}

		public void stop() {
			running = false;
			receiveTimer.cancel();
		}
	}
	
	/**
	 * Keeps track of the use of a Binding
	 *
	 */
	private class BindingCount {
		private FacetBinding binding;
		
		private int count = 0;

		public BindingCount(FacetBinding binding) {
			setBinding(binding);
        }
		
		public void setBinding(FacetBinding binding) {
	        this.binding = binding;
        }

		public FacetBinding getBinding() {
	        return binding;
        }

		public int getCount() {
			return count;
		}
		
		public void increment() {
			count++;
		}
		
		public void decrement() {
			count--;
		}
	}
}
