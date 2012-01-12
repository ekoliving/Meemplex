/*
 * @(#)ControllerFactory.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.controllers;

import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;

/**
 * <code>ControllerFactory</code> defines the contract of a controller factory.
 * <p>
 * @author Kin Wong
 */
abstract public class ControllerFactory {
	/**
	 * Creates a controller from a given node.
	 * <p>
	 * @param node The node that connects to the controller.
	 * @return a controller that corresponds to the node, or null if no controller 
	 * is defined for the node in the context.
	 */
	abstract public Controller create(Node node);
}
