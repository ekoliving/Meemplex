/*
 * @(#)StructuredViewerNodeChangeAdaptor.java
 * Created on 17/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

import org.eclipse.jface.viewers.StructuredViewer;

/**
 * <code>StructuredViewerNodeChangeAdaptor</code>.
 * <p>
 * @author Kin Wong
 */
public class StructuredViewerNodeChangeAdaptor implements NodeChangeListener {
	private StructuredViewer viewer;
	
	/**
	 * Constructs an instance of <code>StructuredViewerNodeChangeAdaptor</code>.
	 * <p>
	 * 
	 */
	public StructuredViewerNodeChangeAdaptor(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeChangeListener#childAdded(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node, org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public void childAdded(Node parent, Node node) {			
		viewer.refresh(node);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeChangeListener#childRemoved(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node, org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public void childRemoved(Node parent, Node node) {
		viewer.refresh(parent);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeChangeListener#childRefreshed(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public void childRefreshed(Node node) {
		viewer.update(node, null);
	}
}
