/*
 * @(#)MeemController.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers;

import java.util.ArrayList;
import java.util.Collection;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.tool.eclipse.client.CategoryEntryNameFactory;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.ui.dialog.DuplicateInputValidator;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.space.CategoryEntry;


/**
 * <code>MeemController</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemController extends Controller {
	private Action removeAction;
	private Action destroyAction;
	private Action renameAction;
	
	/**
	 * Constructs an instance of <code>MeemController</code>.
	 * <p>
	 * @param node
	 */
	public MeemController(MeemNode node) {
		super(node);
	}
	
	protected MeemNode getMeemNode() {
		return (MeemNode)getNode();
	}
	
	protected void createActions() {
		removeAction = new Action("Remove Reference") {
			public void run() {
				remove();
			}
		};
		removeAction.
		setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		removeAction.setToolTipText("Remove");

		destroyAction = new Action("Destroy Original") {
			public void run() {
				destroy();
			}
		};
		destroyAction.setImageDescriptor(Images.ICON_MEEM_STATE_ABSENT);
		destroyAction.setToolTipText("Destroy");

		renameAction = new Action("Rename...") {
			public void run() {
				rename();
			}
		};
		renameAction.setImageDescriptor(Images.ICON_RENAME);
		renameAction.setToolTipText("Rename");
		
		
	}
	
	protected void destroyActions() {
		removeAction = null;
		destroyAction = null;
		renameAction = null;
	}
	
	public void activate(StructuredViewer viewer, ViewPart viewPart, IWorkbenchPartSite site) {
		super.activate(viewer, viewPart, site);
		createActions();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.common.controllers.Controller#deactivate()
	 */
	public void deactivate() {
		destroyActions();
		super.deactivate();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(adapter == MeemClientProxy.class) {
			return getMeemNode().getProxy();
		}
		else
		return super.getAdapter(adapter);
	}

	protected CategoryProxy getParentCategory() {
		Node parent = getNode().getParent();
		if(!(parent instanceof CategoryNode)) return null;
		return ((CategoryNode)parent).getCategory();
	}

	private void remove() {
		CategoryProxy category = getParentCategory();
		category.removeEntry(getNode().getText());
	}

	private void destroy() {
		remove();
		getMeemNode().getProxy().getLifeCycle().changeLifeCycleState(LifeCycleState.ABSENT);
	}
		
	private void rename() {
		CategoryProxy category = getParentCategory();
		String oldName = getNode().getText();
		InputDialog dlg = 
			new InputDialog(getShell(), "Rename", "Enter new name:", 
			oldName, 
			new DuplicateInputValidator(getCategoryEntryNames(category), "Name already exists"));

		if(dlg.open() != Window.OK) return;
		String newName = CategoryEntryNameFactory.createUniqueEntryName(category, dlg.getValue());
		category.renameEntry(oldName, newName);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.common.controllers.Controller#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		CategoryProxy category = getParentCategory();
		if(category == null) return;
		
		if(!category.isReadOnly()) {
			menu.appendToGroup(GROUP_EDIT, renameAction);
			menu.appendToGroup(GROUP_DESTRUCTIVE, removeAction);
			menu.appendToGroup(GROUP_DESTRUCTIVE, destroyAction);
		}
	}
	
	/*
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#fillMainMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMainMenu(IMenuManager menu) {
		super.fillMainMenu(menu);
		//CategoryProxy category = getParentCategory();
		/*
		if (menu.find(importAction.getId()) != null) {
			menu.remove(importAction.getId());
			menu.remove(exportAction.getId());
		}

		if(category == null) return;		
		
		menu.appendToGroup(MajiPlugin.GROUP_START, importAction);
		menu.appendToGroup(MajiPlugin.GROUP_START, exportAction);
		*/
	}

	
	protected Collection getCategoryEntryNames(CategoryProxy category) {
		CategoryEntry[] entries = category.getEntryArray();
		ArrayList entryNames = new ArrayList();
		for(int i = 0; i < entries.length; i++) entryNames.add(entries[i].getName());
		return entryNames;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.common.controllers.Controller#handleDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void handleDoubleClick(DoubleClickEvent e) {
		TreeViewer viewer = (TreeViewer)getMeemNode().getViewer();
		boolean expanded = viewer.getExpandedState(getNode());
		if(expanded) {
			viewer.collapseToLevel(getNode(), 1);
		}
		else {
			viewer.expandToLevel(getNode(), 1);
			viewer.refresh(getNode());
		}
	}


}
