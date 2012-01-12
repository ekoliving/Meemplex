/*
 * @(#)UndoablePropertySheetEntry.java
 * Created on 10/06/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.properties;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;

/**
 * <code>UndoablePropertySheetEntry</code>.<p>
 * @author Kin Wong
 */
public class UndoablePropertySheetEntry
	//extends org.eclipse.gef.internal.ui.properties.UndoablePropertySheetEntry
{

	/**
	 * Constructs an instance of <code>UndoablePropertySheetEntry</code>.<p>
	 * @param stack
	 */
	public UndoablePropertySheetEntry(CommandStack stack) {
		//super(stack);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.internal.ui.properties.UndoablePropertySheetEntry#setValues(java.lang.Object[])
	 */
	public void setValues(Object[] objects) {
		//EditPart primarySelection = null;
		
		for(int i = objects.length - 1; i >= 0; i--) {
			Object selected = objects[i];
			if(selected instanceof EditPart) {
				EditPart selectedEditPart = (EditPart)selected;
				if(selectedEditPart.getSelected() == EditPart.SELECTED_PRIMARY) {
					//primarySelection = selectedEditPart;
					break;
				}
			}
		}
//		if(primarySelection == null) {
//			super.setValues(new Object[0]);
//		}
//		super.setValues(new Object[]{primarySelection});
	}
}
