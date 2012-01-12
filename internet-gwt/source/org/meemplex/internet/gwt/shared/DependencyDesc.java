package org.meemplex.internet.gwt.shared;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class DependencyDesc {

	/**
	 * The Facet depended upon
	 */
	private FacetReference dependee;

	/**
	 * The Facet that depends on another.
	 * 
	 * This may be null for an anonymous client that will handle and route incoming Facet events.
	 */
	private FacetReference dependant;

	public DependencyDesc() {
    }
	
	public DependencyDesc(FacetReference dependee) {
		setDependee(dependee);
    }
	
	public DependencyDesc(FacetReference dependee, FacetReference dependant) {
		setDependee(dependee);
		setDependant(dependant);
    }
	
	public void setDependee(FacetReference dependee) {
	    this.dependee = dependee;
    }

	public FacetReference getDependee() {
	    return dependee;
    }

	public void setDependant(FacetReference dependant) {
	    this.dependant = dependant;
    }

	public FacetReference getDependant() {
	    return dependant;
    }
}
