/*
 * @(#)MeemRegistryBrowserView.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemregistry;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.openmaji.implementation.tool.eclipse.ui.dnd.*;
import org.openmaji.implementation.tool.eclipse.ui.view.SecurityView;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemRegistryBrowserView extends SecurityView {

	protected void createLoginView(Composite parent) {

		RegistriesContentProvider registriesContentProvider = new RegistriesContentProvider();

		ITreeContentProvider contentProvider = (ITreeContentProvider) registriesContentProvider;

		TreeViewer viewer = new TreeViewer(parent);
		viewer.setLabelProvider(new MeemRegistryLabelProvider());
		viewer.setContentProvider(contentProvider);
		viewer.setInput(new Object());

		initDragAndDrop(viewer);
	}

	private void initDragAndDrop(TreeViewer viewer) {
		int ops = DND.DROP_MOVE;
		Transfer[] dragTransfers = new Transfer[] { MeemPathTransfer.getInstance()};
		viewer.addDragSupport(ops, dragTransfers, new MeemBrowserDragAdapter(viewer));
	}
}
