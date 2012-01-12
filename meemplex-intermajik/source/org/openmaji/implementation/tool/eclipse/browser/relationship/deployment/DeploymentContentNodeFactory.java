/*
 * @(#)DeploymentContentNodeFactory.java
 * Created on 18/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment;

import org.openmaji.implementation.tool.eclipse.browser.patterns.common.ContentNodeFactory;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.*;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;

/**
 * <code>DeploymentContentNodeFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class DeploymentContentNodeFactory extends ContentNodeFactory {

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.patterns.common.IContentNodeFactory#createContentNode(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public Node createContentNode(Node node) {
		
		if(node instanceof MeemServerManagerNode) {
			MeemServerManagerNode meemServerManagerNode = (MeemServerManagerNode)node;
			return new 
			MeemServerManagerNode(
				meemServerManagerNode.getText(), 
				meemServerManagerNode.getProxy());
		}
		else
		if(node instanceof MeemServerNode) {
			MeemServerNode meemServerNode = (MeemServerNode)node;
			return new 
			MeemServerNode(
				meemServerNode.getText(), 
				meemServerNode.getProxy());
		}
		else
		
		if(node instanceof SubsystemFactoryNode) {
			SubsystemFactoryNode subsystemManager = (SubsystemFactoryNode)node;
			return new 
				SubsystemFactoryNode(
					subsystemManager.getText(), 
					subsystemManager.getProxy());
		}
		else
		if(node instanceof SubsystemNode) {
			SubsystemNode subsystem = (SubsystemNode)node;
			return new SubsystemNode(subsystem.getProxy());
		}
		else
		if(node instanceof WorksheetManagerNode) {
			WorksheetManagerNode worksheetManager = (WorksheetManagerNode)node;
			return new WorksheetManagerNode(worksheetManager.getText(), worksheetManager.getProxy());
		}
		else
		return super.createContentNode(node);
	}
}
