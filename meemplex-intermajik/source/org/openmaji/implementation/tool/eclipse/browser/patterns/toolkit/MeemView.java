/*
 * @(#)MeemView.java
 * Created on 26/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.patterns.toolkit;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.openmaji.implementation.tool.eclipse.browser.patterns.common.PatternKitView;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeDragSourceListener;
import org.openmaji.implementation.tool.eclipse.hierarchy.MeemNodeLabelProvider;
import org.openmaji.implementation.tool.eclipse.hierarchy.NodeSorter;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeTreeContentProvider;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeemTransfer;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;


/**
 * <code>MeemView</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemView extends PatternKitView {

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#createTreeViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		TreeViewer treeViewer = super.createTreeViewer(parent);
		
		MeemClientProxy rootProxy = getRootProxy();
		if(rootProxy != null) {
			Node root = new CategoryNode("Toolkit (Meems)", rootProxy);
			treeViewer.setContentProvider(new NodeTreeContentProvider());
			treeViewer.setLabelProvider(new MeemNodeLabelProvider());
			treeViewer.setSorter(new NodeSorter());
			treeViewer.setInput(root);
		}
		return treeViewer;
	}
	
	private MeemClientProxy getRootProxy() {
		MeemPath meemViewPath = MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SYSTEM_PATTERN_MEEM);
		return InterMajikClientProxyFactory.getInstance().locate(meemViewPath);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.ExplorerView#initialiseDragAndDrop()
	 */
	protected void initialiseDragAndDrop() {
		int ops = DND.DROP_COPY;
		Transfer[] dragTransfers = new Transfer[] { NamedMeemTransfer.getInstanceForClone()};
		getContentViewer().
		addDragSupport(ops, dragTransfers, 
			new MeemNodeDragSourceListener(getContentViewer(), 
			NamedMeemTransfer.getInstanceForClone()));
	}
}
