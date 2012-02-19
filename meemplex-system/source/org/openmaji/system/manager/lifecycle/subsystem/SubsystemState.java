/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.manager.lifecycle.subsystem;

import java.io.Serializable;

/**
 * Instances of this class are used to represent the state of a Subsystem.
 * A Subsystem is said to be STARTED if all of its Meems are in the READY
 * state. A Subsystem is said to be STOPPED of all of its Meems are in the
 * LOADED state. A Subsystem transitioning from a STOPPED to a STARTED state
 * is said to be in a STARTING state and one transitioning from a STARTED to a
 * STOPPED state is said to be in a STOPPING state.
 *
 * @author Chris Kakris
 */
public final class SubsystemState implements Serializable
{
	private static final long serialVersionUID = -8067392148376299787L;

  /**
   * Represents the state of a subsystem that is currently transitioning from STOPPED to STARTED.
   */
  public static final SubsystemState STARTING = new SubsystemState(0);

  /**
   * Represents the state of a subsystem whose constituent Meems are all in a READY state.
   */
  public static final SubsystemState STARTED = new SubsystemState(1);

  /**
   * Represents the state of a subsystem that is currently transitioning from STARTED to STOPPED.
   */
  public static final SubsystemState STOPPING = new SubsystemState(2);
  
  /**
   * Represents the state of a subsyetem whose constituent Meems are all in STOPPED state.
   */
  public static final SubsystemState STOPPED = new SubsystemState(3);

  private static final String[] stateStrings = { "Starting", "Started", "Stopping", "Stopped" };

  private final int state;

  /**
   * Create an instance of this class with the specified state. This constructor
   * is declared private. It is expected that users will only use the statically
   * declared fields of this class.
   * 
   * @param state The commissioned state
   */
  private SubsystemState(int state)
  {
    this.state = state;
  }

  /**
   * Return the state associated with this instance.
   * 
   * @return  The state
   */
  public int getState()
  {
    return state;
  }

  public int hashCode()
  {
    return state;
  }

  public boolean equals(Object object)
  {
    if ( object == null ) return false;
    if ( object.getClass().equals(this.getClass()) == false ) return false;
    SubsystemState that = (SubsystemState) object;
    if ( that.state != this.state ) return false;
    return true;
  }

  public String toString()
  {
    return stateStrings[state];
  }
}