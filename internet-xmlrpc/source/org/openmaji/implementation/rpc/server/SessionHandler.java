/*
 * Created on 2/09/2004
 *
 */
package org.openmaji.implementation.rpc.server;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.Subject;

import org.openmaji.implementation.rpc.binding.util.MeemPathHelper;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;
import org.openmaji.rpc.binding.FacetEvent;
import org.openmaji.rpc.binding.FacetEventListener;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.FacetHealthListener;
import org.openmaji.rpc.binding.KnockOutEvent;
import org.openmaji.rpc.binding.MeemEvent;



/**
 * An RPC handler for a particular client session
 * 
 * implements FacetEventListener to listen to events from Outbound Facet bindings
 * implements FacetEventSender to send events to inbound Facet bindings
 * 
 * When outbound bindings are created, this object is added as a FacetEvent listener
 * When inbound bindings are created, they are made listeners to this object
 * 
 * Maintains a thread that sends queued messages to Inbound Bindings: calls send() on each BindingHelper
 * 
 * @author Warren Bloomer
 *
 */
public class SessionHandler implements SessionMessageHandler, FacetEventListener, FacetHealthListener {

	/** logger for this class */
	private static final Logger logger = Logger.getAnonymousLogger();	
	
	/** session identifier */
	private String sessionId;
	
	/** the subject to use for invocations */
	private Subject subject;

	/** all bindings relating to the session */
	private Map<FacetKey, OutboundHandler<?>> outboundBindings = new HashMap<FacetKey, OutboundHandler<?>>();
	private Map<FacetKey, InboundHandler> inboundBindings  = new HashMap<FacetKey, InboundHandler>();

	/** listeners for incoming facet events */
	//private HashSet facetEventListeners = new HashSet();
	
	/** queue for storing outbound facet events */
	private List<MeemEvent> outboundQueue = new ArrayList<MeemEvent>();
	
	/** used for keeping track of stale sessions */
	private long lastAccessed = System.currentTimeMillis();
	
	private long sessionTimeout = 60000;

	/** the maximum size of the outbound event queue - events are dropped when queue is full */
	private int maxQueueSize = 2048;

	/** whether the SessionHandler is running */
	private boolean running = false;
	
	/** number fo threads trying to 'receive'. used to only allow one thread to receive */
	int receiveCount = 0;

	private boolean trace = false;
	
	/**
	 * Create a new SessionHandler.
	 * 
	 * @param sessionId
	 */
	public SessionHandler(String sessionId, Subject subject) {
		this.sessionId = sessionId;
		this.subject   = subject;
		running = true;
	}

	/**
	 * 
	 * @param sessionId
	 */
	public SessionHandler(String sessionId) {
		this.sessionId = sessionId;
		this.subject   = Subject.getSubject(AccessController.getContext());
//		LogTools.info(logger, "created session with Subject: " + subject);
		running = true;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public boolean isAlive() {
		return running;
	}

	/**
	 * Receives a "send" message from the RPC client
	 * Add to the queue
	 *
	 */
	public void send(String meemPath, String facetId, String classname, String methodName, Object[] args) {

//		LogTools.info(logger, "in send()...");
		
//		if ( !subject.equals( Subject.getSubject(AccessController.getContext()) ) ) {
//			throw new SecurityException("invalid Subject");
//		}
		
		// send message to the appropriate binding
		//try {
			FacetEvent event = new FacetEvent();
			event.setMeemPath(meemPath);
			event.setFacetId(facetId);
			event.setFacetClass(classname);
			event.setMethod(methodName);
			event.setParams(args);

			// send the Facet Event to the appropriate binding
			InboundHandler binding = getInboundBinding(meemPath, facetId, classname);
			binding.facetEvent(event);
//		}
//		catch (ClassNotFoundException ex) {
//			LogTools.info(logger, "class not found for \"" + classname + "\"");
//		}
	}
	
	/**
	 * This method allows the client to pull an outbound messages from the server.
	 * 
	 * The hastable that is returned contains the meem, facet, method and parameters relating
	 * to an outbound message.
	 * 
	 */
	public MeemEvent receive() {

		MeemEvent event = null;
		
		synchronized (outboundQueue) {
			knockExisting();	// knock off any previous threads in receive()
			
			while ( running && event == null && receiveCount == 1) {
				try {
					// try to pull FacetEvent off outbound queue
					event = outboundQueue.remove(0);
					//eventToHashMap(event, result);
				}
				catch (IndexOutOfBoundsException ex) {
					try {
						// if nothing on queue, wait
						outboundQueue.wait(10000);
					}
					catch (InterruptedException e) {
						running = false;
					}
				}
			}

			if (receiveCount > 1 ) {
				if (event != null) {
					// push the event back in the queue
					outboundQueue.add(0, event);
				}
				event = new KnockOutEvent();
				//result.put(FacetEvent.EVENTTYPE, "KnockedOut");
			}

			receiveCount--;
			outboundQueue.notifyAll();
		}

		if (trace) {
			logger.info("Sending event to client: " + event);
		}
		return event;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MeemEvent> receiveMany() {

		List<MeemEvent> events = new ArrayList<MeemEvent>();
		synchronized (outboundQueue) {
			knockExisting();	// knock off any previous threads in receive()
			MeemEvent event = null;
			while ( running && event == null && receiveCount == 1) {
				if  (outboundQueue.size() > 0) {
					//  pull FacetEvents off outbound queue
					while (outboundQueue.size() > 0) {
						event = outboundQueue.remove(0);
						events.add(event);
					}
				}
				else {
					// if nothing on queue, wait
					try {
						outboundQueue.wait(10000);
					}
					catch (InterruptedException e) {
						running = false;
					}
				}
			}

			// check if new request coming in. if so, leave events behind
			if (receiveCount > 1 ) {
				if (trace) {
					logger.info("this \"receive\" call has been knocked off. Returning events to queue");
				}
				outboundQueue.addAll(0, new ArrayList<MeemEvent>(events));	// add a copy of the list to the queue
				events.clear();			// clear the list that is returned to the client
				//result.clear();

				//Hashtable<String, Serializable> msg = new Hashtable<String, Serializable>();
				//msg.put(FacetEvent.EVENTTYPE, "KnockedOut");
				//result.add(msg);
			}

			receiveCount--;
			outboundQueue.notifyAll();
		}
		
		if (trace) {
			logger.info("Sending events to client: " + events);
		}

		return events;
	}
	
	/**
	 * The client has registered interest in a particular outbound facet.
	 * An appropriate outbound binding is created.
	 * 
	 * @param meemPath  location of the Meem
	 * @param facetId   the facet of interest
	 * @param classname the class of the facet
	 */
	public void register(String meemPath, String facetId, String classname ) {
		
//		if ( !subject.equals( Subject.getSubject(AccessController.getContext()) ) ) {
//			throw new SecurityException("invalid Subject");
//		}
		if (trace) {
			logger.info("registering: " + meemPath + ":" + facetId + ":" + classname);
		}
		
		getOutboundBinding(meemPath, facetId, classname);		
	}

	/**
	 * The client has registered interest in a particular inbound facet of a
	 * Meem (outbound from the point of view of the client).
	 * An appropriate inbound binding is created if it does not yet exist.
	 * 
	 * @param meemPath  location of the Meem
	 * @param facetId   the facet of interest
	 * @param classname the class of the facet
	 */
	public void registerOutbound(String meemPath, String facetId, String classname ) {
//		if ( !subject.equals( Subject.getSubject(AccessController.getContext()) ) ) {
//			throw new SecurityException("invalid Subject");
//		}
	
		getInboundBinding(meemPath, facetId, classname);		
	}
	
	/**
	 * The client expresses its lack of interest in the facet.
	 * Resources relating to the binding may be freed.
	 * 
	 * @param meemPath
	 * @param facetId
	 */
	public void deregister(String meemPath, String facetId) {
		
//		if ( !subject.equals( Subject.getSubject(AccessController.getContext()) ) ) {
//			throw new SecurityException("invalid Subject");
//		}
		
		// locate binding and clean it up		
		FacetKey key = new FacetKey(meemPath, facetId);
		
		synchronized (outboundBindings) {
			OutboundHandler<?> binding = outboundBindings.remove(key);
			if (binding != null) {
				binding.cleanup();
			}
		}
	}

	/**
	 * interrupt a blocking receive request
	 *
	 */
	public void interrupt() {
		synchronized(outboundQueue) {
			outboundQueue.notifyAll();
		}
	}
	
	/**
	 * Free all bindings and cleanup any resources used in this session.
	 */
	public void cleanup() {
		
		// cleanup bindings
		
		synchronized (outboundBindings) {
			for (FacetKey key : outboundBindings.keySet()) {
				OutboundHandler<?> binding = outboundBindings.get(key);
				binding.cleanup();
			}
			outboundBindings.clear();
		}

		synchronized (inboundBindings) {
			for (FacetKey key : inboundBindings.keySet()) {
				InboundHandler binding = inboundBindings.get(key);
				binding.cleanup();
			}
			inboundBindings.clear();
		}
		
		synchronized(outboundQueue) {
			running = false;
			outboundQueue.clear();
			outboundQueue.notifyAll();
		}
	}

	/**
	 * Return the subject for this session
	 */
	public Subject getSubject() {
		return subject;
	}


	
	/**
	 * Set the timeout of this session in milliseconds
	 * @param milliseconds
	 */
	public void setSessionTimeout(long milliseconds) {
		this.sessionTimeout = milliseconds;
	}
	
	/**
	 * A check to see whether the session has timed-out.
	 * 
	 */
	public boolean isStale() {
//		LogTools.info(
//				logger, 
//				"in isStale() with time of " + ( (System.currentTimeMillis() - lastAccessed) / 1000) + " seconds"
//			);
		
		return (System.currentTimeMillis() - lastAccessed) > sessionTimeout;
	}
	
	
	/* -------------------------- FacetEventListener interface ------------------ */
	
	/**
	 * Received from Bindings sending FacetEvents
	 */
	public void facetEvent(FacetEvent event) {

		if (trace) {
			logger.info("Got facet event: " + event);
		}
		
		// add to outbound queue
		synchronized (outboundQueue) {
			outboundQueue.add(event);
			if (trace) {
				logger.info("Event addded to queue: " + outboundQueue.size());
			}
			
			// if the queue is larger than allowed, drop the oldest event
			if (outboundQueue.size() > maxQueueSize) {
				MeemEvent eventObject = outboundQueue.remove(0);
				if (trace) {
					logger.info("Outbound queue larger than " + maxQueueSize + ". Removed " + eventObject);
				}
			}
			outboundQueue.notifyAll();			
		}		
	}


	/* ------------------- FacetHealthEventListener interface -------------------- */
	
	/**
	 * 
	 */
	public void facetHealthEvent(FacetHealthEvent event) {
		if (trace) {
			logger.info("Got facet health event: " + event);
		}

		synchronized (outboundQueue) {
			outboundQueue.add(event);
			
			if (trace) {
				logger.info("HealthEvent addded to queue: " + outboundQueue.size());
			}
			
			// if the queue is larger than allowed, drop the oldest event
			if (outboundQueue.size() > maxQueueSize) {
				MeemEvent eventObject = outboundQueue.remove(0);
				if (trace) {
					logger.info("Outbound queue larger than " + maxQueueSize + ". Removed " + eventObject);
				}
			}
			outboundQueue.notifyAll();			
		}
	}
	
	
	/* ----------------------------- other methods ------------------------------- */
	
	/**
	 * 
	 * @param meemPath
	 * @param facetId
	 */
	private OutboundHandler<?> getOutboundBinding(String meemPath, String facetId, String classname) {

		FacetKey        key     = new FacetKey(meemPath, facetId);		
		OutboundHandler<?> binding = null;
		
		synchronized (outboundBindings) {
			binding = outboundBindings.get(key);
			
			/* HACK force initial content from binding even if it exists */
			// TODO work out how to send initial content.  then we can reuse the binding
			if (binding != null) {
				outboundBindings.remove(key);
				binding.cleanup();
				binding = null;
			}
			/* end HACK */
			
			if (binding == null) {
				// create binding
				try {
					Class<?> facetClass =  ObjectUtility.getClass(classname);				// get Class from classname
					MeemPath path       = MeemPathHelper.toMeemPath(meemPath);	// get MeemPath from meempath String
	
					binding = new OutboundHandler(subject, path, facetId, facetClass.asSubclass(Facet.class));
					binding.addFacetEventListener(this);
					binding.setFacetHealthListener(this);
					outboundBindings.put(key, binding);
				}
				catch (URISyntaxException ex) {
					logger.info("MeemPath URI not valid: \"" + meemPath + "\"");				
				}
				catch (ClassNotFoundException ex) {
					logger.info("Could not locate class \"" + classname + "\"");
				}
				catch (ClassCastException ex) {
					logger.info("Class is not a Facet \"" + classname + "\"");
				}
			}
			else {
				if (trace) {
					logger.info("using existing binding: " + binding);
				}
			}
		}
		return binding;
	}

	/**
	 *
	 * @param meemPath
	 * @param facetId
	 * @param classname
	 */
	private InboundHandler getInboundBinding(String meemPath, String facetId, String classname) {

		FacetKey       key     = new FacetKey(meemPath, facetId);
		InboundHandler binding = null;
		
		synchronized (inboundBindings) {
			binding = (InboundHandler) inboundBindings.get(key);

			if (binding == null) {
				// create binding
				try {
					Class<?> facetClass = ObjectUtility.getClass(classname);			// get Class from classname
					MeemPath path = MeemPathHelper.toMeemPath(meemPath);	// get MeemPath from meempath String
					
					binding = new InboundHandler(subject, path, facetId, facetClass.asSubclass(Facet.class));
					binding.setFacetHealthListener(this);
					inboundBindings.put(key, binding);
				}
				catch (URISyntaxException ex) {
					logger.info("MeemPath URI not valid: \"" + meemPath + "\"");				
				}
				catch (ClassNotFoundException ex) {
					logger.info("Could not locate class \"" + classname + "\"");
				}
				catch (ClassCastException ex) {
					logger.info("Class is not a Facet \"" + classname + "\"");
				}
			}
		}
		return binding;
	}

	/**
	 * Increments the receiveCount so that threads in "receive()" will be knocked out. 
	 */
	private void knockExisting() {
		synchronized (outboundQueue) {
			// increment receiveCount to stop previous threads in "receive" from continuing
			receiveCount++;
			if (receiveCount > 1) {
				outboundQueue.notifyAll();
				try {
					outboundQueue.wait(1000);
				}
				catch (InterruptedException e) {
					running = false;
				}
			}
		}
	}

	
	/**
	 * Keeps the session fresh by setting the last modified time to the current time.
	 *
	 */
	public void touch() {
		lastAccessed = System.currentTimeMillis();
	}
}
