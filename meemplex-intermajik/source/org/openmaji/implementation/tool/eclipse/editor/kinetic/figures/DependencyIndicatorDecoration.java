/*
 * @(#)DependencyIndicatorDecoration.java
 * Created on 6/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * <code>DependencyIndicatorDecoration</code> represents the decoration at the 
 * target of a dependency connection.
 * <p>
 * @author Kin Wong
 */
public class DependencyIndicatorDecoration extends PolygonDecoration {
	private static final PointList DEPENDENCY_INDICATOR = new PointList();
	static {
		final double radius = 40.0;
		final int pointTotal = 6;
		final double HALF_PI = -Math.PI / 2.0;
		final double SLICE = Math.PI;
		
		for(int p = 0; p <= pointTotal; p++) {
			double division = (double)p;
			double angle = HALF_PI + SLICE * division/(double)pointTotal;
			Point point = new Point(radius * Math.cos(angle) - radius, 
									radius * Math.sin(angle));
			DEPENDENCY_INDICATOR.addPoint(point);
		}
	}
	
	/**
	 * Constructs a <code>DependencyIndicatorDecoration</code>. 
	 */
	public DependencyIndicatorDecoration(){
		setTemplate(DEPENDENCY_INDICATOR);
		setScale(0.35,0.25);
		setFill(false);
	}
}
