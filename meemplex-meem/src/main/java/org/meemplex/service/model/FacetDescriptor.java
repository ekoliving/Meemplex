package org.meemplex.service.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Information of a Facet
 * 
 * @author stormboy
 *
 */
@XmlRootElement(name="facet")
@XmlType(propOrder = {"path", "facetClass", "type", "direction", "dependencies"})
public class FacetDescriptor implements Serializable {
	
	private static final long serialVersionUID = 0L;

	/**
	 * Path within a Meem to the facet.
	 * Path is an alphanumeric string potentially including "/" to separate items..
	 * May be qualified by Wedge name, for example, "binary/in", "binaryWedge/out"
	 */
	private String path;

	/**
	 * The class of the Facet. This is the interface by which Facet Messages are passed.
	 */
	private String facetClass;

	/**
	 * This is the unit of measurement, assuming the Facet represents the flow of values.
	 * 
	 * TODO This should not be in the core API, but an extension.
	 */
//	private String unit;
	
	/**
	 * Is this a System or Application Facet.  Applicaiton Facets are generally viewable, whereas System
	 * Facets may not be.
	 */
	private FacetType type;
	
	/**
	 * Whether the flow of information is in or out.
	 */
	private Direction direction;

	/**
	 * A list of Dependencies this Facet has on other Facets.
	 */
	private List<FacetDependency> dependencies;
	

	public void setPath(String path) {
	    this.path = path;
    }

	public String getPath() {
	    return path;
    }

	public void setFacetClass(String facetClass) {
	    this.facetClass = facetClass;
    }

	public String getFacetClass() {
	    return facetClass;
    }

	public void setType(FacetType type) {
	    this.type = type;
    }

	public FacetType getType() {
	    return type;
    }

	public void setDirection(Direction direction) {
	    this.direction = direction;
    }

	public Direction getDirection() {
	    return direction;
    }

	public void setDependencies(List<FacetDependency> dependencies) {
	    this.dependencies = dependencies;
    }

	@XmlElement(name="dependency")
	public List<FacetDependency> getDependencies() {
	    return dependencies;
    }

}
