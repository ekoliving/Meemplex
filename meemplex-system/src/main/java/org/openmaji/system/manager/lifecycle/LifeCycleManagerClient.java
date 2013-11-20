/*
 * @(#)LifeCycleManagerClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;

/**
 * <p>
 * Interface for the outbound facet a life cycle manager uses to communicate meem creation
 * and destruction events to monitoring objects.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.system.manager.lifecycle.LifeCycleManager
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleState
 */

public interface LifeCycleManagerClient extends Facet {

	/**
	 * Notification message that a Meem has been created
	 * @param meem New Meem
	 * @param identifier Identifier of the MeemDefinition used to create the meem
	 */
  public void meemCreated(
    Meem meem,
    String identifier);

	/**
	 * Notification message that a Meem has been permanately destroyed
	 * @param meem Unbound meem that has been destroyed
	 */
  public void meemDestroyed(
    Meem meem);
    
  /**
   * Notification message sent by the previous LifeCycleManager of the 
   * transferred Meem.
   * @param meem Meem that has been transferred between LifeCycleManagers
   * @param targetLifeCycleManager New owning LifeCycleManager of Meem
   */  
  public void meemTransferred(
    Meem meem, LifeCycleManager targetLifeCycleManager);
  
}
