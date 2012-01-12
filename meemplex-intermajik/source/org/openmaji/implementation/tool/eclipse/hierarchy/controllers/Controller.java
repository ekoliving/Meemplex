/*
 * @(#)Controller.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.controllers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;


/**
 * <code>Controller</code> represents the controller of a hierachical node. It
 * provides all the interaction between the view and the node.
 * <p>
 * @author Kin Wong
 */
public class Controller implements IAdaptable {
	static protected String GROUP_CREATION = Controller.class.getName() + ".CREATION";
	static protected String GROUP_MAIN = Controller.class.getName() + ".MAIN";
	static protected String GROUP_EDIT = Controller.class.getName() + ".EDIT";
	static protected String GROUP_IO = Controller.class.getName() + ".IO";
	static protected String GROUP_DESTRUCTIVE = Controller.class.getName() + ".DESTRUCTIVE";
	
	private IWorkbenchPartSite site;
	private Node node;
	private ViewPart viewPart;
	private StructuredViewer viewer;
	
	/**
	 * Constructs an instance of <code>Controller</code>.
	 * <p>
	 * @param node The node that binds to this controller.
	 */
	protected Controller(Node node) {
		this.node = node;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	/**
	 * Gets the structured viewer associates with this node.<p>
	 * @return A structured viewer associates with this node, null otherwise.
	 */	
	public StructuredViewer getViewer() {
		return viewer;
	}
	
	/**
	 * Creates all actions associates with this controller.<p>
	 */
	protected void createActions() {
	}
	
	/**
	 * Updates all actions associates with controller.<p>
	 *
	 */
	public void updateActions() {
	}
	
	/**
	 * Gets the node associates with this controller.<p>
	 * @return The node associates with this controller.
	 */
	public Node getNode() {
		return node;
	}
	
	/**
	 * Gets whether this controller has been activated.<p>
	 * @return true if this controller has been activated which requires 
	 * deactivation, false otherwise.
	 */
	public boolean isActivated() {
		return viewer != null;
	}
	
	/**
	 * Activates this controller with the supplied context.<p> 
	 * @param viewer
	 * @param viewPart
	 * @param site
	 */
	public void activate(StructuredViewer viewer, ViewPart viewPart, IWorkbenchPartSite site) {
		this.viewPart = viewPart;
		this.site = site;
		this.viewer = viewer;
		createActions();
	}
	
	/**
	 * Deactivates this controller from the previous context.<p>
	 */
	public void deactivate() {
		this.viewer = null;
		this.site = null;
		this.viewPart = null;
	}
	
	/**
	 * Gets the <code>IWorkbenchPartSite</code> of the activation context.<p>
	 * @return The <code>IWorkbenchPartSite</code> of the activation context, or
	 * null if the controller has not been activated.
	 */	
	protected IWorkbenchPartSite getSite() {
		return site;
	}
	
	/**
	 * Gets the <code>ViewPart</code> of the activation context.<p>
	 * @return The <code>ViewPart</code> of the activation context, or null if the
	 * controller has not been activated.
	 */
	public ViewPart getViewPart() {
		return viewPart;
	}
	
	/**
	 * Gets the <code>Shell</code> of the activation context.<p>
	 * @return The <code>Shell</code> of the activation context, or null if the 
	 * controller has not been activated.
	 */
	public Shell getShell() {
		return getSite().getShell();
	}

	/**
	 * Fills the given context menu.<p>
	 * @param menu The menu to be filled.
	 */
	public void fillMenu(IMenuManager menu) {
		if(getNode() == null) return;
		menu.add(new Separator(GROUP_CREATION));
		menu.add(new Separator(GROUP_MAIN));
		menu.add(new Separator(GROUP_EDIT));
		menu.add(new Separator(GROUP_IO));
		menu.add(new Separator(GROUP_DESTRUCTIVE));
	}
	
	/**
	 * Fills the given main menu.<p>
	 * @param menu The main menu to be filled.
	 */
	public void fillMainMenu(IMenuManager menu) {
		if(getNode() == null) return;
	}
	
	public void handleDoubleClick(DoubleClickEvent e) {
	}
}
