/*
 * @(#)SessionMeemFactoryClientWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.session;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.session.*;
import org.openmaji.meem.wedge.dependency.*;
import org.openmaji.meem.wedge.error.*;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.system.meem.session.SessionMeemFactory;
import org.openmaji.system.meem.session.SessionMeemFactoryClient;
import org.openmaji.system.request.RequestContext;
import org.openmaji.system.request.RequestCreationException;

/**
 * 
 */
public class SessionMeemFactoryClientWedge implements Wedge, SessionMeemFactoryClient {

	private static final Logger logger = LogFactory.getLogger();

	/** The context for request/response tracking */
	
	public RequestContext requestContext;
	
	/** The conduit for recieving request/response errors */
	
	public ErrorHandler requestErrorConduit = new RequestErrorConduitImpl();
	
	/** Outbound facet to make requests to the remote session factory instance */
	
	public SessionMeemFactory fSessionMeemFactory;
	
	/** Inbound conduit for requests to create server session meem instances.*/
	
	public SessionMeemCreator cSessionMeemCreatorConduit = new SessionMeemCreatorImpl();
	
	/** Outboudn conduit to return a new session meempath to the client */
	
	public SessionMeemCreatorClient cSessionMeemCreatorClientConduit;
	
	/** Outbound conduit for creating the dependencies to the remote factory */
	
	public DependencyHandler dependencyHandlerConduit;
	
	/** Inboudn conduit for receiving notification that a dependency has been
	 * created */
	
	public DependencyClient dependencyClientConduit = new DependencyClientImpl();
	/** Inbound lifecycle conduit */
	
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	/** Outbound lifecycle conduit */
	
	public Vote lifeCycleControlConduit;
	
	/** The factory meempath is stored so that when a dependency notification
	 * is returned, it is known  */
	
	private FactoryRequestObject mFactoryRequest = null;
	private Hashtable mCachedSessionMeems = new Hashtable();
	
	/*
	 * @see org.openmaji.system.meem.session
	 */
	
	public void sessionMeemCreated(MeemPath sessionmp) {
		
		if (mFactoryRequest != null) {
			
			String rc = (String) requestContext.get();
			if (!"createSessionMeem".equals(rc)) {
				return;
			}
			
			// complete the session meem creatoin request
			requestContext.end();
			
			// disconnect dependencies
			Iterator ita = mFactoryRequest.getDependencies();
			while (ita.hasNext()) {
				DependencyAttribute dat = (DependencyAttribute) ita.next();
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "Removing session meem factory dependency " + dat);
				}
				dependencyHandlerConduit.removeDependency(dat);
			}
			
			// Add the session meem to the cache 
			if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
				LogTools.trace(logger, Common.getLogLevel(), "caching newly created session meempath");
			}
			mCachedSessionMeems.put(mFactoryRequest.getMeemPath(), sessionmp);
			cSessionMeemCreatorClientConduit.sessionMeemCreated(sessionmp);
						
		}
		else {
			LogTools.warn(logger, "unexpected session meem created");
			cSessionMeemCreatorClientConduit.sessionMeemCreationError(new MajiException("Unexpected session meem creation reported"));
			
		}
	}
	
	/*
	 * @see org.openmaji.system.meem.session
	 */
	
	public void sessionMeemDestroyed(MeemPath mp) {
		
		// Ignored at the moment
		
	}
	
	/**
	 * Private inneer class for handling request/response errors
	 */
	
	private final class RequestErrorConduitImpl implements ErrorHandler {
		
		public void thrown(Throwable e) {
			
			String context = (String) requestContext.get();
			
			if (context == null) {
				// error coming from another wedge - ignore
				return;
			}
			
			if ("createSessionFactoryDependencies".equals(context)) {
				
				LogTools.error(logger, "Failed to create session factory dependencies: " + e.getMessage());
				cSessionMeemCreatorClientConduit.sessionMeemCreationError(new MajiException("Error setting up dependencies remote session factory", e));
			}
			else if ("createSessionMeem".equals(context)) {

				LogTools.error(logger, "Failed to create session meem: " + e.getMessage());
				cSessionMeemCreatorClientConduit.sessionMeemCreationError(new MajiException("Error creating remote session meem instance", e));

			}
			else {
				LogTools.error(logger, "Unknown request error caught: " + context + " " + e.getMessage());
				
			}
		}
		
	}
	
	/**
	 * Private inner class to handle incoming requests on the session 
	 * meem creator conduit.
	 */
	
	private final class SessionMeemCreatorImpl implements SessionMeemCreator {
		
		/*
		 *  (non-Javadoc)
		 * @see org.openmaji.meem.session.SessionMeemCreator#createServerSessionMeem(org.openmaji.meem.MeemPath)
		 */
		
		public void createServerSessionMeem(MeemPath factorymp) {
			
			// Check to see if the factory meempath has an already  
			// associated session meem from a previous request. If
			// so, return the existing meempath reference.
			
			if (mFactoryRequest != null) {
				
				MeemPath sessionmp = (MeemPath) mCachedSessionMeems.get(factorymp);
				if (sessionmp != null) {
					
					if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
						LogTools.trace(logger, Common.getLogLevel(), "retrieving cached session meem");
					}
					cSessionMeemCreatorClientConduit.sessionMeemCreated(sessionmp);
				}
				else if (mFactoryRequest.getMeemPath().equals(factorymp)) {
					
					// request is pending, so do not queue another request
					return;
				}
				else {
					// Connection to another meem already established - throw an error to exit.
					if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
						LogTools.trace(logger, Common.getLogLevel(), "session meem creator client already connected to remote factory");
					}
					cSessionMeemCreatorClientConduit.sessionMeemCreationError(new MajiException("session meem creator client already connected to remote factory"));
					return;
				}
				
			}
			
			// Else create both dependencies to the remote factory
			else {

				try {
					requestContext.begin(60000, "createSessionFactoryDependencies");
					if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
						LogTools.trace(logger, Common.getLogLevel(), "Establishing dependencies to session meem");
					}
				}
				catch (RequestCreationException e) {
					
					cSessionMeemCreatorClientConduit.sessionMeemCreationError(new MajiException("Dependency Request creation error"));
					
				}
				
				// Create the current factory request
				
				mFactoryRequest = new FactoryRequestObject(factorymp);
				
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "setting up dependencies to session meem factory");
				}
				
                Meem factoryMeem = Meem.spi.get(factorymp);

				DependencyAttribute da1 = new 
					DependencyAttribute(DependencyType.WEAK, Scope.DISTRIBUTED, factoryMeem, "sessionMeemFactory");
				mFactoryRequest.addOutstandingDependency(da1);
			
				DependencyAttribute da2 = new 
					DependencyAttribute(DependencyType.WEAK, Scope.DISTRIBUTED, factoryMeem, "fSessionClient");
				mFactoryRequest.addOutstandingDependency(da2);
		
				dependencyHandlerConduit.addDependency("fSessionMeemFactory", da1, LifeTime.TRANSIENT);
				dependencyHandlerConduit.addDependency("sessionMeemFactoryClient", da2, LifeTime.TRANSIENT);
			
				// Store the dependency requests 
				
			
			}
				
		}
		
	}
	
	/**
	 * Private inner class used to store information on incoming factory requests.
	 */
	
	private final class FactoryRequestObject {
		
		private MeemPath mMeemPath;
		private List mOutstandingDependencies;
		private List mDependencies;
		
		/**
		 * Constructor thta takes the meempath to the remote session factory
		 * as argument
		 * @param mp
		 */
		
		public FactoryRequestObject(MeemPath mp) {
			mMeemPath = mp;
			mOutstandingDependencies = new Vector();
			mDependencies = new Vector();
		}
		
		/**
		 * returns the remote session factory meempath
		 * @return emote session factory meempath
		 */
		
		public MeemPath getMeemPath() {
			return mMeemPath;
		}
		
		/**
		 * Adds a depedency to the list which is informs the request
		 * object that there are dependency requests outstanding. This
		 * should be called once the dependency has been requested.
		 * 
		 * @param da attribute of the newly requested dependency 
		 */
		
		public void addOutstandingDependency(DependencyAttribute da) {
			
			mOutstandingDependencies.add(da);
			mDependencies.add(da);
		}

		/**
		 * Removes a depedency from the list which is informs the request
		 * object that there are dependency requests outstanding. This
		 * should be called once the dependency response has been recieved.
		 * 
		 * @param da attribute of the newly created dependency 
		 */
		
		public void removeOutstandingDependency(DependencyAttribute da) {
			
			boolean isgone = mOutstandingDependencies.remove(da);
			
			if (isgone == false) {
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "Dependency attribute could not be found for removal: " + da);
					LogTools.trace(logger,Common.getLogLevel(), "Number of attributes held: " + mOutstandingDependencies.size());
				}
			}
			else {
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "Outstanding dependency attribute removed: " + da);
				}
			}
			
		}
		
		/**
		 * Returns true if there are dependency requests outstanding
		 * @return true if requests outstandign, false otherwise
		 */
		
		public boolean hasOutstandingDependencies() {
			
			if (mOutstandingDependencies.size() == 0) {
				return false;
			}
			else {
				return true;
			}
		}
		
		/**
		 * Returns an iterator which provides a list of dependency attributes
		 * of all dependency connections held by the wedge.
		 */
		
		public Iterator getDependencies() {
			return mDependencies.iterator();
		}
		
	}
	
	/**
	 * Private inner class to handle incoming responses from the dependency
	 * wedge to notify that dependencies have been successfully added and
	 * removed.
	 */
	
	private final class DependencyClientImpl implements DependencyClient {
		
		/*
		 *  (non-Javadoc)
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyAdded(org.openmaji.meem.definition.DependencyAttribute)
		 */
		
		public void dependencyAdded(String facetId, DependencyAttribute depatt) {
		}
		/*
		 *  (non-Javadoc)
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyRemoved(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyConnected(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyConnected(DependencyAttribute depatt) {
			String rc = (String) requestContext.get();
			
//			if (mFactoryRequest != null && mFactoryRequest.mDependencies != null && mFactoryRequest.mDependencies.contains(depatt)) {
//				rc = "createSessionFactoryDependencies";
//			}

			if (!"createSessionFactoryDependencies".equals(rc)) {
				return;
			}
			else if (mFactoryRequest == null) {
				
				// Ignore, as refers a dependency response from another wedge
				
			}
			else {
				
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "Dependency connected to session factory..." + depatt);
				}
				// attempt to remove the dependency from the outstanding list
				// if the dependency does not apply to this wedge, it will be
				// ignored
				
				mFactoryRequest.removeOutstandingDependency(depatt);
			
				// If this particular request is not waiting for any subsequent dependencies
				// to be attached, then attempt to execute the request/response call
				
				if (!mFactoryRequest.hasOutstandingDependencies()) {
				
					// Complete the request for creating the dependencies
					requestContext.end();
					
					try {
						// Create the request for creating the session meem
						requestContext.begin(30000, "createSessionMeem");
						if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
							LogTools.trace(logger, Common.getLogLevel(), "Dependencies established: creating session meem");
						}
					}
					catch (RequestCreationException e) {
					
						cSessionMeemCreatorClientConduit.sessionMeemCreationError(new MajiException("Request response creation error"));
					
					}
					
					fSessionMeemFactory.createSessionMeem();
				
				}
			}
			
		
		}
		
		/*
		 *  (non-Javadoc)
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyDisconnected(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
		
	}	
		
	/*------------------------- lifecycle methods ---------------------*/
	
	public void commence() {

		
	}
	
	public void conclude() {
		
		// Destroy all existing cached session meems by instructing the 
		// remote session factory to destroy the meem
		
		if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
			LogTools.trace(logger, Common.getLogLevel(), "Shutting down session factory client....");
		}
		Iterator it = mCachedSessionMeems.keySet().iterator();
		
		while (it.hasNext()) {
			MeemPath sessionmp = (MeemPath) mCachedSessionMeems.get(it.next());
			fSessionMeemFactory.destroySessionMeem(sessionmp);
		}
		
		if (mFactoryRequest != null) {
			// Clean up all dependencies created at the start of the request
		
			Iterator ita = mFactoryRequest.getDependencies();
			while (ita.hasNext()) {
				DependencyAttribute dat = (DependencyAttribute) ita.next();
				if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
					LogTools.trace(logger, Common.getLogLevel(), "Removing session meem factory dependency " + dat);
				}
				dependencyHandlerConduit.removeDependency(dat);
			}
		
			mFactoryRequest = null;
		}
		
		// initialise all cached session objects, and ensure the 
		// current request is re-initialised
		
		if (Common.TRACE_ENABLED && Common.TRACE_LICENSING) {
			LogTools.trace(logger, Common.getLogLevel(), "Emptying cached sessions....");
		}
		//Hashtable mCachedSessionMeems = new Hashtable();
		
	}
	
	
}
