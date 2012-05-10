/*
 * @(#)RegularPolygon.java
 * Created on 26/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 
package org.openmaji.implementation.tool.eclipse.editor.common.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.implementation.tool.eclipse.editor.common.util.FastMath;


/**
 * <code>RegularPolygon</code> represents a polygon with equal internal angles.
 * <p>
 * @author Kin Wong
 */
public class RegularPolygon extends Shape 
	implements RotatableDecoration {
	static private final int DEFAULT_SIDE = 6;
	private int side;

	/**
	 * Constructs an instance of RegularPolygon.
	 */
	public RegularPolygon() {
		this(DEFAULT_SIDE);
	}
	
	/**
	 * Constructs an instance of RegularPolygon with the specified number of 
	 * side.
	 * @param side Number of sides.
	 */
	public RegularPolygon(int side) {
		this.side = side;
	}
	/**
	 * Gets the number of sides of this regular polygon.
	 * @return int The number of sides of this regulat polygon.
	 */
	public int getSide() {
		return side;
	}
	
	/**
	 * Sets the number of sides of this regular polygon.
	 * @param side The number of sides of this regular polygon to set.
	 */
	public void setSide(int side) {
		this.side = side;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		graphics.fillPolygon(createPoints());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		graphics.drawPolygon(createPoints());
	}
	
	/**
	 * Ceates the point list for rendering of this regular polygon.
	 * @return PointList The point list that renders this regular polygon.
	 */
	protected PointList createPoints() {
		PointList points = new PointList(side);
		
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBounds());
		r.shrink(lineWidth,lineWidth);
		
		int offsetX = (r.width >> 1);
		int offsetY = (r.height >> 1);
		double radiusX = ((double)r.width) / 2.0;
		double radiusY = ((double)r.height) / 2.0;
		offsetX += r.x;
		offsetY += r.y;
		for(int n = 0; n < side; n++) {
			double angle = ((double)n) * (2.0 * Math.PI / side);
			int x = (int)(FastMath.cos(angle) * radiusX);
			int y = (int)(FastMath.sin(angle) * radiusY);
			points.addPoint(offsetX + x, offsetY + y);
		}
		return points;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.RotatableDecoration#setReferencePoint(org.eclipse.draw2d.geometry.Point)
	 */
	public void setReferencePoint(Point p) {
	}
}
