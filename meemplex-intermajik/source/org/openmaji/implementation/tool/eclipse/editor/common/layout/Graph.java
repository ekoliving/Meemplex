/*
 * @(#)Graph.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.layout;

import java.util.Vector;

/**
 * @author Peter
 */
public class Graph
{
	public void clear()
	{
		nodes.clear();
		edges.clear();
	}

	public void addNode(Node node)
	{
		nodes.add(node);
	}

	public void removeNode(Node node)
	{
		nodes.remove(node);
	}

	public void addEdge(Edge edge)
	{
		edges.add(edge);
	}

	public void removeEdge(Edge edge)
	{
		edges.remove(edge);
	}

	public Vector getNodes()
	{
		return nodes;
	}

	public Vector getEdges()
	{
		return edges;
	}

	private Vector nodes = new Vector();
	private Vector edges = new Vector();
}
