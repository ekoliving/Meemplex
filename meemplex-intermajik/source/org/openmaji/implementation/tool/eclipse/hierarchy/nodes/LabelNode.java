package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;


/*
 * @(#)LabelNode.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */


/**
 * <code>LabelNode</code>.
 * <p>
 * @author Kin Wong
 */
public class LabelNode extends Node {
	private static final long serialVersionUID = 6424227717462161145L;

	private String label;

	public LabelNode() {
	}

	/**
	 * Constructs an instance of <code>LabelNode</code>.
	 * <p>
	 * @param label
	 */
	public LabelNode(String label) {
		this.label = label;
	}
	
	public boolean isLabelDefined() {
		return (label != null);
	}

	/**
	 */
	public String getText() {
		if(label == null) return "";
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		refreshVisual();
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.browser.relationship.common.nodes.Node#getId()
	 */
	public Object getId() {
		return toString();
	}
}
