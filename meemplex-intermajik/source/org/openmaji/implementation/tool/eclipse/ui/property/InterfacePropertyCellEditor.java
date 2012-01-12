/*
 * @(#)InterfacePropertyCellEditor.java
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

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author mg
 * Created on 14/01/2003
 */
public class InterfacePropertyCellEditor extends DialogCellEditor {

	private Label label;
	//private String implClassName;
	//private String dialogMessage;

	protected InterfacePropertyCellEditor(Composite parent, String implClassName, String dialogMessage) {
		super(parent);
		//this.implClassName = implClassName;
		//this.dialogMessage = dialogMessage;
	}

	protected Control createContents(Composite cell) {
		label = new Label(cell, SWT.LEFT);
		label.setFont(cell.getFont());
		label.setBackground(cell.getBackground());
		return label;
	}

	protected Object openDialogBox(Control cellEditorWindow) {
//		ClassDescriptor classDescriptor = (ClassDescriptor) ClassList.getInstance().get(implClassName);
//
//		String value = (String) getValue();
//
//		List initialSelections = new ArrayList();
//		initialSelections.add(value);
//
//		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
//		InterfaceSelectionDialog dialog = new InterfaceSelectionDialog(shell, classDescriptor);
//		dialog.setInitialElementSelections(initialSelections);
//		dialog.setMultipleSelection(false);
//
//		dialog.setMessage(dialogMessage);
//
//		if (dialog.open() == Window.OK) {
//			Object[] result = dialog.getResult();
//
//			if (result.length == 0)
//				return value;
//
//			return (String)result[0];
//		}
//
//		return value;
		return null;
	}

	protected void updateContents(Object value) {
		if (value != null)
			label.setText(value.toString());
		else
			label.setText("");
	}


}