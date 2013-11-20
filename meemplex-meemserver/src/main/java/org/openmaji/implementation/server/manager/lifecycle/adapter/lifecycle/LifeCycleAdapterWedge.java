/*
 * @(#)LifeCycleAdapterWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.adapter.lifecycle;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapter;
import org.openmaji.system.manager.lifecycle.LifeCycleAdapterClient;
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
public class LifeCycleAdapterWedge implements Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final boolean DEBUG = false;
	
	/** meem context */
	public MeemContext meemContext;


	/* ------------------------- outbound facets ------------------------------ */
	
	public LifeCycle lifeCycleAdapter;
	
	public LifeCycleLimit lifeCycleLimitedAdapter;


	/* --------------------------- conduits ----------------------------------- */
	
	public MeemClientConduit meemClientConduit;
	
	public LifeCycleAdapter lifeCycleAdapterConduit = new LifeCycleAdapterConduit(); // inbound
	
	public LifeCycleAdapterClient lifeCycleAdapterClientConduit;


	/* ----------- LifeCycleAdapter Conduit ---------------- */

	private final class LifeCycleAdapterConduit implements LifeCycleAdapter {
		public void changeLifeCycleState(Meem meem, final LifeCycleState lifeCycleState) {
			if (DEBUG) {
				logger.log(Level.INFO, "changing meem state: " + meem.getMeemPath() + lifeCycleState);
			}
			new ChangeTask(meem, lifeCycleState);
		}

		public void limitLifeCycleState(Meem meem, final LifeCycleState lifeCycleState) {
			new LimitTask(meem, lifeCycleState);
		}
	}

	public final class ChangeTask implements LifeCycleClient, MeemClientCallback {
		private final Meem meem;
		private final LifeCycleState lifeCycleState;
		private final Reference reference;

		public ChangeTask(Meem meem, LifeCycleState lifeCycleState) {
			this.meem = meem;
			this.lifeCycleState = lifeCycleState;

			LifeCycleClient lifeCycleClient = (LifeCycleClient) meemContext.getLimitedTargetFor(this, LifeCycleClient.class);
			this.reference = Reference.spi.create("lifeCycleClient", lifeCycleClient, true);

			meem.addOutboundReference(reference, false);

			if (DEBUG) {
				logger.log(Level.INFO, "getting reference to lifeCycle: " + meem.getMeemPath());
			}
			meemClientConduit.provideReference(meem, "lifeCycle", LifeCycle.class, this);
		}

		/**
		 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanged(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
		 */
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if (transition.getCurrentState().equals(lifeCycleState)) {
				meem.removeOutboundReference(reference);

				if (DEBUG) {
					logger.log(Level.INFO, "lifeCycleStateChanged: " + meem.getMeemPath() + " - " + transition);
				}
				
				// notify clients
				lifeCycleAdapterClientConduit.lifeCycleStateChanged(meem.getMeemPath(), transition);
			}
		}

		/**
		 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClient#lifeCycleStateChanging(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
		 */
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}

		/**
		 * 
		 */
		public void referenceProvided(Reference reference) {
			if (reference == null) {
					logger.log(Level.INFO, "change - no lifeCycle facet can't change.");
				return;
			}
			
			if (DEBUG) {
				logger.log(Level.INFO, "got reference to lifeCycle: " + meem.getMeemPath());
			}

			LifeCycle lifeCycle = (LifeCycle) reference.getTarget();
			lifeCycle.changeLifeCycleState(lifeCycleState);
		}
	}

	public final class LimitTask implements LifeCycleLimit, MeemClientCallback {
		private final Meem meem;
		private final LifeCycleState lifeCycleState;
		private final Reference reference;

		public LimitTask(Meem meem, LifeCycleState lifeCycleState) {
			this.meem = meem;
			this.lifeCycleState = lifeCycleState;

			LifeCycleLimit lifeCycleLimit = (LifeCycleLimit) meemContext.getLimitedTargetFor(this, LifeCycleLimit.class);
			this.reference = Reference.spi.create("lifeCycleLimitClient", lifeCycleLimit, true);

			meem.addOutboundReference(reference, false);

			meemClientConduit.provideReference(meem, "lifeCycleLimit", LifeCycleLimit.class, this);
		}

		/**
		 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleLimit#limitLifeCycleState(org.openmaji.meem.wedge.lifecycle.LifeCycleState)
		 */
		public void limitLifeCycleState(LifeCycleState state) {
			if (state.equals(lifeCycleState)) {
				meem.removeOutboundReference(reference);

				// notify clients
				lifeCycleAdapterClientConduit.lifeCycleLimitChanged(meem.getMeemPath(), state);
			}
		}

		/**
		 * 
		 */
		public void referenceProvided(Reference reference) {
			if (reference == null) {
					logger.log(Level.INFO, "limit - no lifeCycleLimit facet can't limit.");
				return;
			}

			LifeCycleLimit lifeCycleLimit = (LifeCycleLimit) reference.getTarget();
			lifeCycleLimit.limitLifeCycleState(lifeCycleState);
		}
	}
}
