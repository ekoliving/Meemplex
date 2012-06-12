/*
 * @(#)SpaceControllerFactory.java
 * Created on 28/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space.controllers;

import org.openmaji.implementation.tool.eclipse.browser.relationship.space.DiagramNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerFactory;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.UnavailableMeemNode;

/**
 * <code>SpaceControllerFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class SpaceControllerFactory extends ControllerFactory {

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.common.controllers.ControllerFactory#create(org.openmaji.implementation.tool.eclipse.browser.relationship.common.nodes.Node)
	 */
	public Controller create(Node node) {
		if (node instanceof DiagramNode) {
			return new DiagramController((DiagramNode) node);
		}
		else if (node instanceof CategoryNode) {
			return new CategoryController((CategoryNode) node);
		}
		else if (node instanceof MeemNode) {
			return new MeemController((MeemNode) node);
		}
		else if (node instanceof UnavailableMeemNode) {
			return new UnavailableMeemController((UnavailableMeemNode) node);
		}
		else {
			//logger.log(Level.INFO, "tried to create Controller for unknown node: " + node);
		}
		return null;
	}
}
