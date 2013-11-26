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
package org.openmaji.implementation.server.manager.lifecycle.essential;

import java.security.Key;
import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.classloader.MajiClassLoader;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapter;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManager;
import org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManagerClient;
import org.openmaji.implementation.server.manager.lifecycle.transitory.TransientLifeCycleManagerMeem;
import org.openmaji.implementation.server.manager.registry.MeemRegistryGatewayWedge;
import org.openmaji.implementation.server.meem.core.*;
import org.openmaji.implementation.server.meem.invocation.InvocationContext;
import org.openmaji.implementation.server.meem.invocation.InvocationContextTracker;
import org.openmaji.implementation.server.security.auth.MajiKeyStore;
import org.openmaji.implementation.server.security.auth.MeemCoreRootAuthority;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class EssentialLifeCycleManagerWedge implements Wedge, EssentialLifeCycleManager, MeemkitLifeCycleManagerClient {

	public static final String MEEMSERVER_REQUIRE_LICENSE = "org.openmaji.server.requires.license";

	private boolean modeBootstrap = false;

	private boolean modeCommenced = false;

	private boolean lifeCycleManagementStarted = false;

	private boolean selfRestored = false;

	private boolean meemkitLCMStarted = false;

	private Reference<?> meemkitLCMStartedReference;

	private String meemServerName = null;

	private MeemCore selfAsMeemCore = null;

	private Map<String, MeemCore> essentialMeemCores = new HashMap<String, MeemCore>();

	// essential meem cores
	private MeemCore meemRegistryMeemCore = null;

	private MeemCore threadManagerMeemCore = null;

	private MeemCore meemServerControllerMeemCore = null;

	private Meem meemStoreMeem = null;

	// conduits
	public MeemStoreAdapter meemStoreAdapterConduit; // outbound

	public MeemContentClient meemContentClientConduit = new MeemContentClientImpl(); // inbound

	public ManagedPersistenceClient managedPersistenceClientConduit = new PersistenceClientImpl(); // inbound

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

		EssentialMeemHelper.setEssentialMeem(EssentialLifeCycleManagerMeem.spi.getIdentifier(), selfAsMeemCore.getSelf());

		// set the current meempath in the invocation context
		InvocationContextTracker.getInvocationContext().put(InvocationContext.CURRENT_MEEM_PATH, meemPath);

		AccessControl control = selfAsMeemCore.getConduitSource("accessControl", AccessControl.class);

		control.addAccess(Principals.OTHER, AccessLevel.READ_WRITE);

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Created essential Meem: " + EssentialLifeCycleManager.spi.getIdentifier() + " : " + meemPath);
		}
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
			logger.log(logLevel, "Registering Essential Meems");
		}

		MeemRegistry meemRegistryFacet = (MeemRegistry) meemRegistryMeemCore.getTarget("meemRegistry");

		Iterator meemIterator = essentialMeemCores.values().iterator();

		while (meemIterator.hasNext()) {
			MeemCore meemCore = (MeemCore) meemIterator.next();

			((MeemCoreImpl) meemCore).initialize(meemRegistryMeemCore.getSelf(), selfAsMeemCore.getSelf(), null, threadManagerMeemCore.getSelf(), null, meemStoreMeem);

			meemRegistryFacet.registerMeem(meemCore.getSelf());
		}

		// Pass 2. Set the meems lifeCycleManger

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Setting Essential Meems LifeCycleManager");
		}

		meemIterator = essentialMeemCores.values().iterator();

		while (meemIterator.hasNext()) {
			MeemCore meemCore = (MeemCore) meemIterator.next();

			LifeCycleManagement lifeCycleManagementFacet = (LifeCycleManagement) meemCore.getTarget("lifeCycleManagement");

			lifeCycleManagementFacet.changeParentLifeCycleManager((LifeCycleManager) selfAsMeemCore.getTarget("lifeCycleManager"));
		}

		// Pass 4. Activate and make ready. This will cause the meems to commence

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
			logger.log(logLevel, "Making Essential Meems ready");
		}

		meemIterator = essentialMeemCores.values().iterator();

		while (meemIterator.hasNext()) {
			MeemCore meemCore = (MeemCore) meemIterator.next();

			if (meemCore != selfAsMeemCore && meemCore != meemServerControllerMeemCore) {
				changeLifeCycleState(meemCore, LifeCycleState.READY);
			}
			else if (meemCore == selfAsMeemCore) {
				changeLifeCycleState(meemCore, LifeCycleState.LOADED);
			}
			// -mg- do we need to do this for essential meems?
			// addLifeCycleReference(meem);

		}

		// set the transient LCM for this meemserver
		Meem transientLCM = ((MeemCore) essentialMeemCores.get(TransientLifeCycleManagerMeem.spi.getIdentifier())).getSelf();
		LifeCycleManagerHelper.setTransientLCM(transientLCM);

		modeCommenced = true;

	}

	/*
	 * This loads our content out meemstore and starts all of the meems we are responsible for.
	 */
	private void startLifeCycleManagement() {
		// load our content out of meemstore

		meemStoreAdapterConduit.load(selfAsMeemCore.getMeemPath());

	}

	/**
	 * This can only be used during bootstrap. Because the LifeCycleManager facet isn't exposed for this wedge, the only way to call this is to have a direct reference to this
	 * class. The only person to have this is Genesis.
	 */
	public void createEssentialMeem(MeemDefinition meemDefinition, LifeCycleState initialState) throws IllegalArgumentException {
		//logger.log(Level.INFO, "creating essential meem: " + meemDefinition);
		
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

			AccessControl control = meemCore.getConduitSource("accessControl", AccessControl.class);

			control.addAccess(Principals.OTHER, AccessLevel.READ_WRITE);

			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				logger.log(logLevel, "Created essential Meem: " + meemDefinition.getMeemAttribute().getIdentifier() + " : " + meemPath);
			}

			if (meemCore.isA(MeemRegistry.class) && meemDefinition.getMeemAttribute().getIdentifier().equals(MeemRegistry.spi.getIdentifier())) {
				if (meemRegistryMeemCore == null) {
					meemRegistryMeemCore = meemCore;
				}

				MeemRegistryGatewayWedge.addLocalRegistry(meemCore.getSelf());

			}
			else if (meemCore.isA(ThreadManager.class) && threadManagerMeemCore == null) {
				threadManagerMeemCore = meemCore;
			}
			else if (meemCore.isA(MeemStore.class) && meemStoreMeem == null) {
				meemStoreMeem = meemCore.getSelf();
			}
			else if (meemCore.isA(MeemServerController.class) && meemServerControllerMeemCore == null) {
				// -mg- this is a hack to stop the MeemServerController from creating a new meemserver meem b4 the
				// ELCM has been restored.
				meemServerControllerMeemCore = meemCore;
			}
			else if (meemCore.isA(MeemkitLifeCycleManager.class)) {
				meemkitLCMStartedReference = Reference.spi.create("meemkitLifeCycleManagerClient", selfAsMeemCore.getTarget("meemkitLifeCycleManagerClient"), false);
				meemCore.getSelf().addOutboundReference(meemkitLCMStartedReference, false);
			}
		}

	}

	private void changeLifeCycleState(MeemCore meemCore, LifeCycleState lifeCycleState) {

		LifeCycle lifeCycleFacet = (LifeCycle) meemCore.getTarget("lifeCycle");

		lifeCycleFacet.changeLifeCycleState(lifeCycleState);

	}

	private void startSelf() {
		if (meemkitLCMStarted && selfRestored) {
			// check to see if we are licensed

			if (!checkMeemServerLicense()) {
				return;
			}

			// make ourself ready

			changeLifeCycleState(selfAsMeemCore, LifeCycleState.READY);

			changeLifeCycleState(meemServerControllerMeemCore, LifeCycleState.READY);

		}
	}

	private boolean checkMeemServerLicense() {
		String requireLicense = System.getProperty(MEEMSERVER_REQUIRE_LICENSE, "false").trim();
		if (!requireLicense.equalsIgnoreCase("true")) {
			return true;
		}

		Key privateKey;

		try {

			String keyStorePasswd = System.getProperty(MeemCoreRootAuthority.KEYSTORE_PASSWD);
			if (keyStorePasswd == null) {
				throw new RuntimeException("unable to find property for key store password.");
			}

			MajiKeyStore keyStore = MeemCoreRootAuthority.getMajiKeyStore();

			privateKey = keyStore.getKey("license", keyStorePasswd.toCharArray());

		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Unable to get access to licence key: " + e.toString());

			return false;
		}

		if (privateKey == null) {
			logger.log(Level.WARNING, "No license key - this meem server has not been licensed");

			return false;
		}
		else {
			return true;
		}
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

				selfRestored = true;

				if (System.getProperty(MajiClassLoader.CLASSPATH_FILE) == null) {
					meemkitLCMStarted = true;
				}

				startSelf();
			}
		}

		/**
		 * 
		 */
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			// Don't care
		}

	}

	/* --------------------- LifeCycleClient conduit ---------------- */

	class LifeCycleClientImpl extends LifeCycleClientAdapter {

		LifeCycleClientImpl(Wedge parent) {
			super(parent);
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {

			if (transition.equals(LifeCycleTransition.DORMANT_LOADED)) {
				if (!lifeCycleManagementStarted) {
					lifeCycleManagementStarted = true;

					startLifeCycleManagement();
				}
			}
		}

	}

	// public LifeCycleManagerShutdownClient lifeCycleManagerShutdownClientConduit = new LifeCycleManagerShutdownClient() {
	// public void shutdown() {
	// if (selfRestored) {
	// System.out.println("SHUTTING DOWN1");
	// try {
	// // -mg- This is dodgy. We need a better way to wait for meemstore to finish writing
	// Thread.sleep(3000);
	// } catch (InterruptedException e) {
	// // -mg- Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// System.out.println("SHUTTING DOWN2");

	// deactivate all of our meems
	// Iterator meemIterator = essentialMeemCores.values().iterator();
	//
	// while (meemIterator.hasNext()) {
	// MeemCore meemCore = (MeemCore) meemIterator.next();
	//
	// if (meemCore != selfAsMeemCore && meemCore != threadManagerMeemCore) {
	// changeLifeCycleState(meemCore, LifeCycleState.DORMANT);
	// }
	// }
	// changeLifeCycleState(selfAsMeemCore, LifeCycleState.DORMANT);
	// changeLifeCycleState(threadManagerMeemCore,LifeCycleState.DORMANT);
	// System.out.println("SHUT DOWN DONE");
	// }
	// }
	// };

	public void conclude() {
		if (selfRestored) {
			// deactivate all of our meems
			Iterator<MeemCore> meemIterator = essentialMeemCores.values().iterator();

			while (meemIterator.hasNext()) {
				MeemCore meemCore = (MeemCore) meemIterator.next();

				if (meemCore != selfAsMeemCore) {
					changeLifeCycleState(meemCore, LifeCycleState.DORMANT);
				}
			}
		}

	}

	/* --------- MeemkitLCMClient methods ---------------- */

	/**
	 * @see org.openmaji.implementation.server.manager.lifecycle.meemkit.MeemkitLifeCycleManagerClient#classLoadingCompleted()
	 */
	public void classLoadingCompleted() {
		//logger.log(Level.INFO, "Meemkit classloaders started");
		meemkitLCMStarted = true;
		startSelf();
	}

	/* --------------------- Logging fields ----------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static final Level logLevel = Common.getLogLevel();

}
