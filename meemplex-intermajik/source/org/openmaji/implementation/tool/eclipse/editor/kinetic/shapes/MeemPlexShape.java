/*
 * @(#)MeemPlexShape.java
 * Created on 18/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.shapes;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.implementation.tool.eclipse.editor.common.util.FastMath;
import org.openmaji.implementation.tool.eclipse.editor.common.util.PointsTransform;


/**
 * <code>MeemPlexShape</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPlexShape extends Shape {
	private int side = 6;
	private boolean innerOutline = true;
	private PointList pointsFill;
	private PointList[] pointsOutlines;
	private double rotation = 0.0;
	private double outerSizeRatio = 0.50;
//	private double innerSizeRatio = 0.75;

	/**
	 * Constructs an instance of <code>MeemPlexShape</code>.
	 * <p>
	 */
	public MeemPlexShape() {
	}
	/**
	 * Gets whether inner outline is drawn.
	 * @return boolean true if inner outline is drawn, false otherwise.
	 */
	public boolean getInnerOutline() {
		return innerOutline;
	}
	/**
	 * Sets whether inner outline is drawn.
	 * @param innerOutline true if inner outline is drawn, false otherwise.
	 */
	public void setInnerOutline(boolean innerOutline) {
		if(this.innerOutline == innerOutline) return;
		this.innerOutline = innerOutline;
		repaint();	
	}
	/**
	 * Overridden to precalculate all the internal point lists.
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	public void paintFigure(Graphics graphics) {
		preparePoints();
		super.paintFigure(graphics);
	}
	public void setRotation(double rotation) {
		this.rotation = rotation;
		repaint();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#fillShape(org.eclipse.draw2d.Graphics)
	 */
	protected void fillShape(Graphics graphics) {
		graphics.pushState();
		graphics.translate(bounds.x, bounds.y);
		graphics.fillPolygon(pointsFill);
		graphics.popState();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Shape#outlineShape(org.eclipse.draw2d.Graphics)
	 */
	protected void outlineShape(Graphics graphics) {
		graphics.pushState();
		graphics.translate(bounds.x, bounds.y);
		if(innerOutline) {
			int width = graphics.getLineWidth();
			graphics.setLineWidth(0);
			for(int hexagon = 0; hexagon < pointsOutlines.length; hexagon++)
			graphics.drawPolygon(pointsOutlines[hexagon]);
			graphics.setLineWidth(width);
		}
		graphics.drawPolygon(pointsFill);
		graphics.popState();
	}
	private void preparePoints() {
		Rectangle r = getBounds();
		double size = Math.min(r.height, r.width);
		double outerSize = size * outerSizeRatio;
//		double innerSize = size * innerSizeRatio;
		
//		double radius = ((double)r.width) / 2.0;
		double radiusInner = ((double)(outerSize)) / 2.0;
		double radiusMid = ((double)(size + outerSize)) / 4.0;
		double radiusOuter = ((double)(size)) / 2.0;

		double cosStart = FastMath.cos(0.0);
		double sinStart = FastMath.sin(0.0);
		
		pointsFill = new PointList(18);
		pointsOutlines = new PointList[side];
		for(int hexagon = 1; hexagon <= side; hexagon++) {
			PointList points = new PointList();
			pointsOutlines[hexagon - 1] = points;
			double angleEnd = ((double)hexagon) * (2.0 * Math.PI / side);
			double cosEnd = FastMath.cos(angleEnd); 
			double sinEnd = FastMath.sin(angleEnd); 
			
			double xInnerStart = cosStart * radiusInner;
			double yInnerStart = sinStart * radiusInner;
			double xInnerEnd = cosEnd * radiusInner;
			double yInnerEnd = sinEnd * radiusInner;
			
			// Add Inner Edge
			points.addPoint((int)xInnerStart, (int)yInnerStart);
			points.addPoint((int)xInnerEnd, (int)yInnerEnd);

			// Work out the equation parameters of inner edge
			double gradient = (yInnerStart - yInnerEnd)/(xInnerStart - xInnerEnd);
//			double cInner = yInnerEnd - gradient * xInnerEnd;
			
			// Mid point 1
			points.addPoint((int)(cosEnd * radiusMid),
							(int)(sinEnd * radiusMid));
			// Add Outer Edges
			double xStart = cosStart * radiusOuter;
			double yStart = sinStart * radiusOuter;
			double cOuter = yStart - gradient * xStart; //c = y - mx
			if(Math.abs(gradient) > 0.00001) { 
				double gradientPP = -(1.0 / gradient);
				// End point
				double cEndPP = yInnerEnd - gradientPP * xInnerEnd; // c = y - mx
				double xOuterEnd = (cOuter - cEndPP)/(gradientPP - gradient);
				double yOuterEnd = gradient * xOuterEnd + cOuter;

				points.addPoint((int)xOuterEnd,
								(int)yOuterEnd); 
			
				double cStartPP = yInnerStart - gradientPP * xInnerStart; // c = y - mx
				double xOuterStart = (cOuter - cStartPP)/(gradientPP - gradient);
				double yOuterStart = gradient * xOuterStart + cOuter;
				points.addPoint((int)xOuterStart,
								(int)yOuterStart);
			}
			else {
				// End point
				double xOuterEnd = xInnerEnd;
				double yOuterEnd = gradient * xOuterEnd + cOuter;
				points.addPoint((int)xOuterEnd,
								(int)yOuterEnd);
			
				double xOuterStart = xInnerStart;
				double yOuterStart = gradient * xOuterStart + cOuter;
				points.addPoint((int)xOuterStart,
								(int)yOuterStart);
			}
			// Mid point 2
			points.addPoint((int)(cosStart * radiusMid),
							(int)(sinStart * radiusMid));
			
			pointsFill.addPoint(points.getPoint(5));
			pointsFill.addPoint(points.getPoint(4));
			pointsFill.addPoint(points.getPoint(3));

			// Next hexagon
			cosStart = cosEnd;
			sinStart = sinEnd; 
		}
		PointsTransform transform = new PointsTransform();
		transform.setTranslation(	((double)r.width) / 2.0, 
									((double)r.height) / 2.0);
		transform.setRotation(rotation);
		pointsFill = transform.transform(pointsFill);
		for(int o = 0; o < pointsOutlines.length; o++)
		pointsOutlines[o] = transform.transform(pointsOutlines[o]);
	}
}
