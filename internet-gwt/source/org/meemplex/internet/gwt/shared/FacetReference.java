package org.meemplex.internet.gwt.shared;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

/**
 * Reference to a Facet.
 * 
 * @author stormboy
 *
 */
@XmlType
public class FacetReference implements Serializable {
	
	private static final long serialVersionUID = 0L;

	/**
	 * The path to the meem that the Facet belongs to.
	 * 
	 * A URI representation of a MeemPath
	 * 
	 * examples of valid locations:
	 * 		hyperSpace:/cat1/cat2/MyMeem
	 * 		meemStore:uuid
	 * 		transient:uuid
	 */
	private String meemPath;

	/**
	 * The id, or name, of the Facet on the Meem.
	 */
	private String facetId;

	/**
	 * The type of Facet. This is only required when using the FacetReference to make
	 * a Dependency on a Facet.
	 */
	private String facetClass;
	
	/**
	 * Direction of the Facet, from the point-of-view of the Facet's Meem.
	 */
	private Direction direction;


	/**
	 * Default constructor
	 */
	public FacetReference() {
    }
	
	public FacetReference(String meemPath, String facetId, String facetClass) {
		setMeemPath(meemPath);
		setFacetId(facetId);
		setFacetClass(facetClass);
    }
	
	public void setMeemPath(String meemPath) {
	    this.meemPath = meemPath;
    }

	public String getMeemPath() {
	    return meemPath;
    }

	public void setFacetId(String facetId) {
	    this.facetId = facetId;
    }

	public String getFacetId() {
	    return facetId;
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

	/**
	 * Returns whether the given FacetReference has the same Facet path.
	 * The path consists of the MeemPath and Facet name;
	 * 
	 * @param other
	 * @return
	 */
	public boolean sameFacetPath(FacetReference other) {
		return
			( this.meemPath == null && other.meemPath == null || this.meemPath != null && this.meemPath.equals(other.meemPath) ) &&
			( this.facetId == null && other.facetId == null || this.facetId != null && this.facetId.equals(other.facetId) );
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equal = false;
		if (obj != null && obj instanceof FacetReference) {
			FacetReference other = (FacetReference) obj;
			equal = 
				( this.meemPath == null && other.meemPath == null     || this.meemPath != null && this.meemPath.equals(other.meemPath)        ) &&
				( this.facetId == null && other.facetId == null       || this.facetId != null && this.facetId.equals(other.facetId)           ) &&
				( this.facetClass == null && other.facetClass == null || this.facetClass != null &&  this.facetClass.equals(other.facetClass) ) &&
				( this.direction == null && other.direction == null   || this.direction != null &&  this.direction.equals(other.direction)    );
		}
		return equal;
	}
	
	@Override
	public int hashCode() {
		int hash = 481037;
		if (meemPath != null) {
			hash ^= meemPath.hashCode();
		}
		if (facetId != null) {
			hash ^= facetId.hashCode();
		}
		if (facetClass != null) {
			hash ^= facetClass.hashCode();
		}
		if (direction != null) {
			hash ^= direction.hashCode();
		}
		return hash;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Facet: ");
		sb.append(getMeemPath());
		sb.append("|");
		sb.append(getFacetId());
		sb.append("]");
		
	    return sb.toString();
	}

}
