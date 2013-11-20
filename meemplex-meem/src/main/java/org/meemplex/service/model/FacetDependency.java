package org.meemplex.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

/**
 * Dependency data for a Facet
 * @author stormboy
 *
 */
@XmlType
public class FacetDependency  implements Serializable {
	
	private static final long serialVersionUID = 0L;

	private String meemPath;
	
	private String facetPath;
	
	private DependencyType type;

	public void setMeemPath(String meemPath) {
	    this.meemPath = meemPath;
    }

	public String getMeemPath() {
	    return meemPath;
    }

	public void setFacetPath(String facetPath) {
	    this.facetPath = facetPath;
    }

	public String getFacetPath() {
	    return facetPath;
    }

	public void setType(DependencyType type) {
	    this.type = type;
    }

	public DependencyType getType() {
	    return type;
    }
}
