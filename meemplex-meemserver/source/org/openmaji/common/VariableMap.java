/*
 * @(#)VariableMap.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.common;

import java.io.Serializable;

import org.openmaji.meem.Facet;

/**
 * This Facet provides a Map capability by being able to store arbitary objects
 * that are indexed by a key of arbitrary type. Wedges that implement this Facet
 * indicate that they can store pairs of objects. Wedges that require notification
 * of additions to a VariableMap should implement the VariableMapClient Facet.
 * 
 * @author  mg
 * @version 1.0
 * @see org.openmaji.common.VariableMapClient
 */
public interface VariableMap extends Facet {
	
	/**
	 * Updates entry in the VariableMap if it already exists, 
	 * or adds a new one if it doesn't.
	 * @param key
	 * @param value
	 */
	public void update(Serializable key, Serializable value);
	
	/**
	 * Removes the relevant key=value pair from the VariableMap. 
	 * No effect if the map doesn't have the specified key.
	 * @param key
	 */
	public void remove(Serializable key);
	
	/**
	 * Merges the value to the value in identified by the key.
	 * @param key The key that identifies the original value.
	 * @param delta The change to merge.
	 */
	public void merge(Serializable key, Serializable delta);
}
