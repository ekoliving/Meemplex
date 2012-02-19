/*
 * @(#)LifeCycleWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meem.wedge.lifecycle;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.Common;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagement;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagementClient;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.meem.wedge.reference.MeemClientCallback;
import org.openmaji.system.meem.wedge.reference.MeemClientConduit;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LifeCycleWedge implements LifeCycle, LifeCycleLimit, LifeCycleManagement, Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();
	private static final Level LOG_LEVEL = Level.FINE;

	public MeemCore meemCore;
	public LifeCycleState maxState = LifeCycleState.READY;

	private LifeCycleState currentState = LifeCycleState.DORMANT;
	private LifeCycleTransition lastTransition = LifeCycleTransition.ABSENT_DORMANT;
	
	private boolean destroying = false;

	//outbound facets
	public LifeCycleClient lifeCycleClient = null;
	public final ContentProvider lifeCycleClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) {
			LifeCycleClient client = (LifeCycleClient) target;
			client.lifeCycleStateChanged(lastTransition);
		}
	};

	public LifeCycleLimit lifeCycleLimitClient = null;
	public final ContentProvider lifeCycleLimitClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) {
			LifeCycleLimit lifeCycleLimitClient = (LifeCycleLimit) target;
			lifeCycleLimitClient.limitLifeCycleState(maxState);
		}
	};

	public LifeCycleManagementClient lifeCycleManagementClient = null;
	public final ContentProvider lifeCycleManagementClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) {
			LifeCycleManagementClient client = (LifeCycleManagementClient) target;
			client.parentLifeCycleManagerChanged(meemCore.getSelf(), parentLifeCycleManager);
		}
	};


	/*
	 * This is the field that stores the Meems parent LifeCycleManager.
	 * It really shouldn't be public, but until persistence is sorted out a 
	 * bit more, it has to be.
	 */
	// -mg- make sure this doesn't stay public 
	public MeemPath parentLifeCycleManagerMeemPath = null;
	private LifeCycleManager parentLifeCycleManager = null;

	// conduits
	public LifeCycle lifeCycleConduit = this;
	public Vote lifeCycleControlConduit = new LifeCycleControlConduit(); // inbound
	public MeemClientConduit meemClientConduit;
	public ManagedPersistenceHandler managedPersistenceHandlerConduit;
	public LifeCycleClient lifeCycleClientConduit;

	// voting lists
	private Map<String, Boolean> systemWedgeVotes = new HashMap<String, Boolean>();
	private Map<String, Boolean> applicationWedgeVotes = new HashMap<String, Boolean>();

	private Map<LifeCycleTransition, Vector<String>> transitionVotes = new HashMap<LifeCycleTransition, Vector<String>>();
	private LifeCycleTransition waitingTransition = null;
	private LifeCycleState waitingTargetState = null;
	
	
	/**
	 * @see org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagement#changeParentLifeCycleManager(org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void changeParentLifeCycleManager(LifeCycleManager lifeCycleManager) {
		/* -mg- 
		 * This should check to make sure the call came from the current parent LCM
		 * but can't do this at the moment, so we'll pretend it did.
		 */
		LifeCycleManager oldLCM = parentLifeCycleManager;
		parentLifeCycleManager = lifeCycleManager;
		parentLifeCycleManagerMeemPath = ((Meem) lifeCycleManager).getMeemPath();

		// persist ourself
		if (oldLCM != null) {
			managedPersistenceHandlerConduit.persist();
		}
		
		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
			logger.log(LOG_LEVEL, "Parent LifeCycleManager changed: " + lifeCycleManager + " Meem: " + meemCore.getMeemPath());
		}

		// notify clients		
		lifeCycleManagementClient.parentLifeCycleManagerChanged(meemCore.getSelf(), lifeCycleManager);

		if (oldLCM != null) {
			// take ourself to dormant
			changeLifeCycleState(LifeCycleState.DORMANT);
		}
	}

	/**
	 */
	public void limitLifeCycleState(LifeCycleState state) {
		if (state.equals(LifeCycleState.PENDING)) {
			state = LifeCycleState.READY;
		}

		maxState = state;

		int currentStateIndex = LifeCycleState.STATES.indexOf(currentState);
		int maxStateIndex = LifeCycleState.STATES.indexOf(maxState);

		if (currentStateIndex > maxStateIndex) {
			performTransitions(maxState);
		}
		
		lifeCycleLimitClient.limitLifeCycleState(state);
	}

	/**
	 */
	public void changeLifeCycleState(LifeCycleState newState) {

		//LogTools.info(logger, "meem changing LC state: " + meemCore.getMeemPath() + " - " + newState);
		
		// make sure the state is changing
		if (newState.equals(currentState))
			return;

		if (newState.equals(LifeCycleState.PENDING)) {
			newState = LifeCycleState.READY;
		}
		
		if (newState.equals(LifeCycleState.ABSENT) && !destroying) {
			destroying = true;
			destroySelf();
			return;
		}

		performTransitions(newState);
	}

	/**
	 * @param newState
	 */
	private void performTransitions(LifeCycleState newState) {

		// make sure we can get to the requested state

		int newStateIndex = LifeCycleState.STATES.indexOf(newState);
		int maxStateIndex = LifeCycleState.STATES.indexOf(maxState);

		if (newStateIndex > maxStateIndex) {
			newStateIndex = maxStateIndex;
		}

		int currentStateIndex = LifeCycleState.STATES.indexOf(currentState);

		int increment = currentStateIndex < newStateIndex ? 1 : -1;

		while (currentStateIndex != newStateIndex) {

			int nextStateIndex = currentStateIndex + increment;

			LifeCycleTransition transition =
				new LifeCycleTransition(
					(LifeCycleState) LifeCycleState.STATES.get(currentStateIndex),
					(LifeCycleState) LifeCycleState.STATES.get(nextStateIndex));

			waitingTransition = transition;
			waitingTargetState = newState;
			
			if (!checkTransitionVotes(transition)) {
				break;
			}
			
			if (!performTransition(transition)) {
				break;
			}
			
			waitingTransition = null;
			waitingTargetState = null;

			currentStateIndex = nextStateIndex;
		}
	}

	private boolean performTransition(LifeCycleTransition transition) {

		if (!currentState.equals(transition.getPreviousState())) {
			return false;
		}
		

		if (transition.equals(LifeCycleTransition.PENDING_READY)) {
			if (!checkVotes()) {
				return false;
			}
		}

		//logger.log(LOG_LEVEL, "Performing Transition : " + transition + " : " + meemCore.getMeemPath());

		currentState = transition.getCurrentState();

		if (transition.equals(LifeCycleTransition.PENDING_LOADED)) {
			// clear application voting list
			applicationWedgeVotes.clear();
		}

//		if (transition.equals(LifeCycleTransition.DORMANT_ABSENT)) {
//			destroySelf();
//		}

		lifeCycleClient.lifeCycleStateChanging(transition);
		lifeCycleClientConduit.lifeCycleStateChanging(transition);

		//currentState = transition.getCurrentState();			

		if (!currentState.equals(transition.getCurrentState())) {
			return false;
		}

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
			logger.log(LOG_LEVEL, "LifeCycle state change: " + transition.toString() + " Meem: " + meemCore.getMeemPath());
		}

		lastTransition = transition;

		lifeCycleClient.lifeCycleStateChanged(transition);
		lifeCycleClientConduit.lifeCycleStateChanged(transition);
		
		return true;
	}

	private void destroySelf() {
		Meem lifeCycleManagerMeem = meemCore.getLifeCycleManager();

	 	meemClientConduit.provideReference(lifeCycleManagerMeem, "lifeCycleManager", LifeCycleManager.class, new ReferenceCallbackImpl());
	}

	private boolean checkVotes() {
		// check system votes
		for (Entry<String, Boolean> entry : systemWedgeVotes.entrySet()) {
			Boolean vote = entry.getValue();
			if (vote.booleanValue() == false) {
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
					logger.log(LOG_LEVEL, "checkVotes() returning false : system " + entry.getKey() + " : " + meemCore.getMeemPath());
				}
				return false;
			}
		}

		// check application votes

		for (Entry<String, Boolean> entry : applicationWedgeVotes.entrySet()) {
			Boolean vote = (Boolean) entry.getValue();
			if (vote.booleanValue() == false) {
				if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
					logger.log(LOG_LEVEL, "checkVotes() returning false : application " + entry.getKey() + " : " + meemCore.getMeemPath());
				}
				return false;
			}
		}

		if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
			logger.log(LOG_LEVEL, "checkVotes() returning true " + meemCore.getMeemPath());
		}
		return true;
	}

	private final class ReferenceCallbackImpl
		implements MeemClientCallback
	{
		public void referenceProvided(Reference reference)
		{
			if (reference == null)
			{
				logger.info("no lifeCycleManager reference found can't destroy self!");
				return;
			}
        	
			LifeCycleManager lifeCycleManager = (LifeCycleManager) reference.getTarget();

			lifeCycleManager.destroyMeem(meemCore.getSelf());
		}
	}
	
	private boolean checkTransitionVotes(LifeCycleTransition transition) {
		List<String> votes = transitionVotes.get(transition);

		if (votes == null || votes.size() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/* --------------- LifeCycleControlConduit class ---------- */

	private final class LifeCycleControlConduit implements Vote {

		/**
		 */
		public void vote(String voterIdentification, boolean goodToGo) {
			if (currentState.equals(LifeCycleState.LOADED)) {
				// should only get votes from system wedges at this time
				systemWedgeVotes.put(voterIdentification, Boolean.valueOf(goodToGo));
			} else {
				Boolean vote = (Boolean) systemWedgeVotes.get(voterIdentification);
				if (vote != null) {
					systemWedgeVotes.put(voterIdentification, Boolean.valueOf(goodToGo));
				} else {
					// its from an application wedge
					applicationWedgeVotes.put(voterIdentification, Boolean.valueOf(goodToGo));
				}

				if (checkVotes()) {
					// go to ready
					if (currentState.equals(LifeCycleState.PENDING) && lastTransition.getCurrentState().equals(LifeCycleState.PENDING)) {
						if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
							logger.log(LOG_LEVEL, "Going PENDING -> READY : " + meemCore.getMeemPath());
						}
						performTransitions(LifeCycleState.READY);
					}
				} else {
					// go to pending
					if (currentState.equals(LifeCycleState.READY)) {
						if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLE) {
							logger.log(LOG_LEVEL, "Going READY -> PENDING : " + meemCore.getMeemPath());
						}
						performTransitions(LifeCycleState.PENDING);
					}
				}
			}
		}
		
		public void vote(String voterIdentification, LifeCycleTransition transition, boolean goodToGo) {
			Vector<String> votes = transitionVotes.get(transition);
			
			if (goodToGo) {
				// voting true
				
				if (votes != null) {
					votes.remove(voterIdentification);
				}
				
				if (transition.equals(waitingTransition)) {
					performTransitions(waitingTargetState);
				}
				
			} else {
				// voting false
				
				if (votes == null) {
					votes = new Vector<String>();
					transitionVotes.put(transition, votes);
				}
				
				votes.add(voterIdentification);
			}
		}
	}
}
