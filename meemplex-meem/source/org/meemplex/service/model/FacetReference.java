package org.meemplex.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Absolute reference to Facet
 * 
 * @author stormboy
 *
 */
@XmlRootElement(name="facetReference")
public class FacetReference implements Serializable {
	private static final long serialVersionUID = 0L;

	/**
	 * The path to the Meem that the Facet belongs to.
	 */
	private String meemPath;

	/**
	 * The path to the facet. This is a path to facet within the Meem.
	 * e.g. "pathto/myfacet" in the full path, "hyperspace:/site/mysite/mymeem:pathto/myfacet"
	 */
	private String facetPath;

	/**
	 * The class of the Facet. This is the interface by which Facet Messages are passed.
	 */
	private String facetClass;

	/**
	 * Direction, from the point-of-view of the Facet's Meem.
	 */
	private Direction direction;

	/**
	 * 
	 */
	public FacetReference() {
    }

	public FacetReference(String meemPath, String facetName) {
		setMeemPath(meemPath);
    }

	public void setMeemPath(String meemPath) {
	    this.meemPath = meemPath;
    }

	@XmlAttribute
	public String getMeemPath() {
	    return meemPath;
    }

	public void setFacetPath(String facetPath) {
	    this.facetPath = facetPath;
    }

	@XmlAttribute
	public String getFacetPath() {
	    return facetPath;
    }

	public void setFacetClass(String facetClass) {
	    this.facetClass = facetClass;
    }

	@XmlAttribute
	public String getFacetClass() {
	    return facetClass;
    }

	public void setDirection(Direction direction) {
	    this.direction = direction;
    }

	public Direction getDirection() {
	    return direction;
    }
}
