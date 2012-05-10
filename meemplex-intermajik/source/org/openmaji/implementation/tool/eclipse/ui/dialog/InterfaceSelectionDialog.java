/*
 * @(#)InterfaceSelectionDialog.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dialog;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.openmaji.implementation.tool.eclipse.library.classlibrary.ClassDescriptor;


/**
 * @author mg
 * Created on 13/01/2003
 */
public class InterfaceSelectionDialog extends ElementListSelectionDialog {

	public InterfaceSelectionDialog(Shell parent, ClassDescriptor classDescriptor) {
		super(parent, new InterfaceLabelProvider());
		setIgnoreCase(false);
		setMultipleSelection(true);
		
		setElements(classDescriptor.getInterfaces().toArray());
		
	}

}
