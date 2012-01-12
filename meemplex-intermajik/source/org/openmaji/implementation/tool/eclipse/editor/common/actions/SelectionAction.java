/*
 * @(#)SelectionAction.java
 * Created on 7/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.actions;

/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Superclass for an action needing the current selection.
 */
public abstract class SelectionAction
	extends EditorPartAction
{
private ISelectionProvider provider;

/*
 * The current selection.
 */
private ISelection selection;

/**
 * Creates a <code>SelectionAction</code> and associates it with the given workbench part.
 * @param part the workbench part
 */
public SelectionAction(IWorkbenchPart part) {
	super(part);
}

/**
 * Creates a <code>SelectionAction</code> and associates it with the given workbench part.
 * @param part the workbench part
 */
public SelectionAction(IWorkbenchPart part, int style) {
	super(part, style);
}

/**
 * @see org.eclipse.gef.Disposable#dispose()
 */
public void dispose() {
	this.selection = StructuredSelection.EMPTY;
	super.dispose();
}

/**
 * Returns a <code>List</code> containing the currently
 * selected objects.
 * 
 * @return A List containing the currently selected objects.
 */
protected List getSelectedObjects() {
	if (!(getSelection() instanceof IStructuredSelection))
		return Collections.EMPTY_LIST;
	return ((IStructuredSelection)getSelection()).toList();
}

/**
 * Returns a <code>List</code> containing the clone of the currently selected
 * objects.
 * @return A List containing the currently selected objects.
 */
protected ArrayList getSelectedObjectClone() {
	ArrayList clone = new ArrayList();
	List selecteds = getSelectedObjects();
	if(selecteds != null) {
		for (Iterator iter = selecteds.iterator(); iter.hasNext();) {
			Object selected = iter.next();
			if(selected instanceof EditPart) clone.add(selected);
		}
	}
	return clone;
}

/**
 * Gets the current selection.
 * 
 * @return The current selection.
 */
protected ISelection getSelection() {
	return selection;
}

/**
 * Called when the selection is changed.
 */
protected void handleSelectionChanged() {
	refresh();
}

/**
 * Sets the current selection and calls on subclasses 
 * to handle the selectionChanged event.
 *
 * @param selection The new selection.
 */
protected void setSelection(ISelection selection) {
	this.selection = selection;
	handleSelectionChanged();
}

/**
 * May be used to provide an alternative selection source other than the workbench's
 * selection service. Use of this method is optional. The default value is
 * <code>null</code>, in which case the selection is obtained using the partsite's
 * selection service.
 * @param provider <code>null</code> or a selection provider
 */
public void setSelectionProvider(ISelectionProvider provider) {
	this.provider = provider;
}

/**
 * @see org.eclipse.gef.ui.actions.EditorPartAction#update()
 */
public void update() {
	if (provider != null)
		setSelection(provider.getSelection());
	else
		setSelection(
			getWorkbenchPart()
				.getSite()
				.getWorkbenchWindow()
				.getSelectionService()
				.getSelection());
}

}
