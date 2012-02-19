/*
 * @(#)SubsystemFactoryController.java
 * Created on 13/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.controllers;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.openmaji.implementation.server.manager.lifecycle.subsystem.SubsystemMeem;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemFactoryNode;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;


/**
 * <code>SubsystemFactoryController</code>.
 * <p>
 * @author Kin Wong
 */
public class SubsystemFactoryController extends MeemController {
	static private final String SUBSYSTEM_CREATE_NAME = "Create Subsystem";

	private Action subsystemCreateAction;
	
	/**
	 * Constructs an instance of <code>SubsystemFactoryController</code>.<p>
	 * @param subsystemFactoryNode
	 */
	public SubsystemFactoryController(SubsystemFactoryNode subsystemFactoryNode) {
		super(subsystemFactoryNode);
	}

	protected SubsystemFactoryNode getSubsystemFactoryNode() {
		return (SubsystemFactoryNode) getNode();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#createActions()
	 */
	protected void createActions() {
		super.createActions();

		//=== Subsystem Create Action ===
		subsystemCreateAction = new Action(SUBSYSTEM_CREATE_NAME) {
			public void run() {
				createSubsystem(getSubsystemFactoryNode());
			}
		};
		subsystemCreateAction.setToolTipText(SUBSYSTEM_CREATE_NAME);
		subsystemCreateAction.setImageDescriptor(
			Images.loadIcon("create_subsystem16.gif"));
		subsystemCreateAction.setHoverImageDescriptor(
			subsystemCreateAction.getImageDescriptor());
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		if (getSubsystemFactoryNode().getSubsystemFactory().isReadOnly())
			return;
		menu.appendToGroup(GROUP_CREATION, subsystemCreateAction);
	}

	private void createSubsystem(SubsystemFactoryNode subsystemFactoryNode) {
		InputDialog dlg =
			new InputDialog(
				getShell(),
				"Create Subsystem",
				"Enter name:",
				"Subsystem",
				null);
		if (dlg.open() != Window.OK)
			return;
		subsystemFactoryNode.getProxy().getSubsystemFactoryProxy().createSubsystem(getMeemDefinition(dlg.getValue()));
	}
	
	private MeemDefinition getMeemDefinition(String name) {
		MeemDefinition meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(SubsystemMeem.class);
		
		meemDefinition.getMeemAttribute().setIdentifier(name);
		
		return meemDefinition;
	}
}
