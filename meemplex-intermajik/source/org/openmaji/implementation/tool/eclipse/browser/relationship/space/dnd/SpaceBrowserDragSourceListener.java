/*
 * @(#)SpaceBrowserDragSourceListener.java
 * Created on 29/03/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space.dnd;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeDragSourceListener;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;


/**
 * <code>SpaceBrowserDragSourceListener</code>.
 * <p>
 * @author Kin Wong
 */
public class SpaceBrowserDragSourceListener
	extends MeemNodeDragSourceListener {
		private boolean copyOnly;
		
	/**
	 * Constructs an instance of <code>SpaceBrowserDragSourceListener</code>.
	 * <p>
	 * @param selectionProvider
	 * @param transfer
	 */
	public SpaceBrowserDragSourceListener(ISelectionProvider selectionProvider, boolean copyOnly, Transfer transfer) {
		super(selectionProvider, transfer);
		this.copyOnly = copyOnly;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeDragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
		if(event.doit == false) return;
		if(copyOnly) return;
		if(event.detail == DND.DROP_MOVE) {
			Iterator it = getMeemNodes().iterator();
			
			while (it.hasNext()) {
				MeemNode node = (MeemNode)it.next();
				if(!(node.getParent() instanceof CategoryNode)) continue;
				
				CategoryNode category = (CategoryNode)node.getParent();
				category.getCategory().removeEntry(node.getText());
			}
		}
		super.dragFinished(event);
	}
	
}
