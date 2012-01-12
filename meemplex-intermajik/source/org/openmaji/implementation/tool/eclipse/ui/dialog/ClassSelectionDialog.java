/*
 * @(#)ClassSelectionDialog.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dialog;

import java.util.Collection;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * @author mg
 * Created on 13/01/2003
 */
public class ClassSelectionDialog extends ElementListSelectionDialog {

	public static int CLASSES = 1;
	public static int INTERFACES = 2;

	public ClassSelectionDialog(Shell parent, int filter) {
		this(parent, filter, null);
	}

	public ClassSelectionDialog(Shell parent, int filter, Collection excludeList) {

		super(parent, new ClassLabelProvider());

		setIgnoreCase(false);
		setMessage("Select an implementation class");
		setMultipleSelection(false);

//		if (filter == CLASSES)
//			setElements(ClassList.getInstance().classesToObjectArray(excludeList));
//
//		if (filter == INTERFACES)
//			setElements(ClassList.getInstance().interfacesToObjectArray(excludeList));
//
//		if (filter == (CLASSES + INTERFACES))
//			setElements(ClassList.getInstance().toObjectArray(excludeList));

	}

	public ClassSelectionDialog(Shell parent, Collection includeList) {

		super(parent, new ClassLabelProvider());

		setIgnoreCase(false);
		setMessage("Select an implementation class");
		setMultipleSelection(false);

		setElements(includeList.toArray());

	}

}
