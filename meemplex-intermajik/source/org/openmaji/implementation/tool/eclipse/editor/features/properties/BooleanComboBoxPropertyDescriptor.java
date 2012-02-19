/*
 * @(#)BooleanComboBoxPropertyDescriptor.java
 * Created on 17/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.properties;

/**
 * <code>BooleanComboBoxPropertyDescriptor</code>.
 * <p>
 * @author Kin Wong
 */
public class BooleanComboBoxPropertyDescriptor extends ObjectComboBoxPropertyDescriptor {
	static private Object[] values = {new Boolean(true), new Boolean(false)};
	/**
	 * Constructs an instance of <code>BooleanComboBoxPropertyDescriptor</code>.
	 * <p>
	 * @param id
	 * @param displayName
	 */
	public BooleanComboBoxPropertyDescriptor(Object id, String displayName) {
		super(id, displayName, values);
	}
}
