package org.meemplex.internet.rs;

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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.meemplex.internet.gwt.server.EventTranslator;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetReference;
import org.meemplex.internet.gwt.shared.MeemEvent;
import org.meemplex.internet.gwt.shared.NewSessionException;
import org.openmaji.implementation.rpc.server.SessionHandler;

/**
 * Handles incoming RPC calls.
 * 
 * Includes a monitor that iterates through the sessionHandlers and destroyes stale handlers.
 * 
 * @author Warren Bloomer
 *
 */

@Path("facet")
public class FacetResource  {
	private static final long serialVersionUID = 0L;

	private static final Logger logger = Logger.getAnonymousLogger();
	
	public static final String PROPERTY_SESSION_TIMEOUT = "sessionTimeout";
	
	public static final String PROPERTY_LOG_TRACE = "logTrace";
	
	private HashMap<String, SessionHandler> sessionHandlers = new HashMap<String, SessionHandler>();
	
	private SessionMonitor sessionMonitor = new SessionMonitor();

	private long sessionTimeout = 60000;

	private boolean trace = true;
	
	/**
	 * Constructor
	 *
	 */
	public FacetResource() {
	    //Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
	}

	public void init(ServletConfig config) throws ServletException {
	    
	    try {
	    	logger.info("this is session handler class: " + Class.forName("org.openmaji.implementation.rpc.server.SessionHandler"));
	    	logger.info("this is service interface: " + Class.forName("org.meemplex.internet.gwt.client.gwtrpc.MeemGwtRpcService"));
	    }
	    catch (Exception e) {
	    	logger.log(Level.INFO, "Could not load class", e);
	    }
	    
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
	
	public void destroy() {
	    stop();
	    cleanup();
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

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void send(@Context HttpServletRequest request, List<MeemEvent> events) throws NewSessionException {
		// get sessionHandler, create if not yet existant
		SessionHandler sessionHandler = getSessionHandler(getSessionId(request), true);

		for (MeemEvent meemEvent : events) {
			if (meemEvent instanceof FacetEvent) {
				FacetEvent facetEvent = (FacetEvent) meemEvent;
				if (trace) {
					logger.log(Level.INFO, "incoming event: \"" + facetEvent + "\"");
				}
				try {
					FacetReference to = facetEvent.getTo();
					sessionHandler.send(
							to.getMeemPath(), 
							to.getFacetId(), 
							to.getFacetClass(),
							facetEvent.getMethod(),
							 facetEvent.getParams() 
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
	@GET
	@Produces(MediaType.APPLICATION_JSON)
    public List<MeemEvent> receive(@Context HttpServletRequest request) throws NewSessionException {
		String sessionId = getSessionId(request);

		if (trace) {
			logger.log(Level.INFO, "receive: \"" + sessionId + "\"");
		}
		
		List<MeemEvent> events = new ArrayList<MeemEvent>(); ;

		try {
			SessionHandler sessionHandler = getSessionHandler(sessionId, true);
			
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
				invalidateSession(request);
			}
		}
		catch (RuntimeException ex) {
			if (trace) {
				logger.log(Level.INFO, "Got runtime exception in \"receive()\"", ex);
			}
			throw ex;
		}
		
		return events;
	}

	@POST
	@Path("register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerDependency(@Context HttpServletRequest request, FacetReference facetReference) throws NewSessionException {
		String sessionId = getSessionId(request);
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
	
	@POST
	@Path("remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeDependency(@Context HttpServletRequest request, FacetReference facetReference) throws NewSessionException {
		String sessionId = getSessionId(request);
		
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
	
	@POST
	@Path("interrupt")
	@Consumes(MediaType.APPLICATION_JSON)
    public void interrupt(@Context HttpServletRequest request) 
		throws NewSessionException
	{
		String sessionId = getSessionId(request);
		
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
	@POST
	@Path("bye")
	@Consumes(MediaType.APPLICATION_JSON)
	public void bye(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			endSession(session.getId());
			session.invalidate();
		}
	}

	
	/* ----------------------- utility methods ------------------------ */
	
	/**
	 * @throws NewSessionException if the session is newly created. This allows the client to cleanup and re-register dependencies on Facets.
	 */
    private String getSessionId(HttpServletRequest request) throws NewSessionException {
		HttpSession session = request.getSession();
		if (session.isNew()) {
			throw new NewSessionException(session.getId());
		}
		return session.getId();
    }

    private void invalidateSession(HttpServletRequest request) {
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
