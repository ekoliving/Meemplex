/*
 * @(#)LifeCycleClient.java
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
 * Used to notify listeners of
 * changes in the LifeCycleState of a Meem. It is an outbound system
 * Facet and also available as a conduit for intra-meem notification.
 * </p>
 * <p>
 * To listen to LifeCycleState changes within a Meem, create a conduit 
 * target:
 * </p>
 * <pre>
 * public LifeCycleClient lifeCycleClientConduit = new LifeCycleClient() {
 *   public void lifeCycleStateChanging(LifeCycleTransition transition) {
 *     // Handle LifeCycle changing events
 *   }
 * 
 *   public void lifeCycleStateChanged(LifeCycleTransition transition) {
 *     // Handle LifeCycle changed events
 *   }
 * } 
 * </pre>
 * <p>
 * Alternatively, you can use a {@link LifeCycleClientAdapter} to listen to state
 * changes.
 * </p>
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleTransition 
 * @see org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter
 */
public interface LifeCycleClient extends Facet {
	
	/**
	 * Nofities clients of a transition occuring in the LifeCycleState of a Meem.
	 * @param transition Transition occuring.
	 */
	public void lifeCycleStateChanging(LifeCycleTransition transition);
	
	/**
	 * Nofities clients of changes in the LifeCycleState of a Meem.
	 * @param transition Last transition performed.
	 */
	public void lifeCycleStateChanged(LifeCycleTransition transition);
	
}
