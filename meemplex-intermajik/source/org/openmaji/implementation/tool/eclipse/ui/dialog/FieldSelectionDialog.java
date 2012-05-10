/*
 * @(#)FieldSelectionDialog.java
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
public class FieldSelectionDialog extends ElementListSelectionDialog {

	public FieldSelectionDialog(Shell parent, ClassDescriptor classDescriptor) {
		super(parent, new FieldLabelProvider());
		setIgnoreCase(false);
		setMessage("Select the outgoing facets");
		setMultipleSelection(true);

		//setElements(filter(classDescriptor.getFields()).toArray());
		setElements(classDescriptor.getFields().toArray());
	}
	
	public void setMessage(String message) {
		super.setMessage(message);
	}

//	private Collection filter(Collection fields) {
//		Collection classTypeFields = new ArrayList();
//		
//		for (Iterator i = fields.iterator(); i.hasNext();) {
//			FieldDescriptor descriptor = (FieldDescriptor)i.next();
//			if (descriptor.getType().startsWith("L")) {
//				classTypeFields.add(descriptor);
//			}
//		}
//
//		return classTypeFields;
//	}
}
