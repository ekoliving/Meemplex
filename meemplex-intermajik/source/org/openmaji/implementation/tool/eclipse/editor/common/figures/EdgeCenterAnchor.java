package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Kin Wong
 *
 * EdgeCenterAnchor provides anchor point at the center of an edge of the owner
 * figure that is closest to the reference point.
 */
public class EdgeCenterAnchor extends AbstractConnectionAnchor {
	/**
	 * Constructs an instance of EdgeCenterAnchor.
	 * @see java.lang.Object#Object()
	 */
	public EdgeCenterAnchor() {
	}
	/**
	 * Constructs an instance of EdgeCenterAnchor with owner.
	 * @see java.lang.Object#Object()
	 */
	public EdgeCenterAnchor(IFigure owner) {
		super(owner);
	}
	/**
	 * Overridden to return a point at the center of an edge of the owner
	 * figure that is closest to the reference point.
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle bounds = Rectangle.SINGLETON;
		bounds.setBounds(getOwner().getBounds());
		bounds.translate(-1, -1);
		bounds.resize(1, 1);
		getOwner().translateToAbsolute(bounds);
		int center = bounds.x + (bounds.width >> 1);
		int middle = bounds.y + (bounds.height >> 1);
		
		Point[] points = new Point[4];
		points[0]= new Point(bounds.x, middle);			// left
		points[1]= new Point(bounds.right(), middle);	// right
		points[2]= new Point(center, bounds.y);			// top
		points[3]= new Point(center, bounds.bottom());	// bottom
		Point point = points[0];
		double minDistance = Double.MAX_VALUE;
		for(int i = 0; i < 4; i++) {
			Point p = points[i];
			double distance = Math.pow((double)(p.x - reference.x), 2.0) + Math.pow((double)(p.y - reference.y), 2.0);
			if(distance < minDistance) {
				point = p;
				minDistance = distance;
			}
		}
		return point;
	}
}
