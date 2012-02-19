/*
 * @(#)EssentialLifeCycleManagerWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.manager.lifecycle.essential;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerShutdownClient;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapter;
import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem;
import org.openmaji.implementation.server.manager.registry.MeemRegistryGatewayWedge;
import org.openmaji.implementation.server.meem.core.*;



import org.openmaji.meem.*;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.server.helper.*;
import org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.registry.MeemRegistry;
import org.openmaji.system.manager.thread.ThreadManager;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.hook.security.AccessControl;
import org.openmaji.system.meem.hook.security.AccessLevel;
import org.openmaji.system.meem.hook.security.Principals;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagement;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.meemserver.controller.MeemServerController;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.utility.uid.UID;
import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class EssentialLifeCycleManagerWedge implements Wedge,EssentialLifeCycleManager {

	private boolean modeBootstrap = false;
	private boolean modeCommenced = false;
	private boolean lifeCycleManagementStarted = false;

	private boolean selfRestored = false;

	private String meemServerName = null;

	private MeemCore selfAsMeemCore = null;

	private Map essentialMeemCores = new HashMap();

	// essential meem cores
	private MeemCore meemRegistryMeemCore = null;
	private MeemCore threadManagerMeemCore = null;
	private MeemCore meemServerControllerMeemCore = null;

	private Meem meemStoreMeem = null;

	// conduits
	public MeemStoreAdapter meemStoreAdapterConduit; //outbound

	public MeemContentClient meemContentClientConduit = new MeemContentClientImpl(); //inbound
	public ManagedPersistenceClient managedPersistenceClientConduit = new PersistenceClientImpl(); //inbound

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientImpl(this);
	
	/**
	 * @see org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager#bootstrap()
	 */
	public void bootstrap() throws RuntimeException {
		if (modeBootstrap) {
			throw new RuntimeException("LifeCycleManager has already entered bootstrap mode");
		}

		if (modeCommenced) {
			throw new RuntimeException("Can't enter bootstrap mode after LifeCycleManager has commenced");
		}

		modeBootstrap = true;

		// Turn ourself into a meem

		meemServerName = MeemServer.spi.getName();

		UID uid = UID.spi.createSHA1(meemServerName);

		MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, uid.getUIDString());

		MeemBuilder meemBuilder = MeemBuilder.spi.create(meemPath);

		selfAsMeemCore = meemBuilder.getMeemCore();

		meemBuilder.addMeemDefinition(new EssentialLifeCycleManagerMeem().getMeemDefinition(), this);

		essentialMeemCores.put(EssentialLifeCycleManagerMeem.spi.getIdentifier(), selfAsMeemCore);


		EssentialMeemHelper.setEssentialMeem(
			EssentialLifeCycleManagerMeem.spi.getIdentifier(),
			selfAsMeemCore.getSelf());

		AccessControl control = (AccessControl)selfAsMeemCore.getConduitSource("accessControl", AccessControl.class);
			
		control.addAccess(Principals.OTHER, AccessLevel.READ_WRITE);
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.EssentialLifeCycleManager#start()
	 */
	public void start() {
		if (modeCommenced) {
			throw new RuntimeException("LifeCycleManager has already commenced operation");
		}

		// --------------------------------
		// Lock the list of essential meems

		EssentialMeemHelper.lock();

		// ---------------------------------------------------------
		// Register all Meems and prepare them to commence operation

		// Pass 1, register them so that any essenital meem specific setting can 
		// be restored from persistent storage

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel, "Registering Essential Meems");
		}

		MeemRegistry meemRegistryFacet = (MeemRegistry) meemRegistryMeemCore.getTarget("meemRegistry");

		Iterator meemIterator = essentialMeemCores.values().iterator();

		while (meemIterator.hasNext()) {
			MeemCore meemCore = (MeemCore) meemIterator.next();

			((MeemCoreImpl) meemCore).initialize(
				meemRegistryMeemCore.getSelf(),
				selfAsMeemCore.getSelf(),
				null,
        		threadManagerMeemCore.getSelf(),
				null,
				meemStoreMeem);

			meemRegistryFacet.registerMeem(meemCore.getSelf());
		}

		//	Pass 2. Set the meems lifeCycleManger

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel, "Setting Essential Meems LifeCycleManager");
		}

		meemIterator = essentialMeemCores.values().iterator();

		while (meemIterator.hasNext()) {
			MeemCore meemCore = (MeemCore) meemIterator.next();

			LifeCycleManagement lifeCycleManagementFacet = (LifeCycleManagement) meemCore.getTarget("lifeCycleManagement");

			lifeCycleManagementFacet.changeParentLifeCycleManager((LifeCycleManager) selfAsMeemCore.getTarget("lifeCycleManager"));
		}

		//	Pass 4. Activate and make ready. This will cause the meems to commence

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			LogTools.trace(logger, logLevel, "Making Essential Meems ready");
		}

		meemIterator = essentialMeemCores.values().iterator();

		while (meemIterator.hasNext()) {
			MeemCore meemCore = (MeemCore) meemIterator.next();

			if (meemCore != selfAsMeemCore && meemCore != meemServerControllerMeemCore) {
				changeLifeCycleState(meemCore, LifeCycleState.READY);
			} else if (meemCore == selfAsMeemCore) {
				changeLifeCycleState(meemCore, LifeCycleState.LOADED);
			}
			// -mg- do we need to do this for essential meems?
			// addLifeCycleReference(meem);

		}

		// set the transient LCM for this meemserver
		Meem transientLCM = ((MeemCore) essentialMeemCores.get(
    TransientLifeCycleManagerMeem.spi.getIdentifier())).getSelf();
		LifeCycleManagerHelper.setTransientLCM(transientLCM);

		modeCommenced = true;

	}

	/*
	 * This loads our content out meemstore and starts all of the meems we
	 * are responsible for.
	 */
	private void startLifeCycleManagement() {
		// load our content out of meemstore

		meemStoreAdapterConduit.load(selfAsMeemCore.getMeemPath());

	}

	/**
	 * This can only be used during bootstrap. Because the LifeCycleManager facet isn't exposed
	 * for this wedge, the only way to call this is to have a direct reference to this class.
	 * The only person to have this is Genesis.
	 */
	public void createEssentialMeem(MeemDefinition meemDefinition, LifeCycleState initialState) throws IllegalArgumentException {
		if (modeCommenced) {
			// This should never get called as the LifeCycleManager Facet isn't exposed on this wedge
			throw new RuntimeException("createMeem: This should not have been called");
		}

		if (modeBootstrap) {

			UID uid = UID.spi.create();
			MeemPath meemPath = MeemPath.spi.create(Space.TRANSIENT, uid.getUIDString());

			MeemBuilder meemBuilder = MeemBuilder.spi.create(meemPath);
			MeemCore meemCore = meemBuilder.getMeemCore();

			meemBuilder.addMeemDefinition(meemDefinition);


			EssentialMeemHelper.setEssentialMeem(meemDefinition.getMeemAttribute().getIdentifier(), meemCore.getSelf());

			essentialMeemCores.put(meemDefinition.getMeemAttribute().getIdentifier(), meemCore);
			
			AccessControl control = (AccessControl)meemCore.getConduitSource("accessControl", AccessControl.class);
			
			control.addAccess(Principals.OTHER, AccessLevel.READ_WRITE);

			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(
					logger,
					logLevel,
					"Created essential Meem: " + meemDefinition.getMeemAttribute().getIdentifier() + " : " + meemPath);
			}

			if (meemCore.isA(MeemRegistry.class) && meemDefinition.getMeemAttribute().getIdentifier().equals(MeemRegistry.spi.getIdentifier())) {
				if (meemRegistryMeemCore == null) {
					meemRegistryMeemCore = meemCore;
				}

				MeemRegistryGatewayWedge.addLocalRegistry(meemCore.getSelf());

			} else if (meemCore.isA(ThreadManager.class) && threadManagerMeemCore == null) {
        threadManagerMeemCore = meemCore;
			} else if (meemCore.isA(MeemStore.class) && meemStoreMeem == null) {
				meemStoreMeem = meemCore.getSelf();
			} else if (meemCore.isA(MeemServerController.class) && meemServerControllerMeemCore == null) {
				// -mg- this is a hack to stop the MeemServerController from creating a new meemserver meem b4 the 
				// ELCM has been restored.
				meemServerControllerMeemCore = meemCore;
			}
		}

	}

	private void changeLifeCycleState(MeemCore meemCore, LifeCycleState lifeCycleState) {

		LifeCycle lifeCycleFacet = (LifeCycle) meemCore.getTarget("lifeCycle");

		lifeCycleFacet.changeLifeCycleState(lifeCycleState);

	}

	/* ---------------------- MeemContentClient conduit ----------------- */

	class MeemContentClientImpl implements MeemContentClient {

		/**
		 * @see org.openmaji.system.space.meemstore.MeemContentClient#meemContentChanged(org.openmaji.meem.MeemPath, org.openmaji.system.meem.definition.MeemContent)
		 */
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			MeemCore meemCore = null;

			if (!selfRestored && meemPath.equals(selfAsMeemCore.getMeemPath())) {
				meemCore = selfAsMeemCore;
			}

			if (meemCore != null) {
				// make meem loaded 

				changeLifeCycleState(meemCore, LifeCycleState.LOADED);

				// restore

				ManagedPersistenceHandler persistenceHandler = (ManagedPersistenceHandler) meemCore.getTarget("managedPersistenceHandler");

				persistenceHandler.restore(meemContent);
				
				
			}
		}

	}

	/* ---------------------- ManagedPersistenceClient conduit ----------------- */

	class PersistenceClientImpl implements ManagedPersistenceClient {

		/**
		 * @see org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient#restored(org.openmaji.meem.MeemPath)
		 */
		public void restored(MeemPath meemPath) {

			if (meemPath.equals(selfAsMeemCore.getMeemPath())) {

				// make ourself ready

				changeLifeCycleState(selfAsMeemCore, LifeCycleState.READY);
				
				changeLifeCycleState(meemServerControllerMeemCore, LifeCycleState.READY);
				selfRestored = true;
			}
		}

		/**
		 */
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			// Don't care
		}

	}

	/* --------------------- LifeCycleClient conduit ----------------*/

	class LifeCycleClientImpl extends LifeCycleClientAdapter {
		
		LifeCycleClientImpl(
			Wedge	parent)
		{
			super(parent);
		}

		/**
		 */
		public void lifeCycleStateChanged(LifeCycleTransition transition) {

			if (transition.equals(LifeCycleTransition.DORMANT_LOADED)) {
				if (!lifeCycleManagementStarted) {
					lifeCycleManagementStarted = true;

					startLifeCycleManagement();
				}
			}
		}

	}

	public LifeCycleManagerShutdownClient lifeCycleManagerShutdownClientConduit = new LifeCycleManagerShutdownClient() {
	 	public void shutdown() {
			if (selfRestored) {		
				try {
					// -mg- This is dodgy. We need a better way to wait for meemstore to finish writing
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// -mg- Auto-generated catch block
					e.printStackTrace();
				}
				
				
//				 deactivate all of our meems
				Iterator meemIterator = essentialMeemCores.values().iterator();
	
				while (meemIterator.hasNext()) {
					MeemCore meemCore = (MeemCore) meemIterator.next();
					if (meemCore != selfAsMeemCore) {
						changeLifeCycleState(meemCore, LifeCycleState.DORMANT);					
					}
				}
			}
		}
	};


	/* --------------------- Logging fields ----------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static int logLevel = Common.getLogLevel();

}
