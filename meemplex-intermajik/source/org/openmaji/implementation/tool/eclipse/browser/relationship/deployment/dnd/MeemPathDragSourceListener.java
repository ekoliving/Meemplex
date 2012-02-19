/*
 * @(#)MeemPathDragSourceListener.java
 * Created on 15/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.dnd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemPathTransfer;
import org.openmaji.implementation.tool.eclipse.ui.dnd.SelectionDragSourceListener;
import org.openmaji.meem.MeemPath;


/**
 * <code>MeemPathDragSourceListener</code>.<p>
 * @author Kin Wong
 */
public class MeemPathDragSourceListener extends SelectionDragSourceListener {
	/**
	 * Constructs an instance of <code>MeemPathDragSourceListener</code>.<p>
	 * @param selectionProvider
	 */
	public MeemPathDragSourceListener(ISelectionProvider selectionProvider) {
		super(selectionProvider, MeemPathTransfer.getInstance());
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.ui.dnd.SelectionDragSourceListener#createData()
	 */
	protected Object createData() {
		List meemPaths = new ArrayList();
		Iterator it = getSelectedObject().iterator();
		while(it.hasNext()) {
			MeemNode meem = (MeemNode)it.next();
			meemPaths.add(meem.getMeemPath());
		}
		return meemPaths.toArray(new MeemPath[0]);
	}
}
