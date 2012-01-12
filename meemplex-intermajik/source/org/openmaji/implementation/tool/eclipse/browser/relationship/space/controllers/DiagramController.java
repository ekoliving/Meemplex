/*
 * @(#)DiagramController.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.openmaji.implementation.tool.eclipse.browser.common.actions.NewWorksheetAction;
import org.openmaji.implementation.tool.eclipse.browser.common.actions.OpenWorksheetAction;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.DiagramNode;


/**
 * <code>DiagramController</code>.
 * <p>
 * @author Kin Wong
 */
public class DiagramController extends CategoryController {
	private OpenWorksheetAction openEditorAction;
	private NewWorksheetAction newViewAction;
	/**
	 * Constructs an instance of <code>DiagramController</code>.
	 * <p>
	 * @param node
	 */
	public DiagramController(DiagramNode node) {
		super(node);
	}
	
	DiagramNode getDiagramNode() {
		return (DiagramNode)getNode();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.CategoryController#createActions()
	 */
	protected void createActions() {
		super.createActions();
		openEditorAction = new OpenWorksheetAction(this);
		newViewAction = new NewWorksheetAction(this);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.CategoryController#destroyActions()
	 */
	protected void destroyActions() {
		openEditorAction = null;
		newViewAction = null;
		super.destroyActions();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#updateActions()
	 */
	public void updateActions() {
		super.updateActions();
		openEditorAction.update();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#handleDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void handleDoubleClick(DoubleClickEvent e) {
		openEditorAction.run();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.CategoryController#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		menu.appendToGroup(GROUP_MAIN, openEditorAction);
		if(newViewAction.isOpened())
		menu.appendToGroup(GROUP_MAIN, newViewAction);
	}
}
