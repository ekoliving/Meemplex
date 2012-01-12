/*
 * @(#)LifeCycleManager.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <p>
 * Basic facet for meem creation, destruction, and transfer with life cycle managers.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.definition.MeemDefinition
 */
public interface LifeCycleManager extends Facet {

  /**
   * Create a new Meem.
   * 
   * @param meemDefinition The MeemDefinition of the Meem to be created
   * @param initialLifeCycleState The initial LifeCycleState for the Meem
   * @exception IllegalArgumentException
   */
  public void createMeem(
    MeemDefinition meemDefinition,
    LifeCycleState initialLifeCycleState)
    throws IllegalArgumentException;
  
  /**
   * Permanently removes a Meem.
   * @param meem Meem to be destroyed
   */  
	public void destroyMeem(
    Meem meem);
	
	/**
	 * Transfers the ownership of a meem between two LifeCycleManagers.
	 * 
	 * @param meem Meem to be transferred
	 * @param targetLifeCycleManager Meems new LifeCycleManager
	 */
	public void transferMeem(
    Meem             meem,
    LifeCycleManager targetLifeCycleManager);
  
	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static String getIdentifier() {
      return("lifeCycleManager");
    };
  }
}