/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.common;
import org.openmaji.meem.Facet;
/**
 *<p>
 * This Facet is implemented by wedges that maintain multiple states.
 * 
 * </p> 
 * 
 * @author Diana Huang
 *
 */
public interface Multistate extends Facet{
	/**
	 * Change the state of this MultiState
	 * 
	 * @param state the new state of this MultiState
	 */
	public void stateChanged(State state);
}
