package org.openmaji.implementation.tool.eclipse.editor.common.model;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author Kin Wong
 */
public class ConnectionBendpoint implements java.io.Serializable, Bendpoint {
	
	private static final long serialVersionUID = 6424227717462161145L;

	private float weight = 0.5f;
	private Dimension d1, d2;
	
	public ConnectionBendpoint() {
	}
	
	public Dimension getFirstRelativeDimension() {
		return d1;
	}
	
	public Point getLocation() {
		return null;
	}
	
	public Dimension getSecondRelativeDimension() {
		return d2;
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
		d1 = dim1;
		d2 = dim2;
	}

	public void setWeight(float w) {
		weight = w;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof ConnectionBendpoint)) return false;
		
		ConnectionBendpoint that = (ConnectionBendpoint)obj;
		if(weight != that.weight) return false;
		return (d1.equals(that.d1) && d2.equals(that.d2));
	}

}