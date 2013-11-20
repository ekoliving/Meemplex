/*
 * @(#)PersistenceHandlerAdapterWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.adapter.persistence;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openmaji.implementation.server.Common;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.*;
import org.openmaji.meem.wedge.dependency.*;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class PersistenceHandlerAdapterWedge implements Wedge, FilterChecker, PersistenceClientAdapter {

	private Set persistDependencyAttributes = Collections.synchronizedSet(new HashSet());
	private Map restoreRequests = Collections.synchronizedMap(new HashMap());
	private Map restoreClientDependencyAttributes = Collections.synchronizedMap(new HashMap());
	private Map restoreClientContent = Collections.synchronizedMap(new HashMap());
	private Map meems = Collections.synchronizedMap(new HashMap());

	public MeemCore meemCore;

	// outbound facets
	public ManagedPersistenceHandler persistenceHandlerAdapter;

	// conduits
	public PersistenceHandlerAdapter persistenceHandlerAdapterConduit = new PersistenceHandlerAdapterConduit(); // inbound
	public ManagedPersistenceClient persistenceClientAdapterConduit; // outbound 

	public DependencyHandler dependencyHandlerConduit; // outbound
	public DependencyClient dependencyClientConduit = new DependencyClientConduit(); // inbound
	
	public MeemClientConduit meemClientConduit;
	public Meem meemConduit;

	/* ------------ FilterChecker methods ------------------ */

	/**
	 * 
	 */
	public boolean invokeMethodCheck(Filter filter, String facetName, String methodName, Object[] args) throws IllegalFilterException {

		if (filter instanceof ExactMatchFilter) {
			ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;

			MeemPath meemPath = (MeemPath) exactMatchFilter.getTemplate();

			if (methodName.equals("restore")) {
				Request request = (Request) restoreRequests.remove(meemPath);
				if (request != null) {
					return true;
				}
			}
		}

		return false;
	}


	/* ----------- LifeCycleAdapter Conduit ---------------- */

	class PersistenceHandlerAdapterConduit implements PersistenceHandlerAdapter {

		/**
		 * 
		 */
		public void restore(Meem meem, MeemContent meemContent) {
//			System.err.println("> PersistenceHandlerAdapterConduit.restore: " + meem.getMeemPath());
			
			meems.put(meem.getMeemPath(), meem);
			
			DependencyAttribute clientDependencyAttribute = new DependencyAttribute(DependencyType.WEAK,
					Scope.LOCAL,
					meem,
					"managedPersistenceClient",
					new ExactMatchFilter(meem.getMeemPath()), 
					false);
			
			restoreClientDependencyAttributes.put(meem.getMeemPath(), clientDependencyAttribute);
			
			dependencyHandlerConduit.addDependency("persistenceClientAdapter", clientDependencyAttribute, LifeTime.TRANSIENT);
			
			restoreClientContent.put(clientDependencyAttribute, meemContent);						
		}
		
		/**
		 * 
		 */
		public void persist(Meem meem) {
			//logger.log(Level.INFO,  "persisting the meem: " + meem);
			DependencyAttribute persistDependencyAttribute =
				new DependencyAttribute(
					DependencyType.WEAK,
					Scope.LOCAL,
					meem,
					"managedPersistenceHandler",
					null, 
					true);

			persistDependencyAttributes.add(persistDependencyAttribute);

			dependencyHandlerConduit.addDependency("persistenceHandlerAdapter", persistDependencyAttribute, LifeTime.TRANSIENT);

		}
	}

	/* ----------- DependencyClient Conduit ---------------- */

	class DependencyClientConduit implements DependencyClient {

		/**
		 * 
		 */
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
//			System.err.println("PersistenceHandlerAdapterConduit.dependencyConnected: " + dependencyAttribute);
			if (dependencyAttribute == null) {
				logger.log(Level.WARNING, " *** DependencyAttribute is null ***");
			}
			
			boolean removed = persistDependencyAttributes.remove(dependencyAttribute);
			if (removed) {
				persistenceHandlerAdapter.persist();
				
				dependencyHandlerConduit.removeDependency(dependencyAttribute);
				return;
			}
			
			boolean restoreValid = restoreClientDependencyAttributes.containsValue(dependencyAttribute); 
			if (restoreValid) {
				Meem meem = dependencyAttribute.getMeem();
				
				MeemContent meemContent = (MeemContent) restoreClientContent.remove(dependencyAttribute);
				
				MeemClientCallback callback = new MeemClientCallbackImpl(meemContent);

				meemClientConduit.provideReference(meem, "managedPersistenceHandler", ManagedPersistenceHandler.class, callback);
			}
		}

		/**
		 * 
		 */
		public void dependencyDisconnected(DependencyAttribute dependencyAttribute) {
		}
		
		/**
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyAdded(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute) {
		}

		/**
		 * @see org.openmaji.meem.wedge.dependency.DependencyClient#dependencyRemoved(org.openmaji.meem.definition.DependencyAttribute)
		 */
		public void dependencyRemoved(DependencyAttribute dependencyAttribute) {
		}
		
		public void dependencyUpdated(DependencyAttribute dependencyAttribute) {
		}


		private class MeemClientCallbackImpl implements MeemClientCallback {
			private MeemContent meemContent;
			
			public MeemClientCallbackImpl(MeemContent meemContent) {
				this.meemContent = meemContent;
			}
			
			public void referenceProvided(Reference reference) {
				((ManagedPersistenceHandler)reference.getTarget()).restore(meemContent);
			}
		}
	}

	/* ------------ ManagedPersistenceClient methods -------- */

	/**
	 * 
	 */
	public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
		// put it on the conduit
		persistenceClientAdapterConduit.meemContentChanged(meemPath, meemContent);
	}

	/**
	 * 
	 */
	public void restored(MeemPath meemPath) {
		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Restored: " + meemPath);
		}

		meems.remove(meemPath);
				
		DependencyAttribute dependencyAttribute = (DependencyAttribute) restoreClientDependencyAttributes.remove(meemPath);
		
		dependencyHandlerConduit.removeDependency(dependencyAttribute);
	
		persistenceClientAdapterConduit.restored(meemPath);
	}

	/* ----------- Request class ------------- */

	class Request {

		private Meem meem;
		private DependencyAttribute dependencyAttribute;
		private MeemContent meemContent;

		public Request(Meem meem, DependencyAttribute dependencyAttribute, MeemContent meemContent) {
			this.meem = meem;
			this.dependencyAttribute = dependencyAttribute;
			this.meemContent = meemContent;

		}

		public DependencyAttribute getDependencyAttribute() {
			return dependencyAttribute;
		}

		public MeemContent getMeemContent() {
			return meemContent;
		}

		public Meem getMeem() {
			return meem;
		}

	}
	
	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final Level logLevel = Common.getLogLevelVerbose();
}
