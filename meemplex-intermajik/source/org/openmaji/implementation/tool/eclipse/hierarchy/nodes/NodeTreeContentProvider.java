/*
 * @(#)NodeTreeContentProvider.java
 * Created on 29/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;



/**
 * <code>NodeTreeContentProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class NodeTreeContentProvider implements ITreeContentProvider {
	private Node rootNode;
	
	public NodeTreeContentProvider() {
	}

	protected void setRootNode(Node rootNode) {
		this.rootNode = rootNode;
	}
	
	protected Node getRootNode() {
		return rootNode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		if(getRootNode() != null) getRootNode().deactivate();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(!(parentElement instanceof Node)) return Node.EMPTY_CHILDREN;
		Node node = (Node)parentElement;
		return node.getChildren();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if(!(element instanceof Node)) return null;
		Node node = (Node)element;
		return node.getParent();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if(!(element instanceof Node)) return false;
		Node node = (Node)element;
		return node.hasChildren();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		Node rootNode = getRootNode();
		if(rootNode == null) return new Object[0];
		return getRootNode().getChildren();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		/*
		System.err.println("inputChanged(" + 
			viewer.toString() + ", oldInput = " + 
			oldInput + ", newInput = " + 
			newInput);
		*/
		if(!(viewer instanceof StructuredViewer)) return;
		StructuredViewer structuredViewer = (StructuredViewer)viewer;		
//		if(newInput == getRootNode())return;

		if(oldInput instanceof Node) {
			// Deactivates the previous root node
			((Node)oldInput).deactivate();
			setRootNode(null);
		}
		
		if(!(newInput instanceof Node)) return;
		Node newNode = (Node)newInput;
		newNode.activate(structuredViewer);
		setRootNode(newNode);
		//viewer.refresh();
	}
}
