/*
 * @(#)TreeViewerNodeChangeAdaptor.java
 * Created on 17/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

import org.eclipse.jface.viewers.AbstractTreeViewer;

/**
 * <code>TreeViewerNodeChangeAdaptor</code>.
 * <p>
 * @author Kin Wong
 */
public class TreeViewerNodeChangeAdaptor implements NodeChangeListener {
	private AbstractTreeViewer treeViewer;
	/**
	 * Constructs an instance of <code>TreeViewerNodeChangeAdaptor</code>.
	 * <p>
	 * @param treeViewer
	 */
	public TreeViewerNodeChangeAdaptor(AbstractTreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeChangeListener#childAdded(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node, org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public void childAdded(Node parent, Node node) {
		treeViewer.refresh(parent);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeChangeListener#childRemoved(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node, org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public void childRemoved(Node parent, Node node) {
		treeViewer.refresh(parent);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeChangeListener#childRefreshed(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public void childRefreshed(Node node) {
		treeViewer.refresh(node);
	}
}
