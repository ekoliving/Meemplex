/*
 * @(#)MeemController.java
 * Created on 21/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.controllers;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemNode;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.SubsystemProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.ui.dialog.SimpleInputValidator;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>MeemController</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemController extends Controller {
	
	private static final Logger logger = Logger.getAnonymousLogger();
	
	//static private final String MEEM_RENAME_NAME = "Rename...";
	
	private Action destroyAction;
	private Action renameAction;

	/**
	 * Constructs an instance of <code>MeemController</code>.
	 * <p>
	 * @param meemNode
	 */
	public MeemController(MeemNode meemNode) {
		super(meemNode);
	}

	protected MeemNode getMeemNode() {
		return (MeemNode)getNode();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#createActions()
	 */
	protected void createActions() {
		super.createActions();
		
		destroyAction = new Action("Destroy Original") {
			public void run() {
				destroy();
			}
		};
		destroyAction.setImageDescriptor(Images.ICON_MEEM_STATE_ABSENT);
		destroyAction.setToolTipText("Destroy");

		//=== Rename Action	===
		renameAction = 
		new Action("Rename...") {
			public void run() {
				renameMeem(getMeemNode());
			}
		};
		renameAction.setToolTipText("Rename");
		renameAction.setImageDescriptor(Images.ICON_RENAME);
		renameAction.setHoverImageDescriptor(Images.ICON_RENAME);
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		if(getMeemNode().isLabelDefined()) {
			return;
		}
		
		ConfigurationHandlerProxy config = getMeemNode().getProxy().getConfigurationHandler();
		if (config.isReadOnly()) {
			return;
		}
		
		menu.appendToGroup(GROUP_EDIT, renameAction);
		menu.appendToGroup(GROUP_DESTRUCTIVE, destroyAction);
	}
	
	protected SubsystemProxy getParentSubsystem() {
		Node parent = getNode().getParent();
		if(!(parent instanceof SubsystemNode)) {
			logger.log(Level.INFO, "parent is: " + parent);
			return null;
		}
		return ((SubsystemNode)parent).getSubsystem();
	}

	private void remove() {
		SubsystemProxy subsystem = getParentSubsystem();
		if (subsystem == null) {
			logger.log(Level.INFO, "Cannot remove from category because parent is not a category"); 
		}
		else {
			//subsystem.removeEntry(getNode().getText());
		}
		// TODO make sure meem is removed from subsystem proxy, probably subsystem needs to have listener
	}

	private void destroy() {
		remove();
		getMeemNode().getProxy().getLifeCycle().changeLifeCycleState(LifeCycleState.ABSENT);
	}

	private void renameMeem(MeemNode meemNode) {
		ConfigurationHandlerProxy config = 
			meemNode.getProxy().getConfigurationHandler();
			
		Object value = config.
			getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);
		if(value == null) return;
		
		InputDialog dlg =
			new InputDialog(
				getShell(),
				"Rename Meem",
				"Enter name:",
				value.toString(),
				new SimpleInputValidator("Meem name has not been changed.", 
					value.toString()));
		
		if(dlg.open() != Window.OK) return;
		config.valueChanged(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER, 
			dlg.getValue());
	}
}
