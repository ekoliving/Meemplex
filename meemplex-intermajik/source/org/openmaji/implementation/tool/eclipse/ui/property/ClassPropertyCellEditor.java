/*
 * @(#)ClassPropertyCellEditor.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.openmaji.implementation.tool.eclipse.library.classlibrary.ClassDescriptor;
import org.openmaji.implementation.tool.eclipse.ui.dialog.ClassSelectionDialog;


/**
 * @author mg
 * Created on 14/01/2003
 */
public class ClassPropertyCellEditor extends DialogCellEditor {

	private Label label;
	private Collection includeList = null;
	private Collection excludeList = null;
	
	/**
	 * Only one of includeList or excludeList can be non-null.
	 * If both are non-null, includeList is used.
	 * @param parent
	 * @param includeList Collection of class names to display in dialog
	 * @param excludeList Collection of class names to exclude from dialog
	 */
	protected ClassPropertyCellEditor(Composite parent, Collection includeList, Collection excludeList) {
		super(parent);
		this.includeList = includeList;
		this.excludeList = excludeList;
	}

	protected Control createContents(Composite cell) {
		label = new Label(cell, SWT.LEFT);
		label.setFont(cell.getFont());
		label.setBackground(cell.getBackground());
		return label;
	}

	protected Object openDialogBox(Control cellEditorWindow) {
		String value = (String)getValue();

		List initialSelections = new ArrayList();
		initialSelections.add(value);

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		ClassSelectionDialog dialog;
		if (includeList != null) {
			dialog = new ClassSelectionDialog(shell, includeList);
		} else {
			dialog = new ClassSelectionDialog(shell, ClassSelectionDialog.CLASSES, excludeList);
		}
		dialog.setInitialElementSelections(initialSelections);

		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();

			if (result.length == 0)
				return value;

			return ((ClassDescriptor)result[0]).getClassName();
		}
		
		return value;
	}
	
	protected void updateContents(Object value) {
		if (value != null)
			label.setText(value.toString());
		else
			label.setText("");
	}

}