/*
 * @(#)Node.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

import java.io.Serializable;
import java.util.*;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;

/**
 * <code>Node</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class Node implements Serializable, IAdaptable {
	private static final long serialVersionUID = 6424227717462161145L;

	static public Node[] EMPTY_CHILDREN = new Node[0];
	static public Node EMPTY_NODE = new EmptyNode();
	
	private transient StructuredViewer viewer;
	private Node parent = null;
	private Map children;

	/**
	 * Constructs an instance of <code>Node</code>.
	 * <p>
	 */
	protected Node() {
	}
	
	/**
	 * returns the id that identifies this node in its parent.
	 * @return And Id that identifies this node in its parent.
	 */
	abstract public Object getId();
	abstract public String getText();
	
	protected boolean hasRefreshed() {
		return (children != null);
	}
	
	protected void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public boolean isActivated() {
		return viewer != null;
	}
	
	public StructuredViewer getViewer() {
		return viewer;
	}
	
	public boolean hasChildren() {
		if(children == null) return initialExpandRequired();
		return !children.isEmpty();
	}
	
	public void addChild(Object key, Node node) {
		addChildInternal(key, node);
		refreshVisual();
	}

	protected boolean removeChild(Object key) {
		if(removeChildInternal(key)) {
			refreshVisual();
			return true;
		}
		return false;
	}

	protected void renameChild(Object oldKey, Object newKey) {
		renameChildInternal(oldKey, newKey);
		updateVisual();
	}

	public Node getChild(Object key) {
		return (Node) children.get(key);
	}

	protected void addChildInternal(Object key, Node node) {
		Assert.isNotNull(key);
		Assert.isNotNull(node);
		Assert.isTrue(!node.isActivated());
		
		if(children == null) {
			children = new HashMap();
		}
		children.put(key, node);
		node.setParent(this);
		if(isActivated()) 
			node.activate(getViewer());
	}

	protected boolean removeChildInternal(Object key) {
		if(children == null) return false;
		Node node = (Node) children.remove(key);
		if(node == null) return false;
		
		if(node.isActivated()) 
			node.deactivate();
		node.setParent(null);
		return true;
	}

	protected void renameChildInternal(Object oldKey, Object newKey) {
		if(children == null) return;
		Node node = (Node) children.remove(oldKey);
		if (node != null) {
			children.put(newKey, node);
		}
	}

	protected boolean initialExpandRequired() {
		return false;
	}
	
	public Node[] getChildren() {
		if(children == null) {
			children = new HashMap();
			refreshChildren();
		}
		return (Node[])children.values().toArray(new Node[0]);
	}
	
	protected void activate(StructuredViewer newViewer) {
		//System.out.println("activate(this =" + this.toString() + ", new viewer = " + newViewer.toString() + ")");
		Assert.isNotNull(newViewer);
		Assert.isTrue(!isActivated());
		this.viewer = newViewer;

		if(!hasRefreshed()) return;	// Lazy activation
		Node[] childNodes = getChildren();
		for(int i = 0; i < childNodes.length; i++)
		childNodes[i].activate(newViewer);
	}
	
	protected void refreshChildren() {
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
				viewer.refresh(this, true);
//			}
//		});
	}
	
	protected void deactivate() {
		//System.out.println("deactivate(this =" + this.toString() + ", viewer = " + viewer.toString() + ")");
		Assert.isTrue(isActivated());
		
		if(hasRefreshed()) {
			Node[] childNodes = getChildren();
			for(int i = 0; i < childNodes.length; i++)
			childNodes[i].deactivate();
		}
		this.viewer = null;
	}

	protected void refreshVisual() {
		if(viewer == null) return;
		//System.out.println("refreshVisual()");
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
				viewer.refresh(this, true);
//			}
//		});
	}

	protected void updateVisual() {
		if(viewer == null) return;
		//viewer.update(this, null);
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
				viewer.refresh(this, true);
//			}
//		});
		//System.out.println("updateVisual()");
	}
	
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
}

final class EmptyNode extends Node {
	private static final long serialVersionUID = 6424227717462161145L;
	public String getText() {
		return "EmptyNode";
	}
	public Object getId() {
		return "EmptyNode";
	}
};
	
