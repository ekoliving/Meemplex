/*
 * @(#)LifeCycleManagerWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.meem.core.MeemBuilder;
import org.openmaji.implementation.server.meem.core.MeemCoreImpl;
import org.openmaji.implementation.server.meem.wedge.reference.AsyncContentProvider;
import org.openmaji.meem.Meem;
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
import org.openmaji.meem.wedge.dependency.DependencyClient;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.lifecycle.Vote;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.CreateMeemFilter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
import org.openmaji.system.manager.registry.MeemRegistry;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagement;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.utility.uid.UID;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * This wedge will only create transient meems
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class LifeCycleManagerWedge implements Wedge, LifeCycleManagementClientLCM, FilterChecker, MeemRegistryClientLCM {

	private static final String UNIDENTIFIED = "unidentified";

	public MeemCore meemCore;

	/* ------------------------------ outbound facets --------------------------------- */

	public LifeCycleManagerClient lifeCycleManagerClient;

	public final AsyncContentProvider lifeCycleManagerClientProvider = new AsyncContentProvider() {

		public void asyncSendContent(Object target, Filter filter, ContentClient contentClient) {
			LifeCycleManagerClient lifeCycleManagerClientTarget = (LifeCycleManagerClient) target;

			if (filter == null) {
				for (Meem meem : getMeems()) {
					lifeCycleManagerClientTarget.meemCreated(meem, UNIDENTIFIED);
				}
			}
			else if (filter instanceof CreateMeemFilter) {
				if (DEBUG) {
					logger.log(Level.INFO, "creating meem through outboundfilter");
				}
				
				CreateMeemFilter createMeemFilter = (CreateMeemFilter) filter;
				UID uid = UID.spi.create();
				String location = uid.getUIDString();
				PendingCreateMeem pendingCreateMeem = new PendingCreateMeem(lifeCycleManagerClientTarget, contentClient);

				pendingCreateMeems.put(location, pendingCreateMeem);

				internalMeemFactoryConduit.createMeem(createMeemFilter.meemDefinition, createMeemFilter.lifeCycleState, location);

				return;
			}
			else if (filter instanceof ExactMatchFilter) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;

				Object template = exactMatchFilter.getTemplate();
				if (template instanceof MeemPath) {
					MeemPath meemPath = (MeemPath) template;
					Meem meem = getMeem(meemPath);

					if (meem != null) {
						lifeCycleManagerClientTarget.meemCreated(meem, UNIDENTIFIED);
					}
				}
				// else if (template instanceof String) {
				// String identifier = (String) template;
				// // -mg- this should send back all the meems with matching identifers
				// }
				else {
					contentClient.contentFailed("Unsupported template type: " + template.getClass());
					return;
				}
			}
			else {
				contentClient.contentFailed("Unsupported filter type: " + filter.getClass());
				return;
			}

			contentClient.contentSent();
		}
	};

	public MeemRegistry meemRegistry;

	/* ------------------------------ conduits --------------------------------- */

	public LifeCycleManagerMisc lifeCycleManagerMiscConduit = new LifeCycleManagerConduitImpl(); // inbound

	public LifeCycleManagerMiscClient lifeCycleManagerMiscClientConduit; // outbound - this is a stupid name

	public DependencyHandler dependencyHandlerConduit; // outbound

	public DependencyClient dependencyClientConduit = new DependencyClientConduit(); // inbound

	public LifeCycleAdapter lifeCycleAdapterConduit; // outbound

	public LifeCycleAdapterClient lifeCycleAdapterClientConduit = new LifeCycleAdapterClientConduit(); // inbound

	public LifeCycleManagerClient lifeCycleManagerClientConduit = new LifeCycleManagerClientImpl(); // inbound

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientImpl();

	public InternalMeemFactory internalMeemFactoryConduit; // outbound

	public LifeCycleManagerShutdownClient lifeCycleManagerShutdownClientConduit; // outbound

	public Vote lifeCycleControlConduit;

	/* --------------------------------- private members ------------------------------ */

	// Collection of LifeCycle managed Meems
	private Map<MeemPath, Meem> meems = new HashMap<MeemPath, Meem>();

	// Collection of LifeCycle DependencyAttributes for managed Meems
	private Map<MeemPath, Reference> meemLifeCycleReferences = new HashMap<MeemPath, Reference>();

	// Collection of meem cores
	// -mg- this is only used by the change parent lcm methods
	private Map<MeemPath, MeemCore> meemCores = new HashMap<MeemPath, MeemCore>();

	private Map<MeemPath, Integer> unregisteredMeems = new HashMap<MeemPath, Integer>();

	private Set<Meem> registeredMeems = Collections.synchronizedSet(new HashSet<Meem>());

	private boolean concluding = false;

	private Map<String, PendingCreateMeem> pendingCreateMeems = new HashMap<String, PendingCreateMeem>();

	// dependency attributes
	private DependencyAttribute meemRegistryDependencyAttribute;

	private DependencyAttribute meemRegistryClientConcludeDependencyAttribute;

	private Map<DependencyAttribute, Meem> registerMeemDependencyAttributes = new HashMap<DependencyAttribute, Meem>();

	private Map<Meem, DependencyAttribute> registerMeemValues = new HashMap<Meem, DependencyAttribute>();

	private Map<MeemPath, DependencyAttribute> changeLCMDependencyAttributes = new HashMap<MeemPath, DependencyAttribute>();
	private Map<DependencyAttribute, MeemPath> changeLCMDependencyAttributesReverse = new HashMap<DependencyAttribute, MeemPath>();

	private Map<MeemPath, LifeCycleManager> changeLCMValues = new HashMap<MeemPath, LifeCycleManager>();

	/* ----------------------- lifecycle methods ------------------------------ */

	public void commence() {
		lifeCycleControlConduit.vote(meemCore.getMeemPath().toString(), LifeCycleTransition.PENDING_LOADED, false);

		concluding = false;

		// set up MeemRegistry Dependency
		meemRegistryDependencyAttribute = new DependencyAttribute(DependencyType.STRONG, Scope.LOCAL, meemCore.getMeemRegistry(), "meemRegistry", null, true);

		dependencyHandlerConduit.addDependency("meemRegistry", meemRegistryDependencyAttribute, LifeTime.TRANSIENT);

		// set up MeemRegistryClient Dependency
		meemRegistryClientConcludeDependencyAttribute = new DependencyAttribute(DependencyType.STRONG, Scope.LOCAL, meemCore.getMeemRegistry(), "meemRegistryClient", null, true);

		dependencyHandlerConduit.addDependency("meemRegistryClientLCM", meemRegistryClientConcludeDependencyAttribute, LifeTime.TRANSIENT);
	}

	public void conclude() {
		concluding = true;

		for (MeemPath meemPath : meems.keySet()) {
			deactivateMeem(meemPath);
		}

		if (meems.size() == 0) {
			lifeCycleControlConduit.vote(meemCore.getMeemPath().toString(), LifeCycleTransition.PENDING_LOADED, true);
		}
	}

	/* --------------------------- inner classes ---------------------------- */

	/**
	 * 
	 */
	private final class LifeCycleClientImpl implements LifeCycleClient {

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			if (transition.equals(LifeCycleTransition.LOADED_PENDING)) {
				commence();
			}

			if (transition.equals(LifeCycleTransition.READY_PENDING)) {
				conclude();
			}

			if (transition.equals(LifeCycleTransition.PENDING_LOADED)) {
				removeDependencies();
			}

		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			// don't care
		}
	}

	private void removeDependencies() {
		// System.err.println("LifeCycleManagerWedge.removeDependencies(): " + meemCore.getMeemPath());

		// remove MeemRegistry Dependency
		dependencyHandlerConduit.removeDependency(meemRegistryDependencyAttribute);
		meemRegistryDependencyAttribute = null;

		// remove MeemRegistryClient Dependency
		dependencyHandlerConduit.removeDependency(meemRegistryClientConcludeDependencyAttribute);
		meemRegistryClientConcludeDependencyAttribute = null;

		lifeCycleManagerShutdownClientConduit.shutdown();

		concluding = false;
	}

	private void constructMeem(MeemPath meemPath, MeemDefinition meemDefinition, int requestId) {

		// Make sure we haven't already got this MeemPath

		if (getMeem(meemPath) != null) {
			return;
		}

		// ------------------------------------------------------------------------
		// Ensure that MeemDefinition MeemAttributes ImmutableAttributes are sealed

		meemDefinition.getMeemAttribute().sealImmutableAttributes();

		// -----------------------------------------------------------------
		// Build the Meem and maintain the LifeCycle managed Meem Collection

		MeemBuilder meemBuilder = MeemBuilder.spi.create(meemPath);

		MeemCore newMeemCore = meemBuilder.getMeemCore();

		meemBuilder.addMeemDefinition(meemDefinition, null);

		Meem meem = newMeemCore.getSelf();

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Meem built. Identifier: " + meemDefinition.getMeemAttribute().getIdentifier() + " MeemPath: " + newMeemCore.getMeemPath() + " LCM: " + meemCore.getMeemPath());
		}

		putMeem(newMeemCore.getMeemPath(), newMeemCore);

		// ---------------------------------------------
		// During normal operations, initialize the Meem

		((MeemCoreImpl) newMeemCore).initialize(meemCore.getMeemRegistry(), meemCore.getSelf(), null, meemCore.getThreadManager(), null, meemCore.getMeemStore());

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Meem initialized. " + newMeemCore.getMeemPath());
		}

		LifeCycleManagement lifeCycleManagementFacet = (LifeCycleManagement) newMeemCore.getTarget("lifeCycleManagement");
		lifeCycleManagementFacet.changeParentLifeCycleManager((LifeCycleManager) meemCore.getTarget("lifeCycleManager"));

		if (DEBUG) {
			logger.log(Level.INFO,  "make meem loaded: " + meemPath + " - " + requestId);
		}
		// make it loaded
		unregisteredMeems.put(meemPath, new Integer(requestId));

		lifeCycleAdapterConduit.changeLifeCycleState(meem, LifeCycleState.LOADED);
	}

	private Meem getMeem(MeemPath meemPath) {
		return ((Meem) meems.get(meemPath));
	}

	private Set<Meem> getMeems() {
		Set<Meem> meemSet;
		synchronized (meems) {
			meemSet = new HashSet<Meem>(meems.values());
		}
		return meemSet;
	}

	private void putMeem(MeemPath meemPath, MeemCore meemCore) {
		meems.put(meemPath, meemCore.getSelf());
		meemCores.put(meemPath, meemCore);
	}

	private void unloadMeem(MeemPath meemPath) {
		Meem meem = getMeem(meemPath);

		if (meem == null) // we don't have this meem
			return;

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Unloading Meem : " + meemPath);
		}

		// remove LifeCycle Reference

		Reference lifeCycleClientReference = (Reference) meemLifeCycleReferences.remove(meemPath);
		meem.removeOutboundReference(lifeCycleClientReference);

		meems.remove(meemPath);
		meemCores.remove(meemPath);

		LifeCycleManager lifeCycleManager = (LifeCycleManager) changeLCMValues.remove(meemPath);

		if (lifeCycleManager != null) {
			lifeCycleManagerMiscClientConduit.parentLifeCycleManagerChanged(meem, lifeCycleManager);
		}

		lifeCycleManagerMiscClientConduit.meemDeactivated(meem.getMeemPath());

		if (concluding) {
			if (meems.size() == 0) {
				lifeCycleControlConduit.vote(meemCore.getMeemPath().toString(), LifeCycleTransition.PENDING_LOADED, true);
			}
		}
	}

	private void deactivateMeem(MeemPath meemPath) {
		if (meemPath.equals(meemCore.getMeemPath()))
			return;

		Meem meem = getMeem(meemPath);

		if (meem == null) // we don't have this meem
			return;

		// set LifeCycleState to DORMANT
		lifeCycleAdapterConduit.changeLifeCycleState(meem, LifeCycleState.DORMANT);
	}

	private void registerMeem(Meem meem) {
		synchronized (registeredMeems) {
			if (!registeredMeems.contains(meem)) {

				registeredMeems.add(meem);

				// set up MeemRegistryClient Dependency
				DependencyAttribute meemRegistryClientDependencyAttribute = new DependencyAttribute(
				// DependencyType.STRONG,
				        DependencyType.WEAK, Scope.LOCAL, meemCore.getMeemRegistry(), "meemRegistryClient", new ExactMatchFilter(meem.getMeemPath()), true);

				registerMeemDependencyAttributes.put(meemRegistryClientDependencyAttribute, meem);
				registerMeemValues.put(meem, meemRegistryClientDependencyAttribute);

				dependencyHandlerConduit.addDependency("meemRegistryClientLCM", meemRegistryClientDependencyAttribute, LifeTime.TRANSIENT);
			}
		}
	}

	private void deregisterMeem(Meem meem) {
		synchronized (registeredMeems) {
			if (registeredMeems.contains(meem)) {
				meemRegistry.deregisterMeem(meem);
			}
		}
	}

	private final class MyLifeCycleClient implements LifeCycleClient, ContentClient {
		public MyLifeCycleClient(Meem meem) {
			this.meem = meem;
		}

		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			// don't care
		}

		/**
		 * 
		 */
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (meem == null) {
				// logger.log(Level.WARNING, "lifeCycleStateChanged(): meem is null ");
				return;
			}

			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				logger.log(logLevel, "lifeCycleStateChanged: meem " + meem + " : " + transition);
			}

			if (transition.equals(LifeCycleTransition.LOADED_DORMANT)) {
				deregisterMeem(meem);
			}
		}

		public void contentFailed(String reason) {
			logger.log(Level.INFO, "Content failed adding LC ref : " + reason);
		}

		public void contentSent() {

		}

		private final Meem meem;
	}

	/**
	 * @see org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagementClient#parentLifeCycleManagerChanged(org.openmaji.meem.Meem,
	 *      org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager lifeCycleManager) {
		LifeCycleManager newLifeCycleManager = (LifeCycleManager) changeLCMValues.get(meem.getMeemPath());
		if (newLifeCycleManager != null && ((Meem) lifeCycleManager).getMeemPath().equals(((Meem) newLifeCycleManager).getMeemPath())) {
			DependencyAttribute dependencyAttribute = (DependencyAttribute) changeLCMDependencyAttributes.remove(meem.getMeemPath());
			if (dependencyAttribute != null) {
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					logger.log(logLevel, "parentLifeCycleManagerChanged: removing dependency");
				}
				dependencyHandlerConduit.removeDependency(dependencyAttribute);
			}
		}
	}

	/* --------------------- FilterChecker methods --------------------- */

	/**
	 * 
	 */
	public boolean invokeMethodCheck(Filter filter, String methodName, Object[] args) throws IllegalFilterException {

		if (!(filter instanceof ExactMatchFilter)) {
			throw new IllegalFilterException("Can't check filter: " + filter);
		}

		MeemPath meemPath = null;

		if (methodName.equals("meemCreated")) {
			String identifier = (String) args[1];

			if (identifier != null) {
				if (((ExactMatchFilter) filter).getTemplate().equals(identifier))
					return (true);
			}

			meemPath = ((Meem) args[0]).getMeemPath();
		}

		if (methodName.equals("meemDestroyed")) {
			meemPath = ((Meem) args[0]).getMeemPath();
		}

		if (meemPath != null) {
			return ((ExactMatchFilter) filter).getTemplate().equals(meemPath);
		}

		return false;
	}

	/* ---------- Logging fields ----------------------------------------------- */

	private static final boolean DEBUG = false;
	
	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final Level logLevel = Common.getLogLevelVerbose();

	/* ----------- LifeCycleManagerMisc Conduit ---------------- */

	private final class LifeCycleManagerConduitImpl implements LifeCycleManagerMisc {

		/**
		 * 
		 */
		public void buildMeem(MeemPath meemPath, MeemDefinition meemDefinition, int requestId) {
			if (DEBUG) {
				logger.log(Level.INFO,  "LifeCycleManagerConduitImpl: building meem: " + meemPath + " - " + requestId);
			}
			constructMeem(meemPath, meemDefinition, requestId);
		}

		/**
		 * 
		 */
		public void addLifeCycleReference(Meem meem) {
			if (DEBUG) {
				logger.log(Level.INFO,  "LifeCycleManagerConduitImpl: addLifeCycleReference: " + meem.getMeemPath());
			}
			
			LifeCycleClient lifeCycleClient = new MyLifeCycleClient(meem);
			lifeCycleClient = (LifeCycleClient) meemCore.getTargetFor(lifeCycleClient, LifeCycleClient.class);

			Reference lifeCycleClientReference = Reference.spi.create("lifeCycleClient", lifeCycleClient, true);
			meem.addOutboundReference(lifeCycleClientReference, false);

			meemLifeCycleReferences.put(meem.getMeemPath(), lifeCycleClientReference);

			// register meem
			Integer requestId = (Integer) unregisteredMeems.get(meem.getMeemPath());
			if (requestId != null) {
				if (DEBUG) {
					logger.log(Level.INFO,  "registerMeem: " + meem.getMeemPath());
				}
				registerMeem(meem);
			}

		}

		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMisc#changeParentLifeCycleManager(org.openmaji.meem.Meem,
		 *      org.openmaji.system.manager.lifecycle.LifeCycleManager)
		 */
		public void changeParentLifeCycleManager(Meem meem, LifeCycleManager targetLifeCycleManager) {

			DependencyAttribute dependencyAttribute;

			synchronized (changeLCMValues) {
				if (changeLCMValues.containsKey(meem.getMeemPath())) {
					return;
				}

				// make sure we have our local copy of the meem, not a smart proxy meem if the call came from a remote vm
				meem = getMeem(meem.getMeemPath());
				// add dependency

				dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meem, "lifeCycleManagementClient", null, true);

				changeLCMDependencyAttributesReverse.put(dependencyAttribute, meem.getMeemPath());
				changeLCMValues.put(meem.getMeemPath(), targetLifeCycleManager);
			}

			dependencyHandlerConduit.addDependency("lifeCycleManagementClientLCM", dependencyAttribute, LifeTime.TRANSIENT);
		}
	}

	/* -----------LifeCycleManagerClient conduit ------------- */

	private final class LifeCycleManagerClientImpl implements LifeCycleManagerClient {

		/**
		 * 
		 */
		public void meemCreated(Meem meem, String identifier) {
			lifeCycleManagerClient.meemCreated(meem, identifier);

			String location = meem.getMeemPath().getLocation();
			PendingCreateMeem pendingCreateMeem = (PendingCreateMeem) pendingCreateMeems.remove(location);

			if (pendingCreateMeem != null) {
				pendingCreateMeem.resolve(meem, identifier);
			}
		}

		/**
		 * 
		 */
		public void meemDestroyed(Meem meem) {
			lifeCycleManagerClient.meemDestroyed(meem);
		}

		/**
		 * @see org.openmaji.system.manager.lifecycle.LifeCycleManagerClient#meemTransferred(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
		 */
		public void meemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
			lifeCycleManagerClient.meemTransferred(meem, targetLifeCycleManager);
		}

	}

	/* ---------------------- DependencyClient conduit ---------------- */

	private final class DependencyClientConduit implements DependencyClient {

		/**
		 * 
		 */
		public void dependencyConnected(DependencyAttribute dependencyAttribute) {
			MeemPath meemPath = changeLCMDependencyAttributesReverse.remove(dependencyAttribute);
			if (meemPath != null) {
				MeemCore meemCore = (MeemCore) meemCores.get(meemPath);
				LifeCycleManagement lifeCycleManagementFacet = (LifeCycleManagement) meemCore.getTarget("lifeCycleManagement");
				LifeCycleManager lifeCycleManager = (LifeCycleManager) changeLCMValues.get(meemPath);
				lifeCycleManagementFacet.changeParentLifeCycleManager(lifeCycleManager);
				changeLCMDependencyAttributes.put(meemPath, dependencyAttribute);
			}

			if (registerMeemDependencyAttributes.containsKey(dependencyAttribute)) {
				Meem meem = (Meem) registerMeemDependencyAttributes.get(dependencyAttribute);
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
					logger.log(logLevel, "Registering " + meem);
				}
				if (DEBUG) {
					logger.log(Level.INFO, "registering meem: " + meem + " on " + meemRegistry + " proxy to " + meemCore.getMeemRegistry());
				}
				meemRegistry.registerMeem(meem);
			}
		}

		/**
		 * 
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
			Integer requestId = (Integer) unregisteredMeems.get(meemPath);
			if (DEBUG) {
				logger.log(Level.INFO, "lifeCycleStateChanged: " + meemPath + " - " + transition + " - " + requestId);
			}
			if (requestId != null) {
				int currentStateIndex = LifeCycleState.STATES.indexOf(transition.getCurrentState());
				int loadedStateIndex = LifeCycleState.STATES.indexOf(LifeCycleState.LOADED);

				if (currentStateIndex >= loadedStateIndex) {
					if (DEBUG) {
						logger.log(Level.INFO, "notify that meem built " + meemPath);
					}
					lifeCycleManagerMiscClientConduit.meemBuilt(meemPath, getMeem(meemPath), requestId.intValue());
				}

			}
		}

		/**
		 * 
		 */
		public void lifeCycleLimitChanged(MeemPath meemPath, LifeCycleState state) {
			// don't care
		}
	}

	/* ------------ MeemRegistryClient methods ------------------- */

	/**
	 * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemDeregistered(org.openmaji.meem.Meem)
	 */
	public void meemDeregistered(Meem meem) {

		if (registeredMeems.remove(meem) == true) {

			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				logger.log(logLevel, "meemDeregistered Meem : " + meem.getMeemPath());
			}

			unloadMeem(meem.getMeemPath());
		}
	}

	/**
	 * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemRegistered(org.openmaji.meem.Meem)
	 */
	public void meemRegistered(Meem meem) {
		// remove dependency
		DependencyAttribute meemRegistryClientDependencyAttribute = (DependencyAttribute) registerMeemValues.remove(meem);
		if (meemRegistryClientDependencyAttribute != null) {
			registerMeemDependencyAttributes.remove(meemRegistryClientDependencyAttribute);
			dependencyHandlerConduit.removeDependency(meemRegistryClientDependencyAttribute);

			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				logger.log(logLevel, "Got MeemRegistered " + meem);
			}
			Integer requestId = (Integer) unregisteredMeems.remove(meem.getMeemPath());
			if (requestId != null) {
				// lifeCycleManagerMiscClientConduit.meemBuilt(meem.getMeemPath(), meem, requestId.intValue());
				lifeCycleManagerMiscClientConduit.lifeCycleReferenceAdded(meem);
			}
		}
	}

	private final class PendingCreateMeem {
		private final LifeCycleManagerClient lifeCycleManagerClient;

		private final ContentClient contentClient;

		public PendingCreateMeem(LifeCycleManagerClient lifeCycleManagerClient, ContentClient contentClient) {
			this.lifeCycleManagerClient = lifeCycleManagerClient;
			this.contentClient = contentClient;
		}

		public void resolve(Meem meem, String identifier) {
			if (meem == null) {
				contentClient.contentFailed("Could not create Meem");
			}
			else {
				lifeCycleManagerClient.meemCreated(meem, identifier);
				contentClient.contentSent();
			}
		}
	};
}
