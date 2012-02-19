/*
 * @(#)TransientLifeCycleManagerWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.transitory;

import java.util.*;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.lifecycle.*;
import org.openmaji.implementation.server.manager.lifecycle.category.LifeCycleManagerCategoryConduit;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;
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
public class TransientLifeCycleManagerWedge implements LifeCycleManager, Wedge 
{

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */
	private static final Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */
	private static final int logLevel = Common.getLogLevelVerbose();
		
	private final static boolean DEBUG = false;
	
	private int WEDGE_ID = this.hashCode();

	private Map initialLifeCycleStates = new HashMap();
	private Map meemDefinitions = new HashMap();
	private Map meems = new HashMap();

	private Set categoryPaths = new HashSet();


	/* ---------------------------------- conduits ---------------------------------- */

	public LifeCycleManagerMisc lifeCycleManagerMiscConduit; // outbound
	public LifeCycleManagerMiscClient lifeCycleManagerMiscClientConduit = new LifeCycleManagerMiscClientImpl(); // inbound

	public LifeCycleAdapter lifeCycleAdapterConduit; // outbound
	public LifeCycleAdapterClient lifeCycleAdapterClientConduit = new LifeCycleAdapterClientConduit(); // inbound
	
	public LifeCycleManager lifeCycleManagerConduit = this;
	public LifeCycleManagerClient lifeCycleManagerClientConduit; // outbound
	
	public LifeCycleManagerCategoryConduit lifeCycleManagerCategoryConduit; // outbound

	public DependencyHandler dependencyHandlerConduit; // outbound
	
//	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClient() {
//		public void lifeCycleStateChanged(LifeCycleTransition transition) {};
//		public void lifeCycleStateChanging(LifeCycleTransition transition) {
//			if (transition.getCurrentState().equals(LifeCycleState.DORMANT)) {
//				lifeCycleManagerCategoryConduit.removeEntry(entryName);
//			}
//		};
//	};

	public InternalMeemFactory internalMeemFactoryConduit = new InternalMeemFactory() {
		public void createMeem(MeemDefinition meemDefinition, LifeCycleState lifeCycleState, String location) {
			MeemPath meemPath = MeemPath.spi.create(Space.TRANSIENT, location);

			initialLifeCycleStates.put(meemPath, lifeCycleState);
			meemDefinitions.put(meemPath, meemDefinition);

			categoryPaths.add(meemPath);

			lifeCycleManagerMiscConduit.buildMeem(meemPath, meemDefinition, WEDGE_ID);
		}
	};


	/* --------------------- LifeCycleManager methods --------------------- */

	/**
	 * 
	 */
	public void createMeem(MeemDefinition meemDefinition, LifeCycleState initialState) throws IllegalArgumentException {
		UID uid = UID.spi.create();
		MeemPath meemPath = MeemPath.spi.create(Space.TRANSIENT, uid.getUIDString());

		initialLifeCycleStates.put(meemPath, initialState);
		meemDefinitions.put(meemPath, meemDefinition);

		categoryPaths.add(meemPath);

		if (DEBUG) {
			LogTools.info(logger, "building meem: " + meemDefinition.getMeemAttribute().getIdentifier());
		}
		lifeCycleManagerMiscConduit.buildMeem(meemPath, meemDefinition, WEDGE_ID);
	}

	/**
	 * 
	 */
	public void destroyMeem(Meem meem) {
		MeemPath meemPath = meem.getMeemPath();

		if (!meemPath.getSpace().equals(Space.TRANSIENT)) {
			return;
		}
		
		lifeCycleAdapterConduit.changeLifeCycleState(meem, LifeCycleState.DORMANT);

		lifeCycleManagerCategoryConduit.removeEntry(meemPath.getLocation());

		lifeCycleManagerClientConduit.meemDestroyed(meem);
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.LifeCycleManager#transferMeem(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void transferMeem(Meem meem, LifeCycleManager targetLifeCycleManager) {

	}
	
	private void notifyClients(MeemPath meemPath) {
		if (DEBUG) {
			LogTools.info(logger, "notifying clients of meem created: " + meemPath);
		}
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
				LogTools.info(logger, "meem built: " + meemPath);
			}

			if (requestId == WEDGE_ID) {
				if (DEBUG) {
					LogTools.info(logger, "meem built request is from this: " + meemPath);
				}
				meems.put(meemPath, meem);

				lifeCycleManagerMiscConduit.addLifeCycleReference(meem);				
			}
		}

		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient#parentLifeCycleManagerChanged(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
		 */
		public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager targetLifeCycleManager) {
		}

		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient#lifeCycleReferenceAdded(org.openmaji.meem.Meem)
		 */
		public void lifeCycleReferenceAdded(Meem meem) {
			if (DEBUG) {
				LogTools.info(logger, "lifeCycleReferenceAdded: " + meem);
			}
			if (meems.containsValue(meem)) {
				if (DEBUG) {
					LogTools.info(logger, "lifeCycleReferenceAdded is for this: " + meem);
				}
				LifeCycleState initialState = (LifeCycleState) initialLifeCycleStates.get(meem.getMeemPath());
				
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
		
		public void meemDeactivated(MeemPath meemPath) {
			if (DEBUG) {
				LogTools.info(logger, "meemDeactivated: " + meemPath);
			}
			
			// remove lifecycle category entry
			lifeCycleManagerCategoryConduit.removeEntry(meemPath.getLocation());
		}

	}
	
	/*---------------- LifeCycleAdapterClientConduit ----------------------------------*/

	private final class LifeCycleAdapterClientConduit implements LifeCycleAdapterClient {

		public void lifeCycleStateChanged(MeemPath meemPath, LifeCycleTransition transition) {
			if (DEBUG) {
				LogTools.info(logger, "lifeCycleStateChanged: " + meemPath + " - " + transition);
			}
			Meem meem = (Meem) meems.get(meemPath);
			if (meem != null) {
				LifeCycleState initialState = (LifeCycleState) initialLifeCycleStates.get(meemPath);
				
				//if (transition.getCurrentState().equals(initialState)) {
				if (!initialState.equals(LifeCycleState.LOADED) && transition.getCurrentState().equals(initialState)) {
					notifyClients(meemPath);
				}
			}
			
			// if meem is absent remove the entry
			if (transition.getCurrentState().equals(LifeCycleState.ABSENT)) {
				lifeCycleManagerCategoryConduit.removeEntry(meemPath.getLocation());
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

}