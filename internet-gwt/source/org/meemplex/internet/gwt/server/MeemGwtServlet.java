package org.meemplex.internet.gwt.server;

import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.meemplex.internet.gwt.client.gwtrpc.MeemGwtRpcService;
import org.meemplex.internet.gwt.shared.ErrorEvent;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetHealthEvent;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.KnockOutEvent;
import org.meemplex.internet.gwt.shared.MeemEvent;
import org.meemplex.internet.gwt.shared.NewSessionException;
import org.openmaji.implementation.rpc.server.SessionHandler;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Handles incoming RPC calls.
 * 
 * Includes a monitor that iterates through the sessionHandlers and destroyes stale handlers.
 * 
 * @author Warren Bloomer
 *
 */
public class MeemGwtServlet extends RemoteServiceServlet implements MeemGwtRpcService {
	private static final long serialVersionUID = 0L;

	private static final Logger logger = Logger.getAnonymousLogger();
	
	public static final String PROPERTY_SESSION_TIMEOUT = "sessionTimeout";
	
	public static final String PROPERTY_LOG_TRACE = "logTrace";
	
	private HashMap<String, SessionHandler> sessionHandlers = new HashMap<String, SessionHandler>();
	
	private SessionMonitor sessionMonitor = new SessionMonitor();

	private long sessionTimeout = 60000;

	private boolean trace = false;
	
	/**
	 * Constructor
	 *
	 */
	public MeemGwtServlet() {
	    //Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    
	    
	    String sessionTimeout = config.getInitParameter(PROPERTY_SESSION_TIMEOUT);
	    if (sessionTimeout != null) {
	    	try {
		    	Long seconds = Long.parseLong(sessionTimeout);
		    	setSessionTimeout(seconds * 1000);
	    	}
	    	catch (Exception e) {
	    	}
	    }
	    
	    String trace = config.getInitParameter(PROPERTY_LOG_TRACE);
	    if (trace != null) {
	    	try {
	    		setTrace(Boolean.parseBoolean(trace));
	    	}
	    	catch (Exception e) {
	    	}
	    }
	    
		if (this.trace) {
			logger.info("starting MeemGwtService at: " + config.getServletContext().getContextPath() + " - " + config.getServletContext().getRealPath("/"));
		}

	    start();
	}
	
	@Override
	public void destroy() {
	    stop();
	    cleanup();
	    super.destroy();
	}

	/**
	 * 
	 */
	protected void start() {
		sessionMonitor.start();		
	}
	
	/**
	 * 
	 */
	protected void stop() {
		sessionMonitor.stop();
	}
	
	protected void cleanup() {
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
	
	protected void setTrace(boolean trace) {
		this.trace = trace;
	}

	protected void setSessionTimeout(long timeout) {
		this.sessionTimeout = timeout;
	}
	
	/* ------------------------- RPC Methods ----------------------- */
	
	@Override
	public void send(MeemEvent[] events) throws NewSessionException {
		// get sessionHandler, create if not yet existant
		SessionHandler sessionHandler = getSessionHandler(getSessionId(), true);

		JSONParser parser=new JSONParser();
		
		for (MeemEvent meemEvent : events) {
			if (meemEvent instanceof FacetEvent) {
				FacetEvent facetEvent = (FacetEvent) meemEvent;
				if (trace) {
					logger.log(Level.INFO, "incoming event: \"" + facetEvent + "\"");
				}
				try {
					
					// convert prarms to serializable objects: Map, List, String, Number, Boolean
					String[] params = facetEvent.getParams();
					Serializable[] serializableParams = new Serializable[params.length];
					int i=0;
					for (String param : params) {
						try {
							serializableParams[i++] = (Serializable) parser.parse(param);
						}
						catch (ParseException e) {
							logger.info("Could not parse JSON value: " + param);
						}
					}
					
					FacetReference to = facetEvent.getTo();
					sessionHandler.send(
							to.getMeemPath(), 
							to.getFacetId(), 
							to.getFacetClass(),
							facetEvent.getMethod(),
							serializableParams 
						);
				}
				catch (RuntimeException e) {
					if (trace) { 
						logger.log(Level.INFO, "Got runtime exception in \"send()\"", e);
					}
					throw e;
				}
			}
		}
	}
	
	/* (non-Javadoc)
     * @see org.meemplex.internet.gwt.server.MeemRpcService#receiveMany(java.lang.String)
     */
	@Override
    public MeemEvent[] receive() throws NewSessionException {
		String sessionId = getSessionId();

		if (trace) {
			logger.log(Level.INFO, "receive: \"" + sessionId + "\"");
		}
		
		List<MeemEvent> events = new ArrayList<MeemEvent>(); ;

		try {
			SessionHandler sessionHandler = getSessionHandler(sessionId, true);
			
			/*
			org.openmaji.rpc.binding.MeemEvent meemEvent = sessionHandler.receive();
			if (sessionHandler.isAlive()) {
				MeemEvent event = EventTranslator.toServiceEvent(meemEvent);
				events.add(event);
			}
			else {
				invalidateSession();
			}
			*/
			List<org.openmaji.rpc.binding.MeemEvent> meemEvents = sessionHandler.receiveMany();
			if (sessionHandler.isAlive()) {
				for (org.openmaji.rpc.binding.MeemEvent meemEvent : meemEvents) {
					// convert Meemplex MeemEvents to GWT service MeemEvents
					MeemEvent event = EventTranslator.toServiceEvent(meemEvent);
					events.add(event);
					
					if (trace) {
						logger.log(Level.INFO, "outgoing event: \"" + event + "\"");
					}
				}
			}
			else {
				invalidateSession();
			}
		}
		catch (RuntimeException ex) {
			if (trace) {
				logger.log(Level.INFO, "Got runtime exception in \"receive()\"", ex);
			}
			throw ex;
		}
		
		return events.toArray(new MeemEvent[]{});
	}
	
	@Override
	public void registerDependency(FacetReference facetReference) throws NewSessionException {
		String sessionId = getSessionId();
		if (trace) {
			logger.log(Level.INFO, "Session " + sessionId + ", registering dependency on facet: " + facetReference.getMeemPath() + ":" + facetReference.getFacetId());
		}
		
		try {
			SessionHandler sessionHandler = getSessionHandler(sessionId, true);
			
			switch (facetReference.getDirection()) {
			// inbound to Meem
			case Inbound:
				sessionHandler.registerOutbound(facetReference.getMeemPath(), facetReference.getFacetId(), facetReference.getFacetClass());			
				break;
	
			// outbound from Meem
			case Outbound:
			default:
				sessionHandler.register(facetReference.getMeemPath(), facetReference.getFacetId(), facetReference.getFacetClass());
				break;
			}
		}
		catch (RuntimeException e) {
			if (trace) {
				logger.log(Level.INFO, "Got runtime exception in \"registerDependency()\"", e);
			}
			throw e;
		}
	}
	
	@Override
	public void removeDependency(FacetReference facetReference) throws NewSessionException {
		String sessionId = getSessionId();
		
		String meemPath = facetReference.getMeemPath(); 
		String facetId = facetReference.getFacetId();

		try {
			if (trace) {
				logger.log(Level.INFO, "deregistering dependency on facet: " + meemPath + ":" + facetId);
			}
			SessionHandler sessionHandler = getSessionHandler(sessionId, false);			
			if (sessionHandler != null) {
				sessionHandler.deregister(meemPath, facetId);
			}
		}
		catch (RuntimeException ex) {
			if (trace) {
				logger.log(Level.INFO, "Got runtime exception in \"deregister()\"", ex);
			}
			throw ex;
		}
	}
	
	/* (non-Javadoc)
     * @see org.meemplex.internet.gwt.server.MeemRpcService#interrupt(java.lang.String)
     */
	@Override
    public void interrupt() 
		throws NewSessionException
	{
		String sessionId = getSessionId();
		
		if (trace) {
			logger.log(Level.INFO, "interrupting session: " + sessionId);
		}
		
		SessionHandler sessionHandler = getSessionHandler(sessionId, false);
		if (sessionHandler != null) {
			sessionHandler.interrupt();
		}
	}
	
	/**
	 * End the session.
	 */
	@Override
	public void bye() {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			endSession(session.getId());
			session.invalidate();
		}
	}

	@Override
	public void dummy(MeemEvent me, FacetEvent fe, FacetHealthEvent fhe, ErrorEvent ee, KnockOutEvent koe, ArrayList<MeemEvent> list) {
	    // ignore
	}
	
	/* ----------------------- utility methods ------------------------ */
	
	/**
	 * @throws NewSessionException if the session is newly created. This allows the client to cleanup and re-register dependencies on Facets.
	 */
    private String getSessionId() throws NewSessionException {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession();
		if (session.isNew()) {
			throw new NewSessionException(session.getId());
		}
		return session.getId();
    }

    private void invalidateSession() {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			if (trace) {
				logger.log(Level.INFO, "invalidating session: " + session.getId());
			}
			session.invalidate();
		}
    }
    
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
				sessionHandler = sessionHandlers.get(sessionId);
			}

			if (sessionHandler == null && create) {

				// do not create a session if no subject is present
				if (Subject.getSubject(AccessController.getContext()) == null) {
					throw new SecurityException("No subject in security context");
				}
				
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

	/* (non-Javadoc)
     * @see org.meemplex.internet.gwt.server.MeemRpcService#endSession(java.lang.String)
     */
    private void endSession(String sessionId) {	

		try {
			if (trace) {
				logger.log(Level.INFO, "ending session: \"" + sessionId + "\"");
			}

			SessionHandler sessionHandler = getSessionHandler(sessionId, false);
			
			if (sessionHandler != null) {
				synchronized (sessionHandlers) {
					sessionHandlers.remove(sessionId);
				}
				sessionHandler.cleanup();
			}
		}
		catch (RuntimeException ex) {
			logger.log(Level.INFO, "Got runtime exception in \"endSession()\"", ex);
			throw ex;
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
								if (trace) {
									logger.info("++++ cleaning up session: " + key + " ++++");
								}
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
				logger.log(Level.INFO, "Exception in SessionMonitor", ex);				
			}
		}
		
		public synchronized void start() {
			if (!running) {
				running = true;
				new Thread(this, "GwtRpc session monitor thread").start();
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
