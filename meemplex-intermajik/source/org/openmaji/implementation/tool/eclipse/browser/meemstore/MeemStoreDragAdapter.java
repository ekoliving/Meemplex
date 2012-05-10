/*
 * @(#)MeemSpaceDragAdapter.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemstore;

import java.util.Iterator;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemPathTransfer;
import org.openmaji.meem.MeemPath;


/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class MeemStoreDragAdapter extends DragSourceAdapter {

	private ListViewer viewer;

	public MeemStoreDragAdapter(ListViewer viewer) {
		this.viewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		if (MeemPathTransfer.getInstance().isSupportedType(event.dataType)) {

			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

			MeemPath[] meemPaths = new MeemPath[selection.size()];

			int iUpto = 0;

			for (Iterator i = selection.iterator(); i.hasNext();) {
				meemPaths[iUpto] = (MeemPath) i.next();
				iUpto++;
			}

			event.data = meemPaths;

		}
	}

}
