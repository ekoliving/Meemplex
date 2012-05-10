/*
 * @(#)DeploymentControllerFactory.java
 * Created on 20/05/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.deployment;

import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.controllers.*;
import org.openmaji.implementation.tool.eclipse.browser.relationship.deployment.nodes.*;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerFactory;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;

/**
 * <code>DeploymentControllerFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class DeploymentControllerFactory extends ControllerFactory {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerFactory#create(org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node)
	 */
	public Controller create(Node node) {
		if(node instanceof SubsystemFactoryNode) {
			return new SubsystemFactoryController((SubsystemFactoryNode)node);
		}
		else
		if(node instanceof SubsystemNode) {
			return new SubsystemController((SubsystemNode)node);
		}
		else
		if(node instanceof MeemNode) {
			return new MeemController((MeemNode)node);
		}
		return null;
	}

}
