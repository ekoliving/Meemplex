package org.meemplex.internet.gwt.shared;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class FacetDetails implements Serializable {
	private static final long serialVersionUID = 0L;
	
	private String facetName;
	
	private String facetClass;
	
	private Direction direction;
	
	
	public FacetDetails() {
    }
	
	public FacetDetails(String facetName, String facetClass, Direction direction) {
		setFacetName(facetName);
		setFacetClass(facetClass);
		setDirection(direction);
    }

	public void setFacetName(String facetName) {
	    this.facetName = facetName;
    }


	public String getFacetName() {
	    return facetName;
    }


	public void setFacetClass(String facetClass) {
	    this.facetClass = facetClass;
    }


	public String getFacetClass() {
	    return facetClass;
    }


	public void setDirection(Direction direction) {
	    this.direction = direction;
    }


	public Direction getDirection() {
	    return direction;
    }
	
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj != null && obj instanceof FacetDetails) {
			FacetDetails other = (FacetDetails) obj;
			equal = 
				( this.facetName == null && other.facetName == null || this.facetName.equals(other.facetName) ) &&
				( this.facetClass == null && other.facetClass == null || this.facetClass.equals(other.facetClass) ) &&
				( this.direction == null && other.direction == null || this.direction.equals(other.direction) );
		}
		return equal;
	}
	
	public int hashCode() {
		int code = 0;
		if (facetName != null) {
			code ^= facetName.hashCode();
		}
		if (facetClass != null) {
			code ^= facetClass.hashCode();
		}
		if (direction != null) {
			code ^= direction.hashCode();
		}
		return code;
	}
}
