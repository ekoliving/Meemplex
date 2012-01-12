/*
 * Created on 2/09/2004
 *
 */
package org.openmaji.implementation.rpc.server;

import java.io.Serializable;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.Subject;

import org.openmaji.rpc.binding.ErrorEvent;
import org.openmaji.rpc.binding.FacetEvent;
import org.openmaji.rpc.binding.FacetHealthEvent;
import org.openmaji.rpc.binding.MeemEvent;
import org.openmaji.rpc.binding.Names;
import org.openmaji.rpc.binding.NoSessionException;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * Handles incoming RPC calls.
 * 
 * Includes a monitor that iterates through the sessionHandlers and destroyes stale handlers.
 * 
 * @author Warren Bloomer
 *
 */
public class RpcHandler implements RpcMessageHandler {

	private static final Logger logger = LogFactory.getLogger();
	
	private HashMap<String, SessionHandler> sessionHandlers = new HashMap<String, SessionHandler>();
	
	private SessionMonitor sessionMonitor = new SessionMonitor();

	private long sessionTimeout = 60000;

	private boolean trace = false;
	
	/**
	 * Constructor
	 *
	 */
	public RpcHandler() {
		start();
	}

	/**
	 * 
	 */
	public void start() {
		sessionMonitor.start();		
	}
	
	/**
	 * 
	 */
	public void stop() {
		sessionMonitor.stop();
	}
	
	public void cleanup() {
		synchronized (sessionHandlers) {
			Object[] ids = sessionHandlers.keySet().toArray();
			for (int i=0; i<ids.length; i++) {
				Object key = ids[i];
				SessionHandler handler = (SessionHandler) sessionHandlers.remove(key);
				if ( handler != null ) {
					handler.cleanup();
					sessionHandlers.remove(key);
				}
			}
		}		
	}
	
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public void setSessionTimeout(long timeout) {
		this.sessionTimeout = timeout;
	}
	
	/* ------------------------- RPC Methods ----------------------- */
	
	/**
	 * Start a new client session.
	 * A client may send a sessionId.  If the sessionId exists, that session is
	 * 
	 */
	public String beginSession(String sessionId) {
		if (trace) {
			LogTools.info(logger, "begin session: \"" + sessionId + "\"");
		}

		try {
			SessionHandler sessionHandler = getSessionHandler(sessionId, true);
			
			//LogTools.info(logger, "beginning session: client \"" + sessionId + "\", server \"" + sessionHandler.getSessionId() + "\"");
			
			return sessionHandler.getSessionId();
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"beginSession()\"", ex);
			throw ex;
		}
	}

	/**
	 * Terminate a client session.
	 * 
	 * The session handler is cleaned up and destroyed.
	 * 
	 * @param sessionId
	 */
	public String endSession(String sessionId) {	

		try {
			if (trace) {
				LogTools.info(logger, "ending session: \"" + sessionId + "\"");
			}

			SessionHandler sessionHandler = getSessionHandler(sessionId, false);
			
			if (sessionHandler != null) {
				synchronized (sessionHandlers) {
					sessionHandlers.remove(sessionId);
				}
				sessionHandler.cleanup();
			}
			return "";
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"endSession()\"", ex);
			throw ex;
		}
	}

	/**
	 * Send a message to a Facet
	 * 
	 * @return Session identifier
	 */
	public String send (
			String sessionId,
			String meemPath, 
			String facetId,
			String classname,
			String methodName,
			Object[] args
		)
	{
		try {
			if (trace) { 
				LogTools.info(logger, "sending message for " + sessionId + ": " + facetId + "." + methodName);
			}
			
			// check if session created, and create if not.
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);

			if (sessionHandler == null) {
				//throw new NoSessionException("No session: \"" + sessionId + "\"");
				return "";
			}
			
			sessionHandler.send(meemPath, facetId, classname, methodName, args);
	
			return sessionHandler.getSessionId();
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"send()\"", ex);
			throw ex;
		}
	}

	/**
	 * To receive the message intended for the session.
	 * These messages will be things like Facet events or health events.
	 */
	public Map<String, Object> receive (String sessionId)
		throws NoSessionException
	{
		try {
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);

			Map<String, Object> result = new Hashtable<String, Object>();
			if (sessionHandler == null) {
				result.put("error", "no session");
			}
			else {
				MeemEvent event = sessionHandler.receive();
				eventToHashMap(event, result);
			}
			
			return result;
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"receive()\"", ex);
			throw ex;
		}
	}
	
	/**
	 * Receive all the events in the queue outbound from Maji.
	 * 
	 * @param sessionId
	 * @return a vector of hashtables representing Maji events.
	 */
	public List<Map<String, Object>> receiveMany(String sessionId) {
		if (trace) {
			LogTools.info(logger, "receiveMany: \"" + sessionId + "\"");
		}

		try {
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);

			List<Map<String, Object>> result = new Vector<Map<String, Object>>();
			if (sessionHandler == null) {
				//throw new NoSessionException("No session: \"" + sessionId + "\"");
				Hashtable<String, Object> object = new Hashtable<String, Object>();
				object.put("error", "no session");
				result.add(object);
			}
			else {
				List<MeemEvent> events = sessionHandler.receiveMany();
				if (events != null) {
					for (MeemEvent event : events) {
						Map<String, Object> map = new Hashtable<String, Object>();
						eventToHashMap(event, map);
						result.add(map);
					}
				}
			}
			return result;
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"receive()\"", ex);
			throw ex;
		}
	}
	
	/**
	 * Register interest in an outbound facet
	 */
	public String register (
			String sessionId, 		
			String meemPath, 
			String facetId,
			String classname
		) 
	{
		try {
			if (trace) {
				LogTools.info(logger, "Session " + sessionId + ", registering facet: " + meemPath + ":" + facetId);
			}
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);
			if (sessionHandler == null) {
				return "";
			}
			sessionHandler.register(meemPath, facetId, classname);
			return sessionHandler.getSessionId();
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"register()\"", ex);
			throw ex;
		}
	}
	
	/**
	 * De-register interest in a Facet
	 */
	public String deregister (
			String sessionId, 		
			String meemPath, 
			String facetId
		)
	{
		try {
			if (trace) {
				LogTools.info(logger, "deregistering facet: " + meemPath + ":" + facetId);
			}
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);			
			if (sessionHandler == null) {
				return null;
			}
			sessionHandler.deregister(meemPath, facetId);
			return sessionHandler.getSessionId();
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"deregister()\"", ex);
			throw ex;
		}
	}

	public String registerOutbound (
			String sessionId, 		
			String meemPath, 
			String facetId,
			String classname
		) 
	{
		try {
			if (trace) {
				LogTools.info(logger, "Session " + sessionId + ", registering facet: " + meemPath + ":" + facetId);
			}
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);
			if (sessionHandler == null) {
				return "";
			}
			sessionHandler.registerOutbound(meemPath, facetId, classname);			
			return sessionHandler.getSessionId();
		}
		catch (RuntimeException ex) {
			LogTools.info(logger, "Got runtime exception in \"registerOutbound()\"", ex);
			throw ex;
		}
	}
	
	/**
	 * Interrupt a blocking receive call.
	 * 
	 * @param sessionId
	 */
	public String interrupt(String sessionId) 
		throws NoSessionException
	{
		if (trace) {
			LogTools.info(logger, "interrupting session: " + sessionId);
		}
		
		SessionHandler sessionHandler = getSessionHandler(sessionId, false);
		
		if (sessionHandler == null) {
//			throw new NoSessionException("No session: \"" + sessionId + "\"");
			return "";
		}
		
		sessionHandler.interrupt();
		return sessionHandler.getSessionId();
	}
	
	/* ----------------------- utility methods ------------------------ */
	
	/**
	 * Returns the session for the given session identifier.
	 * 
	 * If sessionId is null, or if the session is not found, a new session is created.
	 * 
	 * @param sessionId
	 */
	private SessionHandler getSessionHandler(String sessionId, boolean create) {
		SessionHandler sessionHandler = null;

		synchronized (sessionHandlers) {
			if (sessionId != null) {
				sessionHandler = (SessionHandler) sessionHandlers.get(sessionId);
			}

			if (sessionHandler == null && create) {

				// do not create a session if no subject is present
				if (Subject.getSubject(AccessController.getContext()) == null) {
					throw new SecurityException("No subject");
				}
				
				sessionId      = newSessionId();
				sessionHandler = new SessionHandler(sessionId);
				sessionHandler.setSessionTimeout(sessionTimeout);
				sessionHandlers.put(sessionId, sessionHandler);
			}
		}

		if (sessionHandler != null) {
			
			// check if proper subject for the session has been used
//			if ( !subject.equals(sessionHandler.getSubject()) ) {
//				throw new SecurityException("Subjects do not match");
//			}
			sessionHandler.touch();		// keep the session fresh
		}
		
		return sessionHandler;
	}

	/**
	 * 
	 */
	private String newSessionId() {
		String sessionId = IdGenerator.newId();

		synchronized (sessionHandlers) {
			// ensure there are no clashes in session ids
			while (sessionHandlers.get(sessionId) != null) {
				sessionId = IdGenerator.newId();
			}
		}
		return sessionId;
	}
	
	/**
	 * Turns the event into a HashMap
	 * 
	 * @param event
	 * @param result
	 */
	private void eventToHashMap(MeemEvent event, Map<String, Object> result) {
		result.put(Names.FacetEvent.EVENTTYPE, event.getEventType());

		if (event instanceof FacetEvent) {
			FacetEvent facetEvent = (FacetEvent) event;

			result.put(Names.FacetEvent.MEEMPATH,  facetEvent.getMeemPath());
			result.put(Names.FacetEvent.FACETID,   facetEvent.getFacetId());
			result.put(Names.FacetEvent.CLASSNAME, facetEvent.getFacetClass());
			result.put(Names.FacetEvent.METHOD,    facetEvent.getMethod());
			result.put(Names.FacetEvent.PARAMS,    facetEvent.getParams());
		}
		else if (event instanceof FacetHealthEvent) {
			FacetHealthEvent facetHealthEvent = (FacetHealthEvent) event;
			
			String lifeCycleState = "";
			if (facetHealthEvent.getLifeCycleState() != null) {
				lifeCycleState = facetHealthEvent.getLifeCycleState();
			}
			
			result.put(Names.FacetEvent.MEEMPATH,  facetHealthEvent.getMeemPath());
			result.put(Names.FacetEvent.FACETID,   facetHealthEvent.getFacetId());
			result.put(Names.FacetEvent.CLASSNAME, facetHealthEvent.getFacetClass());
			result.put(Names.FacetHealthEvent.BINDINGSTATE,   new Integer(facetHealthEvent.getBindingState()));
			result.put(Names.FacetHealthEvent.LIFECYCLESTATE, lifeCycleState);
		}
		else if (event instanceof ErrorEvent) {
			ErrorEvent errorEvent = (ErrorEvent) event;
			result.put(Names.ErrorEvent.MESSAGE, errorEvent.getMessage());
		}
	}
	
	/* -------------- methods and properties for random string creation ----------- */


	
	/**
	 * Monitors for stale sessions and cleans them up if they are stale.
	 */
	class SessionMonitor implements Runnable {
		
		private boolean running = false;
		private long timeout = 10000;

		public synchronized void run() {
			try {
				while (running) {
					synchronized (sessionHandlers) {
						Object[] ids = sessionHandlers.keySet().toArray();
						for (int i=0; i<ids.length; i++) {
							Object key = ids[i];
							SessionHandler handler = (SessionHandler) sessionHandlers.get(key);
							if ( (handler != null) && handler.isStale() ) {
//								LogTools.info(logger, "++++ cleaning up session: " + key + " ++++");
								handler.cleanup();
								sessionHandlers.remove(key);
							}
						}
					}
					
					try {
						wait(timeout);
					}
					catch (InterruptedException ex) {
//						LogTools.info(logger, "++++ session monitor interrupted ++++");
						running = false;
					}
				}
			}
			catch (Exception ex) {
				LogTools.info(logger, "Exception in SessionMonitor", ex);				
			}
		}
		
		public synchronized void start() {
			if (!running) {
				running = true;
				new Thread(this, "Session monitor thread").start();
			}
			else {
				notify();
			}
		}
		
		public synchronized void stop() {
			running = false;
			notify();
		}
	}
 }
