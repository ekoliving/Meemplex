/*
 * @(#)FieldPropertyDescriptor.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
public class FieldPropertyDescriptor extends PropertyDescriptor {

	private String implClassName;
	private String dialogMessage;

	public FieldPropertyDescriptor(String id, String displayName, String implClassName, String dialogMessage) {
		super(id, displayName);
		this.implClassName = implClassName;
		this.dialogMessage = dialogMessage;
	}

	public CellEditor createPropertyEditor(Composite parent) {
		return new FieldPropertyCellEditor(parent, implClassName, dialogMessage);
	}
}
