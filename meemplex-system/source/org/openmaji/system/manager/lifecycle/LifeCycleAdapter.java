/*
 * @(#)LifeCycleAdapter.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <p>
 * Conduit used by LifeCycleManagers to control the LifeCycleState of Meems.
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleAdapter {
	
	/**
	 * Conduit method to set the maximum LifeCycleSate of the passed in Meem
	 * @param meem Meem whose LifeCycleState limit will be set
	 * @param lifeCycleState LifeCycleState limit for the Meem
	 */
	public void limitLifeCycleState(Meem meem, LifeCycleState lifeCycleState);
	
	/**
	 * Conduit method to attempt to change the LifeCycleState of the passed in Meem
	 * @param meem Meem whose LifeCycleState will be changed
	 * @param lifeCycleState The target LifeCycleState for the Meem
	 */
	public void changeLifeCycleState(Meem meem, LifeCycleState lifeCycleState);
}
