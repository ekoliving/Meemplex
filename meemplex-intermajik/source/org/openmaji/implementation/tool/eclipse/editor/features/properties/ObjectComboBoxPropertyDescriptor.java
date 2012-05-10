/*
 * @(#)ObjectComboBoxPropertyDescriptor.java
 * Created on 16/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * <code>ObjectComboBoxPropertyDescriptor</code>.
 * <p>
 * @author Kin Wong
 */
public class ObjectComboBoxPropertyDescriptor extends PropertyDescriptor {

	/**
	 * The list of possible values to display in the combo box
	 */
	private Object[] values;
/**
 * Creates an property descriptor with the given id, display name, and list
 * of value labels to display in the combo box cell editor.
 * 
 * @param id the id of the property
 * @param displayName the name to display for the property
 * @param valuesArray the list of possible values to display in the combo box
 */
public ObjectComboBoxPropertyDescriptor(Object id, String displayName, Object[] valuesArray) {
	super(id, displayName);
	values = valuesArray;
}
/**
 * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
 * <code>IPropertyDescriptor</code> method creates and returns a new
 * <code>ComboBoxCellEditor</code>.
 * <p>
 * The editor is configured with the current validator if there is one.
 * </p>
 */
public CellEditor createPropertyEditor(Composite parent) {
	CellEditor editor = new ObjectComboBoxCellEditor(parent, values, SWT.READ_ONLY);
	if (getValidator() != null)
		editor.setValidator(getValidator());
	return editor;
}

}
