/*
 * @(#)LazyLifeCycleManagerWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.lazy;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.InternalMeemFactory;
import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerClientLCM;
import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMisc;
import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient;
import org.openmaji.implementation.server.manager.lifecycle.activation.Activation;
import org.openmaji.implementation.server.manager.lifecycle.activation.ActivationClient;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapter;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapter;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryConduit;
import org.openmaji.implementation.server.manager.registry.MeemRegistryGatewayWedge;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.LifeTime;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.filter.FilterChecker;
import org.openmaji.meem.filter.IllegalFilterException;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.meem.FacetClientConduit;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.meem.wedge.reference.ContentClient;
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
public class LazyLifeCycleManagerWedge implements Wedge, LifeCycleManager, LifeCycleManagerClientLCM,
	FilterChecker {

	private int WEDGE_ID = this.hashCode();

	private Set categoryEntries = new HashSet();
	private Set categoryPaths = new HashSet();

	private Map initialLifeCycleStates = new HashMap();
	private Map meemDefinitions = new HashMap();

	private Map meems = new HashMap();
	private Map activatedMeems = new HashMap();
	private Map activatingMeems = new HashMap();

	// TODO[peter] Examine the use of this collection -> not cleaned up properly?
	private Map changeLCMDependencyAttributes = new HashMap();

	public MeemContext meemContext;


	public synchronized boolean invokeMethodCheck(Filter filter, String methodName, Object[] args)
		throws IllegalFilterException {

		if (!(filter instanceof ExactMatchFilter)) {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		if (methodName.equals("meemRegistered")
			|| methodName.equals("meemDeregistered")) {

			Meem meem = (Meem) args[0];
			if (meem != null) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
				return exactMatchFilter.getTemplate().equals(meem.getMeemPath());
			}
		}

		return false;
	}


	// outbound facets
	public MeemRegistryClient meemRegistryClient;
	public final AsyncContentProvider meemRegistryClientProvider = new AsyncContentProvider() {
		
		public void asyncSendContent(Object target, Filter filter, ContentClient contentClient) {
			final MeemRegistryClient meemRegistryClient = (MeemRegistryClient) target;

			if (filter instanceof ExactMatchFilter) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
				Object template = exactMatchFilter.getTemplate();

				if (template instanceof MeemPath) {
					MeemPath meemPath = (MeemPath) exactMatchFilter.getTemplate();

					Meem meem = (Meem) activatedMeems.get(meemPath);

					if (meem != null) {
						if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
							LogTools.trace(logger, logLevel, "Already activated Meem: " + meemPath + " : " + meem);
						}

						meemRegistryClient.meemRegistered(meem);
						contentClient.contentSent();
					}
					else if (categoryPaths.contains(meemPath)) {

						List clients = (List) activatingMeems.get(meemPath);

						boolean shouldActivate = false;
						if (clients == null) {
							clients = new ArrayList();
							activatingMeems.put(meemPath, clients);

							if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
								LogTools.trace(logger, logLevel, "Lazily activating Meem: " + meemPath);
							}

							shouldActivate = true;
						}

						ActivationTarget client = new ActivationTarget(meemRegistryClient, contentClient);

						clients.add(client);

						if (shouldActivate)
						{
							activationConduit.activate(meemPath);
						}
					}
					else {
						contentClient.contentSent();
					}
				}
				else {
					contentClient.contentFailed("Unsupported template: " + template);
				}
			}
			else {
				contentClient.contentFailed("Unsupported filter: " + filter);
			}
		}
	};

	private static final class ActivationTarget {
		public ActivationTarget(MeemRegistryClient meemRegistryClient, ContentClient contentClient)	{
			this.meemRegistryClient = meemRegistryClient;
			this.contentClient = contentClient;
		}

		public final MeemRegistryClient meemRegistryClient;
		public final ContentClient contentClient;
	}


	// conduits
	public ActivationClient activationClientConduit = new ActivationClientImpl(); // inbound
	public Activation activationConduit; // outbound

	public LifeCycleManagerCategoryConduit lifeCycleManagerCategoryConduit; // outbound
	public LifeCycleManagerCategoryClient lifeCycleManagerCategoryClientConduit = new LifeCycleManagerCategoryClientImpl(); //inbound

	public LifeCycleManagerMisc lifeCycleManagerMiscConduit; // outbound
	public LifeCycleManagerMiscClient lifeCycleManagerMiscClientConduit = new LifeCycleManagerMiscClientImpl(); // inbound

	public LifeCycleAdapter lifeCycleAdapterConduit; // outbound
	public LifeCycleAdapterClient lifeCycleAdapterClientConduit = new LifeCycleAdapterClientConduit(); // inbound
	public LifeCycleManagerClient lifeCycleManagerClientConduit; // outbound

	public MeemStoreAdapter meemStoreAdapterConduit; // outbound
	public PersistenceHandlerAdapter persistenceHandlerAdapterConduit; // outbound

	public FacetClientConduit facetClientConduit;
	public Vote lifeCycleControlConduit;

	public ManagedPersistenceClient 					managedPersistenceClientConduit = new PersistenceClientImpl();
	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);
	
	public DependencyHandler dependencyHandlerConduit; // outbound
	public DependencyClient dependencyClientConduit = new DependencyClientConduit(); // inbound


	private boolean started = false;

	public void commence() {

		// vote ourself back to pending
		// we will go ready when we have loaded all our content
		if (!started) {
			lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
		}
		MeemRegistryGatewayWedge.addLocalRegistry(meemContext.getSelf());
	}

	public void conclude() {
		categoryEntries.clear();
		categoryPaths.clear();
		
		activatedMeems.clear();
	}


	public InternalMeemFactory internalMeemFactoryConduit = new InternalMeemFactory() {
		public void createMeem(MeemDefinition meemDefinition, LifeCycleState lifeCycleState, String location) {
			MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, location);

			initialLifeCycleStates.put(meemPath, lifeCycleState);
			meemDefinitions.put(meemPath, meemDefinition);

			categoryPaths.add(meemPath);

			lifeCycleManagerMiscConduit.buildMeem(meemPath, meemDefinition, WEDGE_ID);
		}
	};




	/* --------------------- LifeCycleManager methods --------------------- */

	public void createMeem(MeemDefinition meemDefinition, LifeCycleState initialState) throws IllegalArgumentException {

		UID uid = UID.spi.create();
		MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, uid.getUIDString());

		initialLifeCycleStates.put(meemPath, initialState);
		meemDefinitions.put(meemPath, meemDefinition);

		categoryPaths.add(meemPath);

		lifeCycleManagerMiscConduit.buildMeem(meemPath, meemDefinition, WEDGE_ID);
	}

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

		lifeCycleAdapterConduit.changeLifeCycleState(meem, LifeCycleState.ABSENT);

		lifeCycleManagerCategoryConduit.removeEntry(meemPath.getLocation());

		meemStoreAdapterConduit.destroyMeem(meemPath);

		lifeCycleManagerClientConduit.meemDestroyed(meem);
	}

	public void transferMeem(Meem meem, LifeCycleManager targetLifeCycleManager) {
		Meem self = meemContext.getSelf();
		MeemPath selfPath = self.getMeemPath();

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(
				logger,
				logLevel,
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

				// all we need to do is to activate the meem
				
				List clients = (List) activatingMeems.get(meemPath);
				if (clients == null) {
					clients = new ArrayList();
					activatingMeems.put(meemPath, clients);
					
					activationConduit.activate(meemPath);
				}

				lifeCycleManagerCategoryConduit.addEntry(meemPath.getLocation(), meem);

				lifeCycleManagerClientConduit.meemTransferred(meem, targetLifeCycleManager);
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
		
		activatedMeems.put(meemPath, meem);
		activatingMeems.remove(meemPath);

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
			if (requestId == WEDGE_ID) {

				meems.put(meemPath, meem);

				lifeCycleManagerMiscConduit.addLifeCycleReference(meem);

			}
		}
		
		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient#lifeCycleReferenceAdded(org.openmaji.meem.Meem)
		 */
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
			// take it out of our category

			lifeCycleManagerCategoryConduit.removeEntry(meem.getMeemPath().getLocation());
			activatedMeems.remove(meem.getMeemPath());

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
			// don't care
		}
	}

	/* --------------- ActivationClientImpl class --------------*/

	private final class ActivationClientImpl implements ActivationClient {

		/**
		 * 
		 */
		public void activated(final MeemPath meemPath, final Meem meem, MeemDefinition meemDefinition) {
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(logger, logLevel, "****** Lazily Activated " + meemPath);
			}

			meemRegistryClient.meemRegistered(meem);

			activatedMeems.put(meemPath, meem);

			handle(meemPath, meem);
		}

		/**
		 * 
		 */
		public void activationFailed(MeemPath meemPath) {
			handle(meemPath, null);
		}

		private void handle(MeemPath meemPath, Meem meem) {		
			List clients = (List) activatingMeems.remove(meemPath);

			if (clients != null) {
				Iterator iterator = clients.iterator();
				while (iterator.hasNext()) {
					ActivationTarget client = (ActivationTarget) iterator.next();

					if (meem != null) {
						client.meemRegistryClient.meemRegistered(meem);
						client.contentClient.contentSent();
					} else {
						client.contentClient.contentFailed("Activation Failed");
					}
					
				}
			}
		}
	}

	/* --------------LifeCycleManagerCategoryClient conduit ---------- */

	private final class LifeCycleManagerCategoryClientImpl implements LifeCycleManagerCategoryClient {
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesAdded(CategoryEntry[] newEntries) {
			// got ew meems, so lets add them to our list of ones we can start
			
			for (int i = 0; i < newEntries.length; i++) {
				categoryPaths.add(newEntries[i].getMeem().getMeemPath());
				categoryEntries.add(newEntries[i]);	
			}

		}

		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(CategoryEntry[] removedEntries) {
			for (int i = 0; i < removedEntries.length; i++) {
				categoryPaths.remove(removedEntries[i].getMeem().getMeemPath());
				categoryEntries.remove(removedEntries[i]);
			}
		}


		/**
		 * 
		 */
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			// Don't care		
		}

		/**
		 * 
		 */
		public void contentSent() {
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(logger, logLevel, "Startup Category contentSent done for : "
					+ meemContext.getSelf().getMeemPath());
			}

			if (!started)
			{
				started = true;
				lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), true);
			}
		}

		/**
		 * 
		 */
		public void contentFailed(String reason) {
			started = true;
		}
	}

	/* ------------------- ManagedPersistenceClient conduit -------------------- */

	private final class PersistenceClientImpl implements ManagedPersistenceClient {
		/**
		 * 
		 */
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			// don't care
		}

		/**
		 * @see org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient#restored(org.openmaji.meem.MeemPath)
		 */
		public void restored(MeemPath meemPath) {
			// get our category to tell us about our meems

			lifeCycleManagerCategoryConduit.sendContent();
		}

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
		// remove dependency 

		DependencyAttribute dependencyAttribute = (DependencyAttribute)
			changeLCMDependencyAttributes.remove(meem.getMeemPath());

		Meem tempMeem = (Meem) changeLCMDependencyAttributes.remove(dependencyAttribute);
		changeLCMDependencyAttributes.remove(tempMeem);

		dependencyHandlerConduit.removeDependency(dependencyAttribute);

		// notify our clients

		lifeCycleManagerClientConduit.meemTransferred(meem, targetLifeCycleManager);
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
				LifeCycleManager targetLifeCycleManager = (LifeCycleManager)
					changeLCMDependencyAttributes.get(meem);

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
		
		/**
		 * 
		 */
		public void lifeCycleLimitChanged(MeemPath meemPath, LifeCycleState state) {
			Meem meem = (Meem) meems.get(meemPath);

			if (meem != null) {
				notifyClients(meemPath);
			}

		}

	}	
	/* ---------- Logging fields ---------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */
	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */
	private static final int logLevel = Common.getLogLevelVerbose();
}
