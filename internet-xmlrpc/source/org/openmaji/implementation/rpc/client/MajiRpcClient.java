/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openmaji.implementation.rpc.binding.RpcClient;
import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.rpc.binding.FacetEvent;
import org.openmaji.rpc.binding.FacetEventListener;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.FacetHealthListener;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.rpc.binding.MeemEvent;
import org.openmaji.rpc.binding.Names;
import org.openmaji.rpc.binding.NoSessionException;
import org.openmaji.rpc.binding.OutboundBinding;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.XmlRpcException;


/**
 * This is a client for handling Maji XMLRPC communication.
 * 
 * queue up unregistered facets until they are registered. periodically try to re-register them
 * 
 * TODO (MAYBE) queue up outbound events until a sessionId is obtained
 * 
 * @author Warren Bloomer
 *
 */
public class MajiRpcClient implements RpcClient {

	public static final String METHOD_BEGIN = "rpc.beginSession";
	public static final String METHOD_SEND = "rpc.send";
	public static final String METHOD_RECEIVE = "rpc.receive";
	public static final String METHOD_RECEIVEMANY = "rpc.receiveMany";
	public static final String METHOD_REGISTER = "rpc.register";
	public static final String METHOD_REGISTER_OUTBOUND = "rpc.registerOutbound";
	public static final String METHOD_DEREGISTER = "rpc.deregister";
	public static final String METHOD_INTERRUPT = "rpc.interrupt";
	public static final String METHOD_END = "rpc.endSession";

	private final HashSet<FacetEventListener> facetEventListeners = new HashSet<FacetEventListener>();

	private final HashSet<FacetHealthListener> facetHealthListeners = new HashSet<FacetHealthListener>();

	private final HashSet<Object> unregisteredListeners = new HashSet<Object>();

	private final MyReceiver receiver = new MyReceiver();

	//private String address = null; // RPC server address e.g. "http://localhost:8080/maji/rpc"

	/**
	 * The XmlRpc Client
	 */
	private XmlRpcClient xmlrpc = null;;

	/**
	 * XmlRpcClient config
	 */
	private XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

	private String sessionId = "";

//	private String username = null;

//	private String password = null;
	
	/**
	 * A flag to determine whether debug logs are written
	 */
	private boolean debug = false;

	/** 
	 * The number of receive failures.  This gets reset to 0 when successfully receive
	 * events
	 */
	private int receiveErrorCount = 0;

	/** number of receive failure before sending OFFLINE health event */
	private int tolerableErrors = 5;

	/**
	 * Default constructor.
	 */
	public MajiRpcClient() {
	}

	public MajiRpcClient(String address, String username, String password) 
		throws MalformedURLException, RemoteException  
	{
		//this.address = address;
		//this.username = username;
		//this.password = password;
		
		config.setServerURL(new URL(address));
		config.setBasicUserName(username);
		config.setBasicPassword(password);
		
		//xmlrpc = new XmlRpcClient(address);
//		xmlrpc = new XmlRpcClient();
//		xmlrpc.setConfig(config);
		
		initialize();
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
/*
	public void createNewSession() 
		throws MalformedURLException, NoSessionException, RemoteException 
	{
		xmlrpc = new XmlRpcClient(address);
		xmlrpc.setBasicAuthentication(username, password);
		beginSession();
	}
	*/

	/**
	 * Set the adddress of the XMLRPC server.
	 * 
	 * @param address An HTTP URL string of the XMLRPC server.
	 */
	public void setAddress(URL address) 
		throws MalformedURLException, RemoteException 
	{
		// stop the receiver and restart.
		if (address != null && !address.equals(config.getServerURL())) {
			endSession();
			//this.address = address;
			config.setServerURL(address);
			//xmlrpc = new XmlRpcClient(address);
			initialize();
		}
	}
	
	public void setAddress(String address) throws MalformedURLException, RemoteException {
		setAddress(new URL(address));
	}

	/**
	 * 
	 * @return the address of the XMLRPC server.
	 */
	public URL getAddress() {
		return config.getServerURL();
	}

	/**
	 * Set the Maji username of the user to access the EdgeServer as.
	 * 
	 * @param username The Maji username.
	 */
	public void setUsername(String username) throws RemoteException 
	{
		if (username != null && !username.equals(config.getBasicUserName())) {
			endSession();
			//this.username = username;
			config.setBasicUserName(username);
			initialize();
		}
	}

	/**
	 * Set the Maji user's password.
	 * @param password user password.
	 */
	public void setPassword(String password) throws RemoteException 
	{
		if (password != null && !password.equals(config.getBasicPassword())) {
			endSession();
			config.setBasicPassword(password);
			initialize();
		}
	}

	/**
	 * Start the message receiver.
	 */
	public void start() {
		receiver.start();
	}

	/**
	 * Stop receiving messages from the server.
	 */
	public void stop() {
		receiver.stop();
	}

	/**
	 * Initialize the RPC client.
	 *
	 */
	private void initialize() throws RemoteException {
		if (xmlrpc == null) {
			xmlrpc = new XmlRpcClient();
			xmlrpc.setConfig(config);
		}

		// TEST
		start();
		
		try {
			beginSession();
		}
		catch (NoSessionException ex) {
			System.err.println("Could not begin session");
		}
	}

	/**
	 * Register interest in facets that are outbound from the server.
	 * A list of facets that we are interested in is determined from FacetEventListners that 
	 * have been added to this RPC client.
	 */
	private void registerInterest() throws RemoteException, NoSessionException {
		int retries = 10;

		synchronized (facetEventListeners) {
			unregisteredListeners.clear();
			unregisteredListeners.addAll(facetEventListeners);
			unregisteredListeners.addAll(facetHealthListeners);
			
			for (int j = 0; !unregisteredListeners.isEmpty() && j < retries; j++) {
				Object[] listeners = unregisteredListeners.toArray();
				for (int i = 0; i < listeners.length; i++) {
					Object listener = listeners[i];
					
					if (listener instanceof InboundBinding) {
						InboundBinding binding = (InboundBinding) listener;
						register(binding.getMeemPath(), binding.getFacetId(), binding.getFacetClass());
						unregisteredListeners.remove(binding);
					}
					else if (listener instanceof OutboundBinding) {
						OutboundBinding binding = (OutboundBinding) listener;
						registerOutbound(binding.getMeemPath(), binding.getFacetId(), binding.getFacetClass());
						unregisteredListeners.remove(binding);
					}
				}
			}
		}
	}

	void registerUnregisteredListeners() throws RemoteException {
		int retries = 10;

		synchronized (facetEventListeners) {
			for (int j = 0; !unregisteredListeners.isEmpty() && j < retries; j++) {
				Object[] listeners = unregisteredListeners.toArray();
				for (int i = 0; i < listeners.length; i++) {
					Object listener = listeners[i];
					
					if (listener instanceof InboundBinding) {
						InboundBinding binding = (InboundBinding) listener;

						// register interest for the {Meem, Facet} value pair
						try {
							register(binding.getMeemPath(), binding.getFacetId(), binding.getFacetClass());
							unregisteredListeners.remove(binding);
						}
						catch (NoSessionException e) {
							// ?
						}
					}
					else if (listener instanceof OutboundBinding) {
						OutboundBinding binding = (OutboundBinding) listener;
						try {
							registerOutbound(binding.getMeemPath(), binding.getFacetId(), binding.getFacetClass());
							unregisteredListeners.remove(binding);
						}
						catch (NoSessionException e) {
						}
					}
				}
			}
		}
	}

	/* ----------------- FacetEventReceiver interface ------------- */

	/**
	 * Received from Bindings sending FacetEvents
	 */
	public void facetEvent(FacetEvent event) throws RuntimeException {
		try {
			// make sure unregistered listeners are registered.
			registerUnregisteredListeners();
	
			// send the event over XML-RPC
			send(event.getMeemPath(), event.getFacetId(), event.getFacetClass(), event.getMethod(), event.getParams());
		}
		catch (NoSessionException e) {
			throw new RuntimeException(e);
		}
		catch (RemoteException e) {
			throw new RuntimeException(e);
		}
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
			unregisteredListeners.add(listener);

			/* TEST
			if (listener instanceof InboundBinding) {
				InboundBinding binding = (InboundBinding) listener;

				int retries = 10;
				boolean registered = false;
				for (int i = 0; i < retries && !registered; i++) {
					// register interest for the {Meem, Facet} value pair
					try {
						register(binding.getMeemPath(), binding.getFacetId(), binding.getFacetClass());
						registered = true;
						unregisteredListeners.remove(listener);
					}
					catch (NoSessionException e) {
						// ?
					}
					catch (RemoteException e) {
						// ?
					}
				}
			}
			*/
		}
		
		try {
			// attempt to register any currently unregistered entries
			registerUnregisteredListeners();
		}
		catch (RemoteException e) {
			// ?
		}
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

			try {
				// deregister interest for the {Meem, Facet} value pair				
				deregister(binding.getMeemPath(), binding.getFacetId());
			}
			catch (RemoteException e) {
				// ?
			}
		}
	}

	/* ---------------- FacetHealthEventSender interface ----------------- */

	public void addFacetHealthListener(FacetHealthListener listener) {

		synchronized (facetHealthListeners) {
			//System.out.println("!!! adding health listener: " + listener);
			facetHealthListeners.add(listener);
			unregisteredListeners.add(listener);
		}

		try {
			// attempt to register any currently unregistered entries
			registerUnregisteredListeners();
		}
		catch (RemoteException e) {
			// ?
		}
	}

	public void removeFacetHealthListener(FacetHealthListener listener) {

		synchronized (facetHealthListeners) {
			//System.out.println("!!! adding health listener: " + listener);
			facetHealthListeners.remove(listener);
		}

		// only remove outbound bindins.  an Inbound binding will be removed when it
		// is removed as a FacetEventListener
		if (listener instanceof OutboundBinding) {
			OutboundBinding binding = (OutboundBinding) listener;
			try {
				// deregister interest for the {Meem, Facet} value pair				
				deregister(binding.getMeemPath(), binding.getFacetId());
			}
			catch (RemoteException e) {
				// ?
			}
		}
	}

	/* -------------------- ------------------ */

	/**
	 * Starts a new server session, interrupts any activity for the old session.
	 * Interest in facets outbound from the server is registered.
	 */
	protected void beginSession() throws RemoteException, NoSessionException {
		if (xmlrpc == null) {
			throw new NoSessionException("XML-RPC object is null");
		}

		//		if (username != null && password != null) {
		//			xmlrpc.setBasicAuthentication(username, password);
		//		}

		// call begin remote method
		Vector<Object> params = new Vector<Object>();
		params.add(this.sessionId);
		Object id = invoke(METHOD_BEGIN, params);

		if (debug) {
			System.err.println("beginSession: old sessionId=" + sessionId + " - new sessionId=" + id);
		}
		
		// only authenticate on "beginSession"
		//		xmlrpc.setBasicAuthentication(null, null);

		if (id != null && !"".equals(id)) {
			// session id has changed
			String oldId = this.sessionId;
			this.sessionId = (String) id;
			if ( !(oldId == null || "".equals(oldId)) ) {
				interrupt(oldId); // interrupt the request on the old id
			}
			start();
			registerInterest(); // register interest in facets outbound from server
		}
		else {
			throw new NoSessionException("No Session ID given by server.");
		}
	}

	/**
	 * Finish a server session.
	 */
	protected void endSession() {

		if ( sessionId == null || "".equals(sessionId) ) {
			// if there is no sessionId, return
			return;
		}
		
		//Vector<Object> params = new Vector<Object>();
		//params.add(sessionId);
		//invoke(METHOD_END, params);

		stop();
	}

	/**
	 * Register interest in an Outbound Facet on the Meem Server.
	 * 
	 * @param meemPath The path of the meem whose facet we are interested in.
	 * @param facetId the identifier of the facet.
	 * @param facetClass The class of the facet.
	 */
	protected void register(String meemPath, String facetId, Class<? extends Facet> facetClass) 
		throws NoSessionException, RemoteException 
	{
		Vector<Object> params = new Vector<Object>();
		params.add(sessionId);
		params.add(meemPath);
		params.add(facetId);
		params.add(facetClass.getName());
		Object id = invoke(METHOD_REGISTER, params);

		if (id == null || "".equals(id)) {
			if (debug) {
				System.err.println("No session to register interest in facet, trying new session");
			}
			beginSession();
				
			// try invoking one more time
			params.set(0, sessionId);
			id = invoke(METHOD_REGISTER, params);
			if (id == null || "".equals(id)) {
				throw new NoSessionException("Could not register interest in facet");
			}
		}
	}
	
	protected void registerOutbound(String meemPath, String facetId, Class<? extends Facet> facetClass) 
		throws NoSessionException, RemoteException 
	{
		Vector<Object> params = new Vector<Object>();
		params.add(sessionId);
		params.add(meemPath);
		params.add(facetId);
		params.add(facetClass.getName());
	
		Object id = invoke(METHOD_REGISTER_OUTBOUND, params);

		if (id == null || "".equals(id)) {
			System.err.println("Could not register interest in facet");
			throw new NoSessionException("Could not register interest in facet");
		}
	}
	
	/**
	 * De-register interest in a facet outbound from a meem on the server.
	 * 
	 * @param meemPath The path of the meem whose facet we are interested in.
	 * @param facetId The identifier of the facet.
	 */
	protected void deregister(String meemPath, String facetId) throws RemoteException {
		Vector<Object> params = new Vector<Object>();
		params.add(sessionId);
		params.add(meemPath);
		params.add(facetId);

		String id = (String) invoke(METHOD_DEREGISTER, params);

		if (id == null || "".equals(id)) {
			try {
				beginSession();
			}
			catch (NoSessionException ex) {
			}
		}
	}

	/**
	 * Send a message on a particular facet.
	 */
	protected void send(String meemPath, String facetId, String classname, String method, Object[] params) 
		throws RemoteException, NoSessionException
	{

		// insert meemPath, facetId and classname into params
		Vector<Object> args = new Vector<Object>();
		args.add(sessionId);
		args.add(meemPath);
		args.add(facetId);
		args.add(classname);
		args.add(method);
		args.add(params);

		Object id = invoke(METHOD_SEND, args);

		if (id == null || "".equals(id)) {
			// no session, try starting a new session.
			
			// TODO should we add the message on a queue ????
			if (debug) {
				System.err.println("No session to send message, trying new session");
			}
			beginSession();
				
			// try invoking one more time
			args.set(0, sessionId);
			id = invoke(METHOD_SEND, args);
			if (id == null || "".equals(id)) {
				throw new NoSessionException("Could not establish session to send message");
			}
		}
	}

	/**
	 * return object from the "receive" call is a "XML-RPC struct", i.e. a Hashtable
	 * The method returns a FacetEvent object including MeemPath, FacetId, Method, and Args
	 */
	protected MeemEvent receive() throws RemoteException {
		MeemEvent event = null;

		List<Object> receiveParams = new ArrayList<Object>();
		receiveParams.add(sessionId);

		if (debug) {
			System.out.println("reading result... ");
		}
		
		Map<String,Object> result = (Map<String,Object>) invoke(METHOD_RECEIVE, receiveParams);

		if (result == null || result.size() == 0) {
			return event;
		}
		
		if (debug) {
			System.out.println("read result: " + result);
		}

		// check if session error
		if (result.get("error") != null) {
			try {
				beginSession();
			}
			catch (NoSessionException ex) {
			}

			return event;
		}

		event = convert(result);
		
		/*
		String eventType = (String) result.get(FacetEvent.EVENTTYPE);

		String meemPath = (String) result.get(FacetEvent.MEEMPATH);
		String facetId = (String) result.get(FacetEvent.FACETID);
		String classname = (String) result.get(FacetEvent.CLASSNAME);

		Class<? extends Facet> facetClass = null;
		try {
			facetClass = (Class<? extends Facet>) Class.forName(classname);
		}
		catch (ClassNotFoundException ex) {
			System.err.println("Could not locate class: " + classname);
		}

		if (FacetEvent.NAME.equals(eventType)) {
			String method = (String) result.get(FacetEvent.METHOD);
			Object[] params = (Object[]) result.get(FacetEvent.PARAMS);

			event = new FacetEvent();
			((FacetEvent) event).setMeemPath(meemPath);
			((FacetEvent) event).setFacetId(facetId);
			((FacetEvent) event).setFacetClass(facetClass.getName());
			((FacetEvent) event).setMethod(method);
			((FacetEvent) event).setParams(params);
		}
		else if (FacetHealthEvent.NAME.equals(eventType)) {
			Integer bindingState = (Integer) result.get(FacetHealthEvent.BINDINGSTATE);
			String lifeCycleState = (String) result.get(FacetHealthEvent.LIFECYCLESTATE);

			event = new FacetHealthEvent();
			((FacetHealthEvent) event).setMeemPath(meemPath);
			((FacetHealthEvent) event).setFacetId(facetId);
			((FacetHealthEvent) event).setFacetClass(facetClass.getName());
			((FacetHealthEvent) event).setBindingState(bindingState.intValue());
			((FacetHealthEvent) event).setLifeCycleState(lifeCycleState);
			//System.out.println("!!! got facet health event: " + event);
		}
		*/

		return event;
	}

	
	private List<MeemEvent> receiveMany() throws RemoteException {
		List<MeemEvent> events = new ArrayList<MeemEvent>();
		
		Vector<Object> receiveParams = new Vector<Object>();
		receiveParams.add(sessionId);

		if (debug) {
			System.out.println("reading many result... ");
		}
				
		Object[] results = (Object[]) invoke(METHOD_RECEIVEMANY, receiveParams);

		if (results == null || results.length == 0) {
			return null;
		}

		for (Object res : results) {
			Map<String, Object> result = (Map<String, Object>)res;
			if (debug) {
				System.out.println("read result: " + result);
			}

			// check if session error
			if (result.get("error") != null) {
				try {
					beginSession();
				}
				catch (NoSessionException ex) {
				}
				return null;
			}
			MeemEvent event = convert(result);
			events.add(event);
		}
		
		return events;
		
	}
	
	private MeemEvent convert(Map<String, Object> result) {
		MeemEvent event = null;
		
		String eventType = (String) result.get(Names.FacetEvent.EVENTTYPE);
		String meemPath = (String) result.get(Names.FacetEvent.MEEMPATH);
		String facetId = (String) result.get(Names.FacetEvent.FACETID);
		String classname = (String) result.get(Names.FacetEvent.CLASSNAME);

		Class<? extends Facet> facetClass = null;
		try {
			facetClass = (Class<? extends Facet>) ObjectUtility.getClass(Facet.class, classname);
		}
		catch (ClassNotFoundException ex) {
			System.err.println("Could not locate class: " + classname);
		}

		if (Names.FacetEvent.NAME.equals(eventType)) {
			String method = (String) result.get(Names.FacetEvent.METHOD);
			Object[] params = (Object[]) result.get(Names.FacetEvent.PARAMS);

			event = new FacetEvent();
			((FacetEvent) event).setMeemPath(meemPath);
			((FacetEvent) event).setFacetId(facetId);
			((FacetEvent) event).setFacetClass(facetClass.getName());
			((FacetEvent) event).setMethod(method);
			((FacetEvent) event).setParams(params);
		}
		else if (Names.FacetHealthEvent.NAME.equals(eventType)) {
			Integer bindingState = (Integer) result.get(Names.FacetHealthEvent.BINDINGSTATE);
			String lifeCycleState = (String) result.get(Names.FacetHealthEvent.LIFECYCLESTATE);

			event = new FacetHealthEvent();
			((FacetHealthEvent) event).setMeemPath(meemPath);
			((FacetHealthEvent) event).setFacetId(facetId);
			((FacetHealthEvent) event).setFacetClass(facetClass.getName());
			((FacetHealthEvent) event).setBindingState(bindingState.intValue());
			((FacetHealthEvent) event).setLifeCycleState(lifeCycleState);
			//System.out.println("!!! got facet health event: " + event);
		}
		
		return event;
	}
	
	/**
	 * Interrupt a blocked receive call in the server
	 *
	 */
	protected void interrupt(String sessionId) throws RemoteException {
		Vector<Object> params = new Vector<Object>();
		params.add(sessionId);
		invoke(METHOD_INTERRUPT, params);
	}

	/**
	 * 
	 * @param method
	 * @param params
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private Object invoke(String method, List<Object> params) throws RemoteException {
		if (xmlrpc == null) {
			//System.err.println("Could not invoke \"" + method + "\" null rpc client");
			return null;
		}

		Object result = null;
		try {
			result = xmlrpc.execute(method, params);
		}
//		catch (IOException ex) {
//			System.err.println("IOException while invoking \"" + method + "\": " + ex.getMessage());
//			throw new RemoteException("Problem invoking : " + method, ex);
//		}
		catch (XmlRpcException ex) {
			Throwable cause = ex.getCause();
			if (cause == null) {
				System.err.println("XML-RPC Exception while invoking \"" + method + "\": " + ex.getMessage());
				ex.printStackTrace();
				throw new RemoteException("Problem invoking : " + method, ex);
			}
			else {
				System.err.println("Exception while invoking: " + cause.getMessage());
				throw new RemoteException("Problem invoking : " + method, cause);
			}
		}

		return result;
	}
	
	/*
	private void invokeAsync(String method, Vector<?> params) throws NoSessionException {
		if (xmlrpc == null) {
			throw new NoSessionException("null rpc client");
		}
		xmlrpc.executeAsync(method, params, new MyCallback());
	}
	*/
	 
	/**
	 * Send a Facet event to listeners
	 * @param event
	 */
	protected void sendEvent(FacetEvent event) {
		Object[] listenerArr = null;
		synchronized (facetEventListeners) {
			listenerArr = facetEventListeners.toArray();
		}
		for (int i = 0; i < listenerArr.length; i++) {
			FacetEventListener listener = (FacetEventListener) listenerArr[i];
			listener.facetEvent(event);
		}
	}

	protected void sendEvent(FacetHealthEvent event) {
		Object[] listenerArr = null;
		synchronized (facetHealthListeners) {
			listenerArr = facetHealthListeners.toArray();
		}
		for (int i = 0; i < listenerArr.length; i++) {
			FacetHealthListener listener = (FacetHealthListener) listenerArr[i];
			listener.facetHealthEvent(event);
		}
	}

	/**
	 * An object to contiuously make "receive" invocations to "pull"
	 * Facet events from the RPC server.
	 */
	private class MyReceiver implements Runnable {
		private Thread runningThread = null;

		public synchronized void run() {
			while ( Thread.currentThread().equals(runningThread)) {
				try {
					List<MeemEvent> events = receiveMany();
//					MeemEvent event = receive();

					if (debug) {
						System.err.println("Received events: " + events);
//						System.err.println("Received event: " + event);
					}
//					if (event != null) {
					if (events != null) {
						for (MeemEvent event : events) {

							receiveErrorCount = 0;
	
							if (event instanceof FacetEvent) {
								// send results to listeners
								FacetEvent facetEvent = (FacetEvent) event;
								sendEvent(facetEvent);
							}
							else if (event instanceof FacetHealthEvent) {
								FacetHealthEvent facetHealthEvent = (FacetHealthEvent) event;
								sendEvent(facetHealthEvent);
							}
							else {
								// unhandled event
							}
						}
					}
					else {
						try {
							// wait 1 second
							wait(1000);
						}
						catch (InterruptedException ex) {
							runningThread = null;
						}
					}
				}
				catch (Exception ex) {
					//if (debug) {
						System.err.println("Exception while receiving events: " + ex);
					//}

					receiveErrorCount++;
					if (receiveErrorCount > tolerableErrors) {
						if (debug) {
							System.err.println("Sending offline health state");
						}
						FacetHealthEvent facetHealthEvent = new FacetHealthEvent();
						facetHealthEvent.setBindingState(FacetHealthEvent.OFFLINE);
						sendEvent(facetHealthEvent);
					}
					
					try {
						// wait 5 seconds
						wait(5000);
					}
					catch (InterruptedException e) {
						runningThread = null;
					}
				}
			}
			if (debug) {
				System.err.println("Leaving xml-rpc reader thread");
			}
		}

		public void start() {
			if (runningThread == null) {
				synchronized (this) {
					runningThread = new Thread(this, "Receiver Thread");
					runningThread.start();
				}
			}
			else {
				try {
					interrupt(sessionId); // interrupt any existing receive thread
				}
				catch (RemoteException e) {
					// ?
				}
			}
		}

		public void stop() {
			synchronized (this) {
				runningThread = null;
				// interrupt the receive request
				try {
					interrupt(sessionId); // interrupt any existing receive thread
				}
				catch (RemoteException e) {
					// ?
				}
			}
		}
	}

	/**
	 * A callback to log errors in making RPC calls
	 */
	/*
	private class MyCallback implements AsyncCallback {
		public void handleResult(Object result, URL url, String s) {

		}

		public void handleError(Exception ex, URL url, String s) {
			System.err.println("Exception while invoking: " + ex);
		}
	}
	*/
}
