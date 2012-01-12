package org.openmaji.implementation.tool.eclipse.editor.common.shapes;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EllipseDecoration extends Ellipse implements RotatableDecoration {

	/**
	 * @see org.eclipse.draw2d.RotatableDecoration#setReferencePoint(org.eclipse.draw2d.geometry.Point)
	 */
	/**
	 * @see org.eclipse.draw2d.RotatableDecoration#setLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public void setLocation(Point p) {
		if (getLocation().equals(p)) return;
		Dimension size = getBounds().getSize();
		setBounds(new Rectangle(p.x - (size.width >> 1), p.y - (size.height >> 1), size.width, size.height));
	}

	public void setReferencePoint(Point p) {
	}
}
