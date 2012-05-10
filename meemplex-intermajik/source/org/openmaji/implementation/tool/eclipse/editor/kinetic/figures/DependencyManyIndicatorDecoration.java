/*
 * @(#)DependencyManyIndicatorDecoration.java
 * Created on 10/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * <code>DependencyManyIndicatorDecoration</code> represents the decoration on 
 * the target of "Many" dependency connection.
 * <p>
 * @author Kin Wong
 */
public class DependencyManyIndicatorDecoration extends PolygonDecoration {
	
	/**
	 * Constructs a DependencyIndicatorDecoration. 
	 */
	public DependencyManyIndicatorDecoration(int offset){
		setTemplate(createTemplate(offset));
		setScale(0.35,0.25);
		setFill(false);
	}

	private PointList createTemplate(int offset) {
		PointList points = new PointList();
		points.addPoint(new Point(-offset, -40));
		points.addPoint(new Point(-offset, 40));
		return points;
	}
}
