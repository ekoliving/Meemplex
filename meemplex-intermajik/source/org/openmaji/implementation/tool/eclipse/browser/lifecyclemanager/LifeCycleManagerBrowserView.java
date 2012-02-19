/*
 * @(#)LifeCycleManagerBrowserView.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.lifecyclemanager;

import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemBrowserDragAdapter;
import org.openmaji.implementation.tool.eclipse.ui.dnd.MeemPathTransfer;
import org.openmaji.implementation.tool.eclipse.ui.view.SecurityView;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LifeCycleManagerBrowserView extends SecurityView {

	protected void createLoginView(Composite parent) {

		TreeViewer viewer = new TreeViewer(parent);

		IElementComparer comparer = new IElementComparer() {
			public boolean equals(final Object a, final Object b)
			{
				return a.equals(b);
			}

			public int hashCode(final Object element)
			{
				return element.hashCode();
			}
		};

		viewer.setComparer(comparer);
		viewer.setLabelProvider(new LifeCycleManagerLabelProvider());
		viewer.setContentProvider(new LifeCycleManagersContentProvider());
		viewer.setInput(new Object());


		initDragAndDrop(viewer);
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
//	 */
//	public void createPartControl(Composite parent) {
//		MajiPlugin.startMaji();
//		
//		viewer = new TreeViewer(parent);
//
//		viewer.setLabelProvider(new LifeCycleManagerLabelProvider());
//		viewer.setContentProvider(new LifeCycleManagersContentProvider());
//		
//		viewer.setInput(new Object());
//
//		initDragAndDrop(viewer);
//	}

	private void initDragAndDrop(TreeViewer viewer) {
		int ops = DND.DROP_MOVE;
		Transfer[] dragTransfers = new Transfer[] { MeemPathTransfer.getInstance()};
		viewer.addDragSupport(ops, dragTransfers, new MeemBrowserDragAdapter(viewer));
	}

}
