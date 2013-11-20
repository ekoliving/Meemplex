/*
 * @(#)LifeCycleAdapterClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * Client Conduit used by LifeCycleManagers to listen to changes in LifeCycleState of meems
 *
 * @author  mg
 * @version 1.0
 */
public interface LifeCycleAdapterClient {
	
	/**
	 * Notification method to tell clients that the LifeCycleState of the Meem at
	 * the given MeemPath has changed. 
	 * @param meemPath MeemPath of the Meem whose LifeCycleState has changed
	 * @param transition The last LifeCycleStateTransition that occured
	 */
	public void lifeCycleStateChanged(MeemPath meemPath, LifeCycleTransition transition);
	
	/**
	 * Notification method to tell clients that the maximum LifeCycleState of the Meem at
	 * the given MeemPath has changed. 
	 * @param meemPath MeemPath of the Meem whose maximum LifeCycleState has changed
	 * @param state The new maximum LifeCycleState
	 */
	public void lifeCycleLimitChanged(MeemPath meemPath, LifeCycleState state);
	
}
