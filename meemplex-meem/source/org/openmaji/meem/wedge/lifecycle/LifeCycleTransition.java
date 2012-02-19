/*
 * @(#)LifeCycleTransition.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.meem.wedge.lifecycle;

import java.io.Serializable;

/**
 * <p>
 * The LifeCycleTransition contains the current and previous states 
 * which are used to define the last state transition.
 * </p>
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleState
 */
public final class LifeCycleTransition implements Serializable {

	private static final long serialVersionUID = -1178365040590887015L;

	/**
	 * ABSENT_DORMANT transition: Absent state -> Dormant state
	 */

	public static final LifeCycleTransition ABSENT_DORMANT = 
		new LifeCycleTransition(LifeCycleState.ABSENT, LifeCycleState.DORMANT);

	/**
	 * DORMANT_LOADED transition: Dormant state -> Loaded state
	 */

	public static final LifeCycleTransition DORMANT_LOADED =
		new LifeCycleTransition(LifeCycleState.DORMANT, LifeCycleState.LOADED);

	/**
	 * LOADED_PENDING transition: Loaded state -> Pending state
	 */

	public static final LifeCycleTransition LOADED_PENDING = 
		new LifeCycleTransition(LifeCycleState.LOADED, LifeCycleState.PENDING);

	/**
	 * PENDING_READY transition: Pending state -> Ready state
	 */

	public static final LifeCycleTransition PENDING_READY = 
		new LifeCycleTransition(LifeCycleState.PENDING, LifeCycleState.READY);

	/**
	 * READY_PENDING transition: Ready state -> Pending state
	 */

	public static final LifeCycleTransition READY_PENDING = 
		new LifeCycleTransition(LifeCycleState.READY, LifeCycleState.PENDING);
		
	/**
	 * PENDING_LOADED transition: Pending state -> Loaded state
	 */

	public static final LifeCycleTransition PENDING_LOADED = 
		new LifeCycleTransition(LifeCycleState.PENDING, LifeCycleState.LOADED);

	/**
	 * READY_LOADED transition: Ready state -> Loaded state
	 */

	public static final LifeCycleTransition READY_LOADED =
		new LifeCycleTransition(LifeCycleState.READY, LifeCycleState.LOADED);

	/**
	 * LOADED_DORMANT transition: Loaded state -> Dormant state
	 */

	public static final LifeCycleTransition LOADED_DORMANT =
		new LifeCycleTransition(LifeCycleState.LOADED, LifeCycleState.DORMANT);

	/**
	 * DORMANT_ABSENT transition: Dormant state -> Absent state
	 */

	public static final LifeCycleTransition DORMANT_ABSENT =
		new LifeCycleTransition(LifeCycleState.DORMANT, LifeCycleState.ABSENT);

	/**
		* Uniquely distinguishes one "current state" from another
		*/

	private LifeCycleState currentState = null;

	/**
		* Uniquely distinguishes one "previous state" from another
		*/

	private LifeCycleState previousState = null;

	public LifeCycleTransition(
		LifeCycleState previousLifeCycleState, 
		LifeCycleState currentLifeCycleState)
		throws IllegalArgumentException {

		if (currentLifeCycleState == null) {
			throw new IllegalArgumentException("Current state must not be null");
		}

		if (previousLifeCycleState == null) {
			throw new IllegalArgumentException("Previous state must not be null");
		}

		this.currentState = currentLifeCycleState;

		this.previousState = previousLifeCycleState;
	}

	/**
	 * Provides the current state for this LifeCycleTransition.
	 *
	 * @return String The current state for this LifeCycleTransition
	 */

	public LifeCycleState getCurrentState() {
		return (currentState);
	}

	/**
	 * Provides the previous state for this LifeCycleTransition.
	 *
	 * @return String The previous state for this LifeCycleTransition
	 */

	public LifeCycleState getPreviousState() {
		return (previousState);
	}

	/**
	 * Compares LifeCycleState to the specified object for the same transition.
	 * The result is true, if and only if the current state and the previous
	 * state are both not null and the LifeCycleStates are identical.
	 *
	 * @return true if LifeCycleTransition are the same transition
	 */

	public boolean equals(Object object) {

		if (object == this)
			return (true);

		if ((object instanceof LifeCycleTransition) == false)
			return (false);

		LifeCycleTransition thatLifeCycleTransition = (LifeCycleTransition) object;

		if (currentState.equals(thatLifeCycleTransition.getCurrentState()) == false) {
			return (false);
		}

		if (previousState == null)
			return (false);

		return (previousState.equals(thatLifeCycleTransition.getPreviousState()));
	}
	
	/**
   * Provides the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return LifeCycleState hashCode
   */

  public int hashCode() {
    int hashCode = currentState.hashCode();

    if (previousState != null) hashCode ^= previousState.hashCode();

    return(hashCode);
  }

  /**
   * Provides a String representation of LifeCycleState.
   *
   * @return String representation of LifeCycleState
   */

  public String toString() {
    return(
      getClass().getName() + "[" +
      "currentState="    + currentState  +
      ", previousState=" + previousState +
      "]"
    );
  }
}
