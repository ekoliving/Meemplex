/*
 * @(#)VariableMapClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.common;

import java.io.Serializable;
import java.util.Map;

import org.openmaji.meem.Facet;

/**
 * This Facet is implemented by Wedges that require notification of changes
 * that occur to a VariableMap.
 * 
 * @author  mg
 * @version 1.0
 * @see org.openmaji.common.VariableMap
 */
public interface VariableMapClient extends Facet {
	public void changed(Map.Entry<Serializable, Serializable>[] entries);
	
	/**
	 * This will only be called if an object is removed. If nothing 
	 * is removed from the VariableMap, then no notification will occur
	 * @param key
	 */
	public void removed(Serializable key);
	
}
