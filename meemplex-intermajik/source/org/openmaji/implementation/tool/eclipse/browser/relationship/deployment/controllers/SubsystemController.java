/*
 * @(#)SubsystemController.java
 * Created on 13/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.controllers;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemFactoryNode;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.SubsystemNode;
import org.openmaji.implementation.tool.eclipse.client.SubsystemProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemState;


/**
 * <code>SubsystemController</code>.
 * <p>
 * @author Kin Wong
 */
public class SubsystemController extends MeemController {
	static private final String SUBSYSTEM_COMMISSION_NAME = "Commission";
	static private final String SUBSYSTEM_DECOMMISSION_NAME = "Decommission";
	static private final String SUBSYSTEM_START_NAME = "Start";
	static private final String SUBSYSTEM_STOP_NAME = "Stop";
	static private final String SUBSYSTEM_REMOVE_NAME = "Remove";

	private Action subsystemCommissionAction;
	private Action subsystemDecommissionAction;
	private Action subsystemStartAction;
	private Action subsystemStopAction;
	private Action subsystemRemoveAction;
	
	/**
	 * Constructs an instance of <code>SubsystemController</code>.<p>
	 * @param subsystemNode
	 */
	public SubsystemController(SubsystemNode subsystemNode) {
		super(subsystemNode);
	}
	
	protected SubsystemNode getSubsystemNode() {
		return (SubsystemNode)getNode();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers.MeemController#createActions()
	 */
	protected void createActions() {
		super.createActions();
		
		//	=== Subsystem Commission Action	===
		subsystemCommissionAction = 
		new Action(SUBSYSTEM_COMMISSION_NAME) {
			public void run() {
				commissionSubsystem(getSubsystemNode());
			}
		};
		subsystemCommissionAction.setToolTipText(SUBSYSTEM_COMMISSION_NAME);
		subsystemCommissionAction.setImageDescriptor(Images.ICON_START);
		subsystemCommissionAction.setHoverImageDescriptor(Images.ICON_START);
		
		//	=== Subsystem DeCommission Action	===
		subsystemDecommissionAction = 
		new Action(SUBSYSTEM_DECOMMISSION_NAME) {
			public void run() {
				decommissionSubsystem(getSubsystemNode());
			}
		};
		subsystemDecommissionAction.setToolTipText(SUBSYSTEM_DECOMMISSION_NAME);
		subsystemDecommissionAction.setImageDescriptor(Images.ICON_START);
		subsystemDecommissionAction.setHoverImageDescriptor(Images.ICON_START);
		
		
		//=== Subsystem Start Action	===
		subsystemStartAction = 
		new Action(SUBSYSTEM_START_NAME) {
			public void run() {
				startSubsystem(getSubsystemNode());
			}
		};
		subsystemStartAction.setToolTipText(SUBSYSTEM_START_NAME);
		subsystemStartAction.setImageDescriptor(Images.ICON_START);
		subsystemStartAction.setHoverImageDescriptor(Images.ICON_START);
		
		//=== Subsystem Stop Action	===
		subsystemStopAction = 
		new Action(SUBSYSTEM_STOP_NAME) {
			public void run() {
				stopSubsystem(getSubsystemNode());
			}
		};
		subsystemStopAction.setToolTipText(SUBSYSTEM_STOP_NAME);
		subsystemStopAction.setImageDescriptor(Images.ICON_STOP);
		subsystemStopAction.setHoverImageDescriptor(Images.ICON_STOP);

		//=== Subsystem Remove Action	===
		subsystemRemoveAction = 
		new Action(SUBSYSTEM_REMOVE_NAME) {
			public void run() {
				removeSubsystem(getSubsystemNode());
			}
		};
		subsystemRemoveAction.setToolTipText(SUBSYSTEM_REMOVE_NAME);
		subsystemRemoveAction.setImageDescriptor(Images.ICON_DELETE);
		subsystemRemoveAction.setHoverImageDescriptor(Images.ICON_DELETE);
	}
	
	private void commissionSubsystem(SubsystemNode subsystemNode) {
		subsystemNode.getSubsystem().changeCommissionState(CommissionState.COMMISSIONED);
	}
	
	private void decommissionSubsystem(SubsystemNode subsystemNode) {
		subsystemNode.getSubsystem().changeCommissionState(CommissionState.NOT_COMMISSIONED);
	}
	
	private void startSubsystem(SubsystemNode subsystemNode) {
		subsystemNode.getSubsystem().changeSubsystemState(SubsystemState.STARTED);
	}
	
	private void stopSubsystem(SubsystemNode subsystemNode) {
		subsystemNode.getSubsystem().changeSubsystemState(SubsystemState.STOPPED);
	}
	
	private void removeSubsystem(SubsystemNode subsystemNode) {
		// get parent subsystem factory
		
		Node parent = subsystemNode.getParent();
		if (parent instanceof SubsystemFactoryNode) {
			SubsystemFactoryNode subsystemFactoryNode = (SubsystemFactoryNode) parent;
			// tell parent to destroy subsystem
			subsystemFactoryNode.getSubsystemFactory().destroySubsystem(subsystemNode.getProxy().getUnderlyingMeem());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller#fillMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenu(IMenuManager menu) {
		super.fillMenu(menu);
		SubsystemProxy subsystem = getSubsystemNode().getSubsystem();
		if (subsystem.isReadOnly()) return;
		
		if(subsystem.isCommissioned()) {
			if(subsystem.isStarted()) {
				menu.appendToGroup(GROUP_MAIN, subsystemStopAction);
			}
			else {
				menu.appendToGroup(GROUP_MAIN, subsystemStartAction);
				menu.appendToGroup(GROUP_MAIN, subsystemDecommissionAction);
			}			
		}
		else {
			menu.appendToGroup(GROUP_MAIN, subsystemCommissionAction);
		}	
		
		if(subsystem.isSetEmpty())
		menu.appendToGroup(GROUP_DESTRUCTIVE, subsystemRemoveAction);
	}
}
