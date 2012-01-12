/*
 * @(#)SelectionChangeAdaptor.java
 * Created on 18/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.view;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * <code>SelectionChangeAdaptor</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class SelectionChangeAdaptor implements ISelectionChangedListener {
	private Object[] selectedObjects;
	
	protected Object[] getSelection() {
		return selectedObjects;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		
		boolean changed = true;
		
		if(selection != null) {
			if(selectedObjects == null) {
				// No selection before, so definitely a change
				selectedObjects = selection.toArray();
			}
			else {
				Object[] newSelectedObjects = selection.toArray();
				if(selectedObjects.length == newSelectedObjects.length) {
					changed = false;	// Assume no change
					// Do a deeper comparison here
					for (int i = 0; i < selectedObjects.length; i++) {
						if(!selectedObjects[i].equals(newSelectedObjects[i])) {
							changed = true;
							break;
						}
					}
					selectedObjects = newSelectedObjects;
				}
			}
		}
		if(changed) selectionChanged(event, selectedObjects);
	}

	abstract public void 
	selectionChanged(SelectionChangedEvent event, Object[] selectedObject);
}
