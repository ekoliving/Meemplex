/*
 * @(#)IVariableSource.java
 * Created on 5/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client.variables;

import org.openmaji.implementation.intermajik.model.ValueBag;


/**
 * <code>IVariableSource</code>.
 * <p>
 * @author Kin Wong
 */
public interface IVariableSource {
	/**
	 * Extracts all known values from this variable source and place it in the 
	 * returned <code>ValueBag</code>.
	 * @return ValueBag
	 */
	ValueBag extractAll();

	/**
	 * Extracts the value identified by the key from this variable source and 
	 * place it in the returned <code>ValueBag</code>.
	 * @param key The key that identifies the value.
	 * @param value The value bag to hold the new value.
	 * @return true is the key is identified and value has been extracted, false
	 * otherwise.
	 */
	boolean extract(Object key, ValueBag value);
	
	/**
	 * Merges all the values in the <code>ValueBag</code> into the variable 
	 * source.
	 * @param bag 
	 * @return boolean
	 */
	boolean merge(ValueBag bag);
	
}
