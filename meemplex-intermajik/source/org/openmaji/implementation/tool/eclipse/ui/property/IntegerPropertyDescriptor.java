/*
 * @(#)IntegerPropertyDescriptor.java
 * Created on 10/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * <code>IntegerPropertyDescriptor</code>.
 * <p>
 * @author Kin Wong
 */
public class IntegerPropertyDescriptor extends PropertyDescriptor {
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id the id of the property
	 * @param displayName the name to display for the property
	 */
	public IntegerPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}
	/**
	 * The <code>TextPropertyDescriptor</code> implementation of this 
	 * <code>IPropertyDescriptor</code> method creates and returns a new
	 * <code>TextCellEditor</code>.
	 * <p>
	 * The editor is configured with the current validator if there is one.
	 * </p>
	 */
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new IntegerCellEditor(parent);
		if (getValidator() != null)
			editor.setValidator(getValidator());
		return editor;
	}
}
