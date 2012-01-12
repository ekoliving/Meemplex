/*
 * @(#)PersistingLifeCycleManagerWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.persisting;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.*;
import org.openmaji.implementation.server.manager.lifecycle.activation.Activation;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapter;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapter;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryConduit;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.utility.uid.UID;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class PersistingLifeCycleManagerWedge implements Wedge, LifeCycleManager, LifeCycleManagerClientLCM {

	private int WEDGE_ID = this.hashCode();

	private Map<MeemPath, LifeCycleState> initialLifeCycleStates = new HashMap<MeemPath, LifeCycleState>();
	
	private Map<MeemPath, MeemDefinition> meemDefinitions        = new HashMap<MeemPath, MeemDefinition>();
	
	private Map<MeemPath, Meem>           meems                  = new HashMap<MeemPath, Meem>();

	private Set<MeemPath>                 categoryPaths          = new HashSet<MeemPath>();
	
	private Set<MeemPath>                 transferActivate       = new HashSet<MeemPath>();

	// TODO[peter] Examine the use of this collection -> not cleaned up properly?
	private Map<Object, Object> changeLCMDependencyAttributes = new HashMap<Object, Object>();

	public MeemContext meemContext;


	/* ------------------------------------- conduits ---------------------------------- */

	public LifeCycleManagerCategoryConduit lifeCycleManagerCategoryConduit; // outbound
	
	public LifeCycleManagerCategoryClient lifeCycleManagerCategoryClientConduit = new LifeCycleManagerCategoryClientImpl(); //inbound

	public LifeCycleManagerMisc lifeCycleManagerMiscConduit; // outbound
	
	public LifeCycleManagerMiscClient lifeCycleManagerMiscClientConduit = new LifeCycleManagerMiscClientImpl(); // inbound

	public LifeCycleAdapter lifeCycleAdapterConduit; // outbound
	
	public LifeCycleAdapterClient lifeCycleAdapterClientConduit = new LifeCycleAdapterClientConduit(); // inbound

	public LifeCycleManager lifeCycleManagerConduit = this;
	
	public LifeCycleManagerClient lifeCycleManagerClientConduit; // outbound

	public MeemStoreAdapter meemStoreAdapterConduit; // outbound
	
	public PersistenceHandlerAdapter persistenceHandlerAdapterConduit; // outbound

	public Activation activationConduit; // outbound
	
	public ActivationClient activationClientConduit = new ActivationClientImpl(); //inbound

	public DependencyHandler dependencyHandlerConduit; // outbound
	
	public DependencyClient dependencyClientConduit = new DependencyClientConduit(); // inbound

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientImpl();

	public InternalMeemFactory internalMeemFactoryConduit = new InternalMeemFactory() {
		public void createMeem(MeemDefinition meemDefinition, LifeCycleState lifeCycleState, String location) {
			MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, location);
			initialLifeCycleStates.put(meemPath, lifeCycleState);
			meemDefinitions.put(meemPath, meemDefinition);
			categoryPaths.add(meemPath);
			lifeCycleManagerMiscConduit.buildMeem(meemPath, meemDefinition, WEDGE_ID);
		}
	};


	/* ------------------------------------- lifecycle methods -------------------------------- */
	
	public void commence() {
		// activate all meems in our category
		lifeCycleManagerCategoryConduit.sendContent();
	}

	public void conclude() {
		categoryPaths.clear();
	}


	/* --------------- LifeCycleManagerClient methods ------------- */

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemCreated(org.openmaji.meem.Meem, java.lang.String)
	 */
	public void meemCreated(Meem meem, String identifier) {
		// Don't care
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemDestroyed(org.openmaji.meem.Meem)
	 */
	public void meemDestroyed(Meem meem) {
		// Don't care	
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemTransferred(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
		
		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel, "meemTransferred: " + meem + " to " + targetLifeCycleManager);
		}
		
		// remove dependency 

		DependencyAttribute dependencyAttribute = (DependencyAttribute) changeLCMDependencyAttributes.remove(meem.getMeemPath());

		Meem tempMeem = (Meem) changeLCMDependencyAttributes.remove(dependencyAttribute);
		changeLCMDependencyAttributes.remove(tempMeem);
		
		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel, "changeLCMDependencyAttributes = " + changeLCMDependencyAttributes);
		}

		dependencyHandlerConduit.removeDependency(dependencyAttribute);

		// notify our clients

		lifeCycleManagerClientConduit.meemTransferred(meem, targetLifeCycleManager);
	}


	/* --------------------- LifeCycleManager methods --------------------- */

	/**
	 * 
	 */
	public void createMeem(MeemDefinition meemDefinition, LifeCycleState initialState) throws IllegalArgumentException {
		UID uid = UID.spi.create();
		MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, uid.getUIDString());

		initialLifeCycleStates.put(meemPath, initialState);
		meemDefinitions.put(meemPath, meemDefinition);

		categoryPaths.add(meemPath);

		lifeCycleManagerMiscConduit.buildMeem(meemPath, meemDefinition, WEDGE_ID);
	}

	/**
	 * 
	 */
	public void destroyMeem(Meem meem) {
		// only do this for meemstore meempaths

		MeemPath meemPath = meem.getMeemPath();

		if (!meemPath.getSpace().equals(Space.MEEMSTORE)) {
			return;
		}

		// only destroy our own meems
		if (!categoryPaths.contains(meemPath)) {
			return;
		}
		
		lifeCycleManagerCategoryConduit.removeEntry(meemPath.getLocation());
		
		lifeCycleAdapterConduit.changeLifeCycleState(meem, LifeCycleState.ABSENT);		

		meemStoreAdapterConduit.destroyMeem(meemPath);

		lifeCycleManagerClientConduit.meemDestroyed(meem);
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManager#transferMeem(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void transferMeem(Meem meem, LifeCycleManager targetLifeCycleManager) {
		Meem self = meemContext.getSelf();
		MeemPath selfPath = self.getMeemPath();

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel,
				"Transferring meem " + meem + " to LCM " + targetLifeCycleManager + " from LCM " + selfPath);
		}

		MeemPath meemPath = meem.getMeemPath();
		MeemPath targetPath = ((Meem) targetLifeCycleManager).getMeemPath();

		boolean isMyLCM = targetPath.equals(selfPath);

		if (categoryPaths.contains(meemPath)) {
			if (isMyLCM) {
				// Nothing to do
			}
			else {
				lifeCycleManagerMiscConduit.changeParentLifeCycleManager(meem, targetLifeCycleManager);
			}
		}
		else {
			if (isMyLCM) {
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					LogTools.trace(logger, logLevel, "Transferring to self");
				}

				transferActivate.add(meemPath);
				categoryPaths.add(meemPath);
				activationConduit.activate(meemPath);

			}
			else {
				LogTools.error(logger, "Transfers must be initiated by the source or target LCM");
			}
		}
	}

	private void notifyClients(MeemPath meemPath) {
		initialLifeCycleStates.remove(meemPath);
		MeemDefinition meemDefinition = (MeemDefinition) meemDefinitions.remove(meemPath);
		Meem meem = (Meem) meems.remove(meemPath);

		String identifier = meemDefinition.getMeemAttribute().getIdentifier();

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel, "Meem created: " + identifier + " : " + meemPath);
		}

		//	------------------------------------------------
		// For those who care ... the Meem has been created
		lifeCycleManagerClientConduit.meemCreated(meem, identifier);
	}

	/* ---------------LifeCycleManagerMiscClient class ----------- */

	private final class LifeCycleManagerMiscClientImpl implements LifeCycleManagerMiscClient {

		/**
		 * 
		 */
		public void meemBuilt(MeemPath meemPath, final Meem meem, int requestId) {
			if (DEBUG) {
				LogTools.info(logger, "meemBuilt: " + meemPath + " - " + requestId);
			}

			if (requestId == WEDGE_ID) {
				if (DEBUG) {
					LogTools.info(logger, "matches WEDGE_ID: " + meemPath + " - " + requestId);
				}
				
				meems.put(meemPath, meem);

				lifeCycleManagerMiscConduit.addLifeCycleReference(meem);
			}
		}

		public void lifeCycleReferenceAdded(Meem meem) {
			if (meems.containsValue(meem)) {
				LifeCycleState initialState = (LifeCycleState) initialLifeCycleStates.get(meem.getMeemPath());
				MeemDefinition meemDefinition = (MeemDefinition) meemDefinitions.get(meem.getMeemPath());

				// store the meem defintion and content

				meemStoreAdapterConduit.storeMeemDefinition(meem.getMeemPath(), meemDefinition);
				persistenceHandlerAdapterConduit.persist(meem);

				// fake a restore
				persistenceHandlerAdapterConduit.restore(meem, new MeemContent());

				// add it to our startup category
				lifeCycleManagerCategoryConduit.addEntry(meem.getMeemPath().getLocation(), meem);

				if (initialState.equals(LifeCycleState.LOADED)) {
					if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
						LogTools.trace(logger, logLevel, "About to limit LifeCycleStae " + initialState + " : " + meem.getMeemPath());
					}
					lifeCycleAdapterConduit.limitLifeCycleState(meem, initialState);
				} else {
					if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
						LogTools.trace(logger, logLevel, "About to make " + initialState + " : " + meem.getMeemPath());
					}

					lifeCycleAdapterConduit.changeLifeCycleState(meem, initialState);
				}
			}

		}

		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient#parentLifeCycleManagerChanged(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
		 */
		public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager targetLifeCycleManager) {
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(logger, logLevel, "parentLifeCycleManagerChanged " + meem.getMeemPath());
			}
			// take it out of our category

			lifeCycleManagerCategoryConduit.removeEntry(meem.getMeemPath().getLocation());

			// meem will have already set itself to dormant

			// set up dependency on target lcms lifeCycleManagerClient facet

			DependencyAttribute dependencyAttribute =
				new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, (Meem) targetLifeCycleManager, "lifeCycleManagerClient", null, true);

			changeLCMDependencyAttributes.put(meem.getMeemPath(), dependencyAttribute);
			changeLCMDependencyAttributes.put(dependencyAttribute, meem);
			changeLCMDependencyAttributes.put(meem, targetLifeCycleManager);

			dependencyHandlerConduit.addDependency("lifeCycleManagerClientLCM", dependencyAttribute, LifeTime.TRANSIENT);

		}
		
		/**
		 * 
		 */
		public void meemDeactivated(MeemPath meemPath) {
			if (categoryPaths.contains(meemPath)) {
				// reactivate the meem
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					LogTools.trace(logger, logLevel, "Reactivating " + meemPath);
				}
				activationConduit.activate(meemPath);
			}
		}

	}

	/* --------------LifeCycleManagerCategoryClient conduit ---------- */

	private final class LifeCycleManagerCategoryClientImpl implements LifeCycleManagerCategoryClient {

		private boolean startupDone = false;
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesAdded(CategoryEntry[] newEntries) {
			// got ew meems, so lets add them to our list of ones we can start

			for (int i = 0; i < newEntries.length; i++) {
				if (!categoryPaths.contains(newEntries[i].getMeem().getMeemPath())) {
					categoryPaths.add(newEntries[i].getMeem().getMeemPath());
					activationConduit.activate(newEntries[i].getMeem().getMeemPath());
				}
			}

		}

		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(CategoryEntry[] removedEntries) {
			for (int i = 0; i < removedEntries.length; i++) {
				categoryPaths.remove(removedEntries[i].getMeem().getMeemPath());
			}
		}

		/**
		 * 
		 */
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			// Don't care		
		}

		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */
		public void contentSent() {
			if (!startupDone) {
				startupDone = true;
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					LogTools.trace(logger, logLevel, "Startup activation done: " + meemContext.getSelf().getMeemPath());
				}
			}			
		}

		/**
		 * 
		 */
		public void contentFailed(String reason) {
			LogTools.warn(logger, "Content failed during LCM startup: " + reason);
		}
	}

	/* ---------------------- ActivationClient conduit ----------------*/

	private final class ActivationClientImpl implements ActivationClient {
		/**
		 * 
		 */
		public void activated(MeemPath meemPath, Meem meem, MeemDefinition meemDefinition) {
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(logger, logLevel, "activated : " + meem);
			}
			if (transferActivate.remove(meemPath)) {
				lifeCycleManagerClientConduit.meemTransferred(meem, (LifeCycleManager)meemContext.getTarget("lifeCycleManager"));
				lifeCycleManagerCategoryConduit.addEntry(meemPath.getLocation(), meem);
			}
		}

		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient#activationFailed(org.openmaji.meem.MeemPath)
		 */
		public void activationFailed(MeemPath meemPath) {
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.warn(logger, "activationFailed : " + meemPath);
			}
		}

	}


	/* ---------------------- DependencyClient conduit ----------------*/

	private final class DependencyClientConduit implements DependencyClient {

		/**
		 * 
		 */
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {

			Meem meem = (Meem) changeLCMDependencyAttributes.get(dependencyAttribute);

			if (meem != null)
			{
				LifeCycleManager targetLifeCycleManager = (LifeCycleManager) changeLCMDependencyAttributes.get(meem);

				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					LogTools.trace(logger, logLevel, "dependencyConnected: About to transfer " + meem + " to " + targetLifeCycleManager);
				}
				targetLifeCycleManager.transferMeem(meem, targetLifeCycleManager);
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
	}

	/*---------------- LifeCycleAdapterClientConduit ----------------------------------*/

	private final class LifeCycleAdapterClientConduit implements LifeCycleAdapterClient {

		public void lifeCycleStateChanged(MeemPath meemPath, LifeCycleTransition transition) {
			Meem meem = (Meem) meems.get(meemPath);
			if (meem != null) {
				LifeCycleState initialState = (LifeCycleState) initialLifeCycleStates.get(meemPath);
				if (!initialState.equals(LifeCycleState.LOADED) && transition.getCurrentState().equals(initialState)) {
					notifyClients(meemPath);
				}
			}
		}

		public void lifeCycleLimitChanged(MeemPath meemPath, LifeCycleState state) {
			Meem meem = (Meem) meems.get(meemPath);

			if (meem != null) {
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					LogTools.trace(logger, logLevel, "lifeCycleLimitChanged " + state + " : " + meemPath);
				}
				notifyClients(meemPath);
			}

		}

	}
	
	private final class LifeCycleClientImpl implements LifeCycleClient {

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.PENDING_READY)) {
				commence();
			}
			
			if (transition.equals(LifeCycleTransition.READY_PENDING)) {
				conclude();
			}

		}
		
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			// don't care
		}
	}

	/* ---------- Logging fields ----------------------------------------------- */

	private static final boolean DEBUG = false;
	
	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final int logLevel = Common.getLogLevelVerbose();

}
