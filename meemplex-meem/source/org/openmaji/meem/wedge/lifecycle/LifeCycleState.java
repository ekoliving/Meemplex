/*
 * @(#)LifeCycleState.java
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
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * The LifeCycleState represents the "overall state of play" for a Meem.
 * </p>
 * <p>
* Meems have a well-defined LifeCycle that consists of five states ...
 * <ul>
 *   <li>ABSENT: Meem does not exist</li>
 *   <li>DORMANT: Definition exists in MeemStore.  Meem isn't instantiated</li>
 *   <li>LOADED: Meem instantiated.  Only system defined Facets usable</li>
 *   <li>PENDING: Meem instantiated, waiting on resources.  Only system defined Facets usable</li>
 *   <li>READY:  Meem instantiated.  All Facets usable</li>
 * </ul>
 * There are nine valid transitions between those states ...
 * <table>
 * <tr>
 *   <td>ABSENT_DORMANT:</td><td>&nbsp;</td><td>ABSENT</td><td>-></td><td>DORMANT</td>
 * </tr><tr>
 *   <td>DORMANT_LOADED:</td><td>&nbsp;</td><td>DORMANT</td><td>-></td><td>LOADED</td>
 * </tr><tr>
 *   <td>LOADED_PENDING:</td><td>&nbsp;</td><td>LOADED</td><td>-></td><td>PENDING</td>
 * </tr><tr>
 *   <td>PENDING_READY:</td><td>&nbsp;</td><td>PENDING</td><td>-></td><td>READY</td>
 * </tr><tr>
 *   <td>READY_PENDING:</td><td>&nbsp;</td><td>READY</td><td>-></td><td>PENDING</td>
 * </tr><tr>
 *   <td>PENDING_LOADED:</td><td>&nbsp;</td><td>PENDING</td><td>-></td><td>LOADED</td>
 * </tr><tr>
 *   <td>READY_LOADED:</td><td>&nbsp;</td><td>READY</td><td>-></td><td>LOADED</td>
 * </tr><tr>
 *   <td>LOADED_DORMANT:</td><td>&nbsp;</td><td>LOADED</td><td>-></td><td>DORMANT</td>
 * </tr><tr>
 *   <td>DORMANT_ABSENT:</td><td>&nbsp;</td><td>DORMANT</td><td>-></td><td>ABSENT</td>
 * </tr>
 * </table>
 * </p>

 * The LifeCycleState contains the current state of the Meem.
 * <p>
 * Note: Implementation thread safe = Yes (2003-04-24)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 */

public final class LifeCycleState implements Serializable {

	private static final long serialVersionUID = 3718621151394124697L;

  /**
   * Absent state: Meem does not exist
   */

  public static final LifeCycleState ABSENT = new LifeCycleState("absent");

  /**
   * Dormant state: Definition exists in MeemStore.  Meem isn't instantiated
   */

  public static final LifeCycleState DORMANT = new LifeCycleState("dormant");

  /**
   * Loaded state: Meem instantiated.  Only system defined Facets usable
   */

  public static final LifeCycleState LOADED = new LifeCycleState("loaded");

	/**
	 * Pending state: Meem trying to become ready but waiting for resources to become available.  
	 * Only system defined Facets usable
	 */

	public static final LifeCycleState PENDING = new LifeCycleState("pending");

  /**
   * Ready state: Meem instantiated.  All Facets are usable
   */

  public static final LifeCycleState READY = new LifeCycleState("ready");

	/**
	 * List of available states
	 */

	public static final List STATES = Arrays.asList(new LifeCycleState[] {
		LifeCycleState.ABSENT, 
		LifeCycleState.DORMANT, 
		LifeCycleState.LOADED,
		LifeCycleState.PENDING, 
		LifeCycleState.READY
	});

  /**
   * Uniquely distinguishes one "current state" from another
   */

  private String currentState = null;

  /**
   * Create LifeCycleState.
   *
   * @param currentState Unique current state distinguisher
   * @exception IllegalArgumentException Current state must not be null
   */

  public LifeCycleState(
    String currentState)
    throws IllegalArgumentException {

    if (currentState == null) {
      throw new IllegalArgumentException("Current state must not be null");
    }

    this.currentState = currentState;
  }

  /**
   * Provides the current state for this LifeCycleState.
   *
   * @return String The current for this LifeCycleState
   */

  public String getCurrentState() {
    return(currentState);
  }


  /**
   * Compares LifeCycleState to the specified object.
   * The result is true, if and only if both the current state and
   * the previous state are identical.
   *
   * @return true if LifeCycleStates are equal
   */

  public boolean equals(
    Object object) {

		if (object == this) return(true);

		if ((object instanceof LifeCycleState) == false) return(false);

		LifeCycleState thatLifeCycleState = (LifeCycleState) object;

		return(currentState.equals(thatLifeCycleState.getCurrentState()));
  }

  /**
   * Provides the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return LifeCycleState hashCode
   */

  public int hashCode() {
    int hashCode = currentState.hashCode();

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
      "]"
    );
  }
}
