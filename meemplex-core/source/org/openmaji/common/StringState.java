/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.common;


/**
 * The is a concrete class of AbstractState (example)
 * 
 * @author Diana Huang
 * 
 * @see org.openmaji.common.AbstractState
 */
public class StringState extends AbstractState {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * A constructor
	 * 
	 * @param state
	 */	
	public StringState(String state, String[] available){
		this.availableStates = available;
		setState(state);
	}

	public void setState(String state){
		this.state=state;
	}
}
