package org.meemplex.service.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.meemplex.meem.InvocationFilter;

/**
 * A definition of a Dependency between Meem Facets.
 * 
 * @author stormboy
 *
 */
@XmlRootElement(name="dependency")
public class DependencyDefinition implements Serializable {
	
	private static final long serialVersionUID = 0L;

	/**
	 * The facet that is dependent on another Facet.
	 */
	public FacetReference dependantFacet;

	/**
	 * The Facet that is depended upon.
	 */
	public FacetReference dependeeFacet;

	/**
	 * The type of dependency.  Strong, Weak.
	 */
	public DependencyType type;

	/**
	 * Whether the outbound facet should send content once dependency is
	 * connected and active.
	 * 
	 * 
	 */
//	private boolean initialContent;
		
	/**
	 * Filter
	 */
	public InvocationFilter filter;
	
	public static enum Direction {
		FORWARD,
		REVERSE
	}
	
	public static enum LifeSpan {
		TRANSIENT,
		ONGOING
	}
}