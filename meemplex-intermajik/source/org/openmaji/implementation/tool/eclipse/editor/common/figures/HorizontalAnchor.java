package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Kin Wong
 * The HorizontalAnchor provides anchor point that can be on the left or the
 * right edge of the owner figure that is closer to the reference point.
 */

public class HorizontalAnchor extends AbstractConnectionAnchor {
	/**
	 * Constructs an instance of HorizontalAnchor.
	 * @see java.lang.Object#Object()
	 */
	public HorizontalAnchor() {

	}
	/**
	 * Constructs an instance of HorizontalAnchor with the owner figure.
	 * @see java.lang.Object#Object()
	 */
	public HorizontalAnchor(IFigure owner) {
		super(owner);
	}
	/**
	 * Determines the distance of the reference point from the left and right
	 * edge of the figure and returns the closer one.
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(Point)
	 */
	public Point getLocation(Point reference) {
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getOwner().getBounds());
		r.translate(-1, -1);
		r.resize(1, 1);
		getOwner().translateToAbsolute(r);
		int middle = r.y + (r.height >> 1);
		Point left = new Point(r.x, middle);
		Point right = new Point(r.right(), middle);
		
		double leftDistance =	(double) Math.pow((double)(left.x - reference.x), 2.0) + Math.pow((double)(left.y - reference.y), 2.0);
		double rightDistance =	(double) Math.pow((double)(right.x - reference.x), 2.0) + Math.pow((double)(right.y - reference.y), 2.0);
		return (leftDistance < rightDistance)? left:right;
	}
}
