/*
 * @(#)SelectionDragSourceListener.java
 * Created on 15/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;

/**
 * <code>SelectionDragSourceListener</code>.<p>
 * @author Kin Wong
 */
abstract public class SelectionDragSourceListener
	implements TransferDragSourceListener {
		private ISelectionProvider selectionProvider;
		private Transfer transfer;
		private List selecteds;
				
	protected SelectionDragSourceListener(
		ISelectionProvider selectionProvider, Transfer transfer) {
		Assert.isNotNull(selectionProvider);
		Assert.isNotNull(transfer);
		
		this.selectionProvider = selectionProvider;
		this.transfer = transfer;
	}
	
	protected List getSelectedObject() {
		return selecteds;
	}
	
	private IStructuredSelection getSelection() {
		ISelection selection = selectionProvider.getSelection();
		if(selection instanceof IStructuredSelection) 
		return (IStructuredSelection)selection;
		else
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.TransferDragSourceListener#getTransfer()
	 */
	public Transfer getTransfer() {
		return transfer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		event.doit = false;
		IStructuredSelection selection = getSelection();
		if(selection == null) return;
		
		selecteds = new ArrayList();
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			selecteds.add(it.next());
		}
		event.doit = (!selecteds.isEmpty());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
		selecteds = null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		if(!transfer.isSupportedType(event.dataType)) return;
		event.data = createData();
	}

	abstract protected Object createData();
}
