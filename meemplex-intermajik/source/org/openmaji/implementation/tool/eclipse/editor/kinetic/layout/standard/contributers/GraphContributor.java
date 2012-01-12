/*
 * @(#)GraphContributor.java
 * Created on 9/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.layout.standard.contributers;

import org.eclipse.draw2d.graph.DirectedGraph;

/**
 * <code>GraphContributor</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class GraphContributor {
	abstract public void contributeNodes(DirectedGraph graph);
	abstract public void contributeEdges(DirectedGraph graph);
}

