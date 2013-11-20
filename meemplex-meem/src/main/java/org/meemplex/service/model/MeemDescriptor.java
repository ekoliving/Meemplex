package org.meemplex.service.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Describes the structure of a Meem instance.
 * 
 * @author stormboy
 *
 */
@XmlRootElement(name="meem")
@XmlType(propOrder = {"id", "name", "description", "type", "facets", "properties"})
public class MeemDescriptor extends Identifiable {
	
	/**
	 * A name given to the Meem
	 */
	private String name;

	/**
	 * A description given to the Meem.
	 */
	private String description;

	/**
	 * Type of Meem.
	 * 
	 * This is a path to a Meem in the Meem Toolkit
	 * toolkit:/automation/lights/dimmable
	 */
	private String type;
	
	/**
	 * Facets of the Meem
	 */
	private List<FacetDescriptor> facets;

	/**
	 * Configuration properties
	 */
	private List<PropertyDesc> properties;
	
	public void setName(String name) {
	    this.name = name;
    }

	@XmlAttribute
	public String getName() {
	    return name;
    }

	public void setDescription(String description) {
	    this.description = description;
    }

	public String getDescription() {
	    return description;
    }

	public void setFacets(List<FacetDescriptor> facets) {
	    this.facets = facets;
    }

	@XmlElement(name="facet")
	public List<FacetDescriptor> getFacets() {
	    return facets;
    }

	public void setProperties(List<PropertyDesc> properties) {
	    this.properties = properties;
    }

	@XmlElement(name="property")
	public List<PropertyDesc> getProperties() {
	    return properties;
    }

	public void setType(String type) {
	    this.type = type;
    }

	public String getType() {
	    return type;
    }
}
