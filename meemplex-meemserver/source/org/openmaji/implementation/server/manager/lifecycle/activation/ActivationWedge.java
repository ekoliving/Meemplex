/*
 * @(#)ActivationWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.activation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMisc;
import org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient;
import org.openmaji.implementation.server.manager.lifecycle.adapter.meemstore.MeemStoreAdapter;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceClientAdapter;
import org.openmaji.implementation.server.manager.lifecycle.adapter.persistence.PersistenceHandlerAdapter;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceClient;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;



/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class ActivationWedge implements Wedge {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	private static final boolean DEBUG = false;
	
	
	private int WEDGE_ID = this.hashCode();

	private Map<MeemPath, DefinitionContentPair> definitionContentPairs = new HashMap<MeemPath, DefinitionContentPair>();
	
	private Map<MeemPath, Meem> meems = new HashMap<MeemPath, Meem>();
	
	private Map<MeemPath, LifeCycleState> meemMaxLifeCycleStates = new HashMap<MeemPath, LifeCycleState>();

	// conduits
	public Activation activationConduit = new ActivationConduitImpl(); // inbound
	public ActivationClient activationClientConduit; // outbound

	public LifeCycleManagerMisc lifeCycleManagerMiscConduit; // outbound
	public LifeCycleManagerMiscClient lifeCycleManagerMiscClientConduit = new LifeCycleManagerMiscClientImpl(); // inbound

	public MeemDefinitionClient meemDefinitionClientConduit = new MeemDefinitionClientImpl(); // inbound
	public MeemContentClient meemContentClientConduit = new MeemContentClientImpl(); // inbound
	public MeemStoreAdapter meemStoreAdapterConduit; // outbound

	public PersistenceHandlerAdapter persistenceHandlerAdapterConduit; // outbound
	public ManagedPersistenceClient persistenceClientAdapterConduit = new PersistenceClientAdapterImpl(); // inbound
	
	public LifeCycleAdapter lifeCycleAdapterConduit; // outbound
	public LifeCycleAdapterClient lifeCycleAdapterClientConduit = new LifeCycleAdapterClientImpl(); //inbound

	
	private void buildMeem(MeemPath meemPath) {
		
		DefinitionContentPair definitionContentPair = (DefinitionContentPair) definitionContentPairs.get(meemPath);
		if (definitionContentPair != null) {
			lifeCycleManagerMiscConduit.buildMeem(meemPath, definitionContentPair.getMeemDefinition(), WEDGE_ID);
		}
	}

	private void setContent(MeemPath meemPath, Meem meem) {

		meems.put(meemPath, meem);

		DefinitionContentPair definitionContentPair = (DefinitionContentPair) definitionContentPairs.get(meemPath);

		if (definitionContentPair != null) {
			MeemContent                 meemContent       = definitionContentPair.getMeemContent();
			Map<String, Serializable>   lcPersistanceMap  = meemContent.getPersistentFields("LifeCycleWedge");
			LifeCycleState              maxState          = (LifeCycleState) lcPersistanceMap.get("maxState");

			meemMaxLifeCycleStates.put(meemPath, maxState);
			
			persistenceHandlerAdapterConduit.restore(meem, definitionContentPair.getMeemContent());
		}
	}

	/* --------------- ActivationWedge class ---------------- */

	class ActivationConduitImpl implements Activation {

		public void activate(MeemPath meemPath) {
			definitionContentPairs.put(meemPath, new DefinitionContentPair());
			meemStoreAdapterConduit.load(meemPath);
		}

	}

	/* ---------------LifeCycleManagerMiscClient class ----------- */

	class LifeCycleManagerMiscClientImpl implements LifeCycleManagerMiscClient {

		public void meemBuilt(MeemPath meemPath, Meem meem, int requestId) {
			if (requestId == WEDGE_ID) {
				if (DEBUG) {
					logger.info("matches WEDGE_ID: " + meemPath + " - " + requestId);
				}
				
				if (meem != null) {
					setContent(meemPath, meem);
				} else {
					// this meem has already been started
					definitionContentPairs.remove(meemPath);
				}
			}			
		}
		
		/**
		 * @see org.openmaji.implementation.server.manager.lifecycle.LifeCycleManagerMiscClient#parentLifeCycleManagerChanged(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
		 */
		public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager targetLifeCycleManager) {
			// Don't care
		}

		public void lifeCycleReferenceAdded(Meem meem) {
			MeemPath notifyMeemPath = meem.getMeemPath();

			Meem notifyMeem = (Meem) meems.get(notifyMeemPath);

			if (notifyMeem != null) {
				LifeCycleState maxState = (LifeCycleState) meemMaxLifeCycleStates.remove(notifyMeemPath);
				
				if (!maxState.equals(LifeCycleState.LOADED)) {
					// meem is now good to go. last thing is to make it pending - the lc wedge will bump it to ready
					// however, the lc adapter only notifies when the target state is reached. if we make it ready,
					// but the meem only goes pending, we don't get told anything
					lifeCycleAdapterConduit.changeLifeCycleState(meem, LifeCycleState.PENDING);
				} else {
					// fake going ready
					lifeCycleAdapterClientConduit.lifeCycleStateChanged(notifyMeemPath, LifeCycleTransition.LOADED_PENDING);
				}
			}
		}
		
		/**
		 * 
		 */
		public void meemDeactivated(MeemPath meemPath) {
			// don't care
		}
	}

	/* ---------------MeemDefinitionClient class ----------- */

	class MeemDefinitionClientImpl implements MeemDefinitionClient {
		public void meemDefinitionChanged(MeemPath meemPath, MeemDefinition meemDefinition) {
			DefinitionContentPair definitionContentPair = (DefinitionContentPair)
				definitionContentPairs.get(meemPath);

			if (definitionContentPair != null) {
				if (meemDefinition == null) {				
					activationClientConduit.activationFailed(meemPath);
				}

				definitionContentPair.setMeemDefinition(meemDefinition);

				if (definitionContentPair.getMeemContent() != null) {
					buildMeem(meemPath);
				}
			}
		}
	}

	/* ---------------MeemContentClient class ----------- */

	class MeemContentClientImpl implements MeemContentClient {
		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			DefinitionContentPair definitionContentPair = (DefinitionContentPair)
				definitionContentPairs.get(meemPath);

			if (definitionContentPair != null) {
				definitionContentPair.setMeemContent(meemContent);

				if (definitionContentPair.getMeemDefinition() != null) {
					buildMeem(meemPath);
				}
			}
		}
	}

	/* -------------- PersistenceClientAdapter class -------- */

	class PersistenceClientAdapterImpl implements PersistenceClientAdapter {

		public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
			// don't care
		}

		public void restored(MeemPath meemPath) {
			//System.err.println("PersistenceClientAdapterImpl.restored: " + meemPath);
			Meem meem = (Meem) meems.get(meemPath);

			// the meem should always be in the list, but just in case
			if (meem != null) {
				// register it
				lifeCycleManagerMiscConduit.addLifeCycleReference(meem);
			}
			else
			{
				//System.err.println("PersistenceClientAdapterImpl.restored: Meem not in list!!");
			}

		}

	}

	/* ------------LifeCycleAdapterClientImpl class ------------ */
	
	class LifeCycleAdapterClientImpl implements LifeCycleAdapterClient {
		
		/**
		 * @see org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient#lifeCycleLimitChanged(org.openmaji.meem.MeemPath, org.openmaji.meem.wedge.lifecycle.LifeCycleState)
		 */
		public void lifeCycleLimitChanged(MeemPath meemPath, LifeCycleState state) {
			// don't care

		}
		/**
		 * @see org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient#lifeCycleStateChanged(org.openmaji.meem.MeemPath, org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
		 */
		public void lifeCycleStateChanged(MeemPath meemPath, LifeCycleTransition transition) {
			if (!meems.containsKey(meemPath)) {
				return;
			}
			int index = LifeCycleState.STATES.indexOf(transition.getCurrentState());
			int indexPending = LifeCycleState.STATES.indexOf(LifeCycleState.PENDING);
			if (index >= indexPending) {
//			if (transition.equals(LifeCycleTransition.PENDING_READY)) {
				Meem notifyMeem = (Meem) meems.remove(meemPath);
	
				if (notifyMeem != null) {
					DefinitionContentPair pair = (DefinitionContentPair) definitionContentPairs.remove(meemPath);
	
					if (pair != null) {
						// lets give it back.
						activationClientConduit.activated(meemPath, notifyMeem, pair.getMeemDefinition());
					}
				}
			}
		}
}
	
	/* ------------- DefinitionContentPair class --------- */

	class DefinitionContentPair {
		private MeemDefinition meemDefinition;
		private MeemContent meemContent;

		public MeemContent getMeemContent() {
			return meemContent;
		}

		public MeemDefinition getMeemDefinition() {
			return meemDefinition;
		}

		public void setMeemContent(MeemContent content) {
			meemContent = content;
		}

		public void setMeemDefinition(MeemDefinition definition) {
			meemDefinition = definition;
		}

	}
}
