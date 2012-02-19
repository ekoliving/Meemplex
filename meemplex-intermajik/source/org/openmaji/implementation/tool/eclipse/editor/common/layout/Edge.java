/*
 * @(#)Edge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.layout;

/**
 * @author Peter
 */
public class Edge
{
	public static final double DEFAULT_LENGTH = 50.0;
	public static final double DEFAULT_STRENGTH = 0.2;
	public static final double DEFAULT_MAGNETISM = 0.0;
	public static final Vector2D DEFAULT_ANCHOR = new Vector2D(0.0, 0.0);

	public Edge(Node sourceNode, Node targetNode)
	{
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.sourceAnchor = DEFAULT_ANCHOR;
		this.targetAnchor = DEFAULT_ANCHOR;
		this.length = DEFAULT_LENGTH;
		this.strength = DEFAULT_STRENGTH;
		this.magnetism = DEFAULT_MAGNETISM;
	}

	public Edge(
		Node sourceNode,
		Node targetNode,
		Vector2D sourceAnchor,
		Vector2D targetAnchor,
		double length,
		double strength,
		double magnetism)
	{
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.sourceAnchor = sourceAnchor;
		this.targetAnchor = targetAnchor;
		this.length = length;
		this.strength = strength;
		this.magnetism = magnetism;
	}

	protected Node sourceNode, targetNode;
	protected Vector2D sourceAnchor, targetAnchor;
	protected double length, strength, magnetism;
}
