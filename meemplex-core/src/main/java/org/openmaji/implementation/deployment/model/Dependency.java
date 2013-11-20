package org.openmaji.implementation.deployment.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="dependency")
public class Dependency {
	
	@XmlAttribute(name="facet-id")
	private String facetId;
	
	@XmlAttribute(name="type")
	private String type;
	
	@XmlAttribute(name="scope")
	private String scope;
	
	@XmlAttribute(name="lifetime")
	private String lifetime;
	
	@XmlAttribute(name="content-required")
	private Boolean contentRequired;
	
	@XmlElement(name="other-meem")
	private FacetReference otherMeem;

	public void setFacetId(String facetId) {
	    this.facetId = facetId;
    }

	public String getFacetId() {
	    return facetId;
    }

	public void setType(String type) {
	    this.type = type;
    }

	public String getType() {
	    return type;
    }

	public void setScope(String scope) {
	    this.scope = scope;
    }

	public String getScope() {
	    return scope;
    }

	public void setLifetime(String lifetime) {
	    this.lifetime = lifetime;
    }

	public String getLifetime() {
	    return lifetime;
    }

	public void setContentRequired(Boolean contentRequired) {
	    this.contentRequired = contentRequired;
    }

	public Boolean getContentRequired() {
	    return contentRequired;
    }

	public void setOtherMeem(FacetReference otherMeem) {
	    this.otherMeem = otherMeem;
    }

	public FacetReference getOtherMeem() {
	    return otherMeem;
    }
}
