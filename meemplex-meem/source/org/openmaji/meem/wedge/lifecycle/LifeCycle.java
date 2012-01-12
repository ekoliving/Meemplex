/*
 * @(#)LifeCycle.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.meem.wedge.lifecycle;

import org.openmaji.meem.Facet;

/**
 * <p>
 * Inbound system Facet that is used to change the LifeCycleState of a Meem.
 * </p>
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleState 
 */

public interface LifeCycle extends Facet {
	
	/**
	 * Attempt to change this Meems current LifeCycleState to the desired state 
	 * set by the state parameter. All states between the current state and the
	 * desired state should be passed through.
	 * @param state Desired LifeCycleState
	 */

	public void changeLifeCycleState(
    LifeCycleState state);
  
/**
 * Nested class for service provider.
 * 
 * @see org.openmaji.spi.MajiSPI
 */
  public class spi {
    public static String getIdentifier() {
      return("lifeCycle");
    };
  }
}