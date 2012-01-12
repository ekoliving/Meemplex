/*
 * @(#)MeemNodeDragSourceListener.java
 * Created on 15/06/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemNodeTransfer;
import org.openmaji.implementation.tool.eclipse.ui.dnd.SelectionDragSourceListener;


/**
 * <code>MeemNodeDragSourceListener</code>.<p>
 * @author mg
 */
public class MeemNodeDragSourceListener extends SelectionDragSourceListener {
	/**
	 * Constructs an instance of <code>MeemPathDragSourceListener</code>.<p>
	 * @param selectionProvider
	 */
	public MeemNodeDragSourceListener(ISelectionProvider selectionProvider) {
		super(selectionProvider, MeemNodeTransfer.getInstance());
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.dnd.SelectionDragSourceListener#createData()
	 */
	protected Object createData() {
		List meemTransfer = new ArrayList();
		Iterator it = getSelectedObject().iterator();
		while(it.hasNext()) {
			MeemNode meemNode = (MeemNode)it.next();
			TransferDrag dragObject = new TransferDrag(meemNode.getMeemPath(), ((MeemNode)meemNode.getParent()).getMeemPath());
			meemTransfer.add(dragObject);
		}
		return meemTransfer.toArray(new TransferDrag[0]);
	}
}
