/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.manager.lifecycle.subsystem;

import java.io.Serializable;

/**
 * Instances of this class are used to represent the commissioned state of a
 * Subsystem. A Subsystem is said to be commissioned if it is ready to provide
 * its services to clients. In most cases a Subsystem will need to configured
 * before being commissioned.
 *
 * @author Chris Kakris
 */
public final class CommissionState implements Serializable
{
	private static final long serialVersionUID = -4346442626216118038L;

  /**
   * Represents the state of a Subsystem that has been commissioned.
   */
  public static final CommissionState COMMISSIONED = new CommissionState(0);

  /**
   * Represents the state of a Subsystem that is not commissioned.
   */
  public static final CommissionState NOT_COMMISSIONED = new CommissionState(1);

  private final int state;

  /**
   * Create an instance of this class with the specified state. This constructor
   * is declared private. It is expected that users will only use the statically
   * declared instances COMMISSIONED and NOT_COMMISSIONED.
   * 
   * @param state The commissioned state
   */
  private CommissionState(int state)
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
    CommissionState that = (CommissionState) object;
    if ( that.state != this.state ) return false;
    return true;
  }

}