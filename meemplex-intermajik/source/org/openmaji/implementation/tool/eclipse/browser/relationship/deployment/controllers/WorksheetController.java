/*
 * @(#)WorksheetController.java
 * Created on 13/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.controllers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.openmaji.implementation.tool.eclipse.browser.common.actions.OpenWorksheetAction;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;


/**
 * <code>WorksheetController</code>.
 * <p>
 * @author Kin Wong
 */
public class WorksheetController extends MeemController {
	private Action openEditorAction;

	/**
	 * Constructs an instance of <code>WorksheetController</code>.
	 * <p>
	 * @param node
	 */
	public WorksheetController(MeemNode node) {
		super(node);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.controllers.MeemController#createActions()
	 */
	protected void createActions() {
		super.createActions();
		openEditorAction = new OpenWorksheetAction(this);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#handleDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void handleDoubleClick(DoubleClickEvent e) {
		openEditorAction.run();
	}
}
