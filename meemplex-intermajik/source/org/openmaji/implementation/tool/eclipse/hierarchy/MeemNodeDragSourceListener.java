/*
 * @(#)MeemNodeDragSourceListener.java
 * Created on 29/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;


/**
 * <code>MeemNodeDragSourceListener</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemNodeDragSourceListener implements DragSourceListener {
	private ISelectionProvider selectionProvider;
	private List meemNodes;
	private Transfer transfer;
	
	public MeemNodeDragSourceListener(ISelectionProvider selectionProvider, Transfer transfer) {
		this.selectionProvider = selectionProvider;	
		this.transfer = transfer;
	}
	
	protected Transfer getTransfer() {
		return transfer;
	}
	
	protected IStructuredSelection getSelection() {
		ISelection selection = selectionProvider.getSelection();
		if(selection instanceof IStructuredSelection) 
		return (IStructuredSelection)selection;
		else
		return null;
	}
	
	protected List getMeemNodes() {
		return meemNodes;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		IStructuredSelection selection = getSelection();
		if(selection == null) return;
		
		meemNodes = new ArrayList();
		Iterator it = selection.iterator();
		while(it.hasNext()) {
			Object selected = it.next();
			if(!(selected instanceof MeemNode)) continue;
			meemNodes.add(selected);
		}

		if(meemNodes.isEmpty()) {
			event.doit = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		if(!transfer.isSupportedType(event.dataType)) return;

		NamedMeem[] namedMeems = new NamedMeem[meemNodes.size()];
		for(int i = 0; i < namedMeems.length; i++) {
			MeemNode meemNode = (MeemNode)meemNodes.get(i);
			namedMeems[i] = new NamedMeem(meemNode.getText(), meemNode.getMeemPath());
		}
		event.data = namedMeems;
	}

	/**
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
		meemNodes = null;
	}
}
