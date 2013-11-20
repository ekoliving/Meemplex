/*
 * @(#)LifeCycleLimit.java
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

import org.openmaji.meem.Facet;

/**
 * <p>
 * Inbound system Facet used to set the maximum possible LifeCycleState of a Meem.
 * </p>
 * @see LifeCycleState
 */
public interface LifeCycleLimit extends Facet {
	
	/**
	 * Sets the maximum LifeCycleState that this Meem can get to. If the current
	 * state is higher than the desired maximum, change the current state to be the 
	 * maximum state (passing through all transitions). 
	 * @param state Maximum LifeCycleState.
	 */
	public void limitLifeCycleState(LifeCycleState state);
}
