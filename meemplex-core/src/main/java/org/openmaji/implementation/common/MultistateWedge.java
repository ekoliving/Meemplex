/* Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.common;

import org.openmaji.common.Multistate;
import org.openmaji.common.State;
import org.openmaji.common.TrafficLightState;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MultistateWedge is used to maintain the state of Multistate type information.
 * It is intended to work in conjunction with another Wedge that provides a
 * specific implementation of some Multistate things.
 * 
 * @author Diana Huang
 * 
 */
public class MultistateWedge implements Multistate, Wedge {
	private static Logger logger = Logger.getAnonymousLogger();
	/*
	 * Multistate outbound Facet
	 */
	public Multistate multistateClient;
	public final ContentProvider<Multistate> multistateClientProvider = new ContentProvider<Multistate>() {
		/**
		 * Send content to a Multistate client that has just had its Reference
		 * added.
		 * 
		 * @param target
		 *            Reference to the target Meem
		 * @param filter
		 *            No Filters are currently implemented
		 */
		public synchronized void sendContent(Multistate target, Filter filter) {
			if (DebugFlag.TRACE)
				logger.log(Level.FINE, "sendContent() - invoked");
			target.stateChanged(state);
		}
	};
	public State state = TrafficLightState.RED;
	/**
	 * The conduit through which other Wedges in the Meem are notified of a
	 * request to change the state.
	 */
	public Multistate multistateControlConduit = null;
	/**
	 * The conduit through which state changes are received other Wedges in the
	 * Meem.
	 */
	public Multistate multistateStateConduit = new MultistateStateConduit();

	/*
	 * ---------- Multistate Facet method(s)
	 * ---------------------------------------
	 */
	public synchronized void stateChanged(State state) {
		if (DebugFlag.TRACE)
			logger.log(Level.FINE, "stateChanged() - invoked on inbound facet");
		multistateControlConduit.stateChanged(state);
	}

	/*
	 * ---------- MultistateStateConduit
	 * -------------------------------------------
	 */

	/**
	 * This class handles incoming Multistate messages from other Wedges in the
	 * Meem.
	 */
	class MultistateStateConduit implements Multistate {
		public synchronized void stateChanged(State newState) {
			if (DebugFlag.TRACE)
				logger.log(Level.FINE, "stateChanged() - invoked on MultistateStateConduit");
			state = newState;
			// logger.log(Level.INFO, "Current state is "+state.getState());
			multistateClient.stateChanged(state);
		}
	}
}
