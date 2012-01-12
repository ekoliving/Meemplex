/*
 * @(#)FieldsPropertyDescriptor.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author mg
 * Created on 14/01/2003
 */
public class FieldsPropertyDescriptor extends PropertyDescriptor {

	private String implClassName;
	private String dialogMessage;

	public FieldsPropertyDescriptor(String name, String displayName, String implClassName, String dialogMessage) {
		super(name, displayName);
		this.implClassName = implClassName;
		this.dialogMessage = dialogMessage;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		return new FieldsPropertyCellEditor(parent, implClassName, dialogMessage);
	}
}
