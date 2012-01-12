/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.common;

import java.io.Serializable;
/**
 * The State interface is used by the Multistate Facet
 * to represent a state value of the Facet
 * 
 * @author Diana Huang
 */
public interface State extends Serializable{
	/**
	 * Set a new state value
	 * 
	 * @param state a new String value
	 */
	public void setState(String state);
	/**
	 * Return a state value
	 * 
	 * @return a state value
	 */
	public String getState();
	/**
	 * Return a String array of available State values
	 * 
	 * @return available State values
	 */
	public String[] getAvailableStates();
}
