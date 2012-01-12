package org.openmaji.implementation.tool.eclipse.editor.common.model;

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
abstract public class BoundsObject extends Element {
	protected Point location = new Point(0, 0);
	protected Dimension size = new Dimension(-1, -1);

	public static final String ID_LOCATION = "location";
	public static final String ID_SIZE = "size";
	public static final String ID_VALIDATEFROMFIGURE = "validate from figure";

	/**
	 * Constructs an instance of a BoundsObject.
	 * @see java.lang.Object#Object()
	 */
	protected BoundsObject() {
	}
	
	/**
	 * Returns the location of the bound object.
	 * @return Point The location of the object.
	 */
	public Point getLocation() {
		return location;
	}
	
	/**
	 * Gets whether the object is resizeable.
	 * @return boolean true if the object is resizeable, false otherwise.
	 */
	public boolean isResizeable() {
		return false;
	}
		
	/**
	 * Returns the size of the object.
	 * @return Dimension
	 */
	public Dimension getSize() {
		return size;
	}

	/**
	 * Returns the bounds of the object.
	 * @return Rectangle The bounds of the object.
	 */
	public Rectangle getBounds() {
		return new Rectangle(location, size);
	}
	
	/**
	 * Sets the location of the object.
	 * @param location The location of the object.
	 */
	public void setLocation(Point location) {
		if (location.equals(this.location)) return;
		this.location = location;
		firePropertyChange(ID_LOCATION, null, location);
	}
	
	/**
	 * Sets the size of the object.
	 * @param size The new size of the object.
	 */
	public void setSize(Dimension size) {
		if (size.equals(this.size)) return;
		this.size = size;
		firePropertyChange(ID_SIZE, null, size);
	}
	
	/**
	 * Sets the bounds of the object.
	 * @param bounds The new bounds of the object.
	 */
	public void setBounds(Rectangle bounds) {
		setLocation(bounds.getLocation());
		setSize(bounds.getSize());
	}
	public boolean isPropertySet(){
		return true;	
	}
	
	/**
	 * Validates the size from the current figure.
	 */	
	public void validateFromFigure() {
		firePropertyChange(ID_VALIDATEFROMFIGURE, null, null);
	}
}
