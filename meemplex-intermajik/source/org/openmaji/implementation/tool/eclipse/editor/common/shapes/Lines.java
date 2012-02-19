/*
 * @(#)Lines.java
 * Created on 19/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * @author Kin Wong
 * <P> Lines paints all the points in pair as line fragment. </P>
 */
public class Lines extends Polyline {
	/**
	 * Adds a new line to this lines.
	 * @param start The start of the line.
	 * @param end The end of the line.
	 */
	public void addLine(Point start, Point end) {
		addLine(start.x, start.y, end.x, end.y);
	}
	/**
	 * Adds a new line to this lines.
	 * @param x1 The x-coordinate of the start of the line.
	 * @param y1 The y-coordinate of the start of the line.
	 * @param x2 The x-coordinate of the end of the line.
	 * @param y2 The y-coordinate of the end of the line.
	 */
	public void addLine(int x1, int y1, int x2, int y2) {
		getPoints().addPoint(x1, y1);
		getPoints().addPoint(x2, y2);
		bounds = null;
		repaint();
	}
	
	/**
	 * Overridden to do nothing as there is impossible to paint filled line 
	 * fragment.
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics) {
	}
	
	/**
	 * Overridden to paint lines.
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		PointList points = getPoints();
		int size = ((points.size() >> 1) << 1);
		for(int i = 0; i < size; ) {
			Point pt1 = points.getPoint(i++);
			Point pt2 = points.getPoint(i++);
			graphics.drawLine(pt1, pt2); // draw each line
		}
	}
}
