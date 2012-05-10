/*
 * @(#)Hexagon.java
 * Created on 26/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.common.shapes;


/**
 * <code>Hexagon</code> is a shape of hexagon.
 * @author Kin Wong
 */
public class Hexagon extends RegularPolygon {
	/**
	 * Constructs an instance of <code>Hexagon</code>.
	 * <p>
	 */
	public Hexagon() {
		super(6);
		setFill(true);
		setOutline(true);
	}
}
