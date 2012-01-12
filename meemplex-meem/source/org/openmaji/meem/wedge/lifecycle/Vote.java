/*
 * @(#)Vote.java
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

/**
 * <p>
 * Conduit interface used by Wedges to prevent a Meem from going to the
 * READY LifeCycleState. Wedges may wish to vote against going ready 
 * while they are aquiring resources.
 * </p>
 * <p>
 * If a Wedge doesn't vote, it is assumed that it has voted true.
 * </p>
 * <p>
 * Declaring conduit:
 * </p>
 * <pre>
 * public Vote lifeCycleControlConduit;
 * </pre>
 * <p>
 * Example usage:
 * </p>
 * <pre>
 * lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), false);
 * </pre>
 */
public interface Vote {

	/**
	 * 
	 * @param voterIdentification Unique identifier for wedge
	 * @param goodToGo Whether the wedge has aquired all its resources and
	 * is ok to go to the READY LifeCycleState
	 */
	public void vote(String voterIdentification, boolean goodToGo);
	
	public void vote(String voterIdentification, LifeCycleTransition transition, boolean goodToGo);
}
