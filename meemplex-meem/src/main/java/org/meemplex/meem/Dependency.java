package org.meemplex.meem;

import java.util.UUID;

import org.meemplex.service.model.DependencyDefinition;
import org.meemplex.service.model.DependencyType;

/**
 * A dependency that can be added to a dependant Meem Facet. This object describes a Meem Facet that
 * is depended on.
 * 
 * A connection is set-up by the DependencyManager between the dependee and dependant Facets
 * 
 * @author stormboy
 *
 */
public class Dependency {

	/**
	 * The id of the meem to depend on.
	 */
	private UUID meemId;

	/**
	 * The path of the Facet to depend on.
	 */
	private String facetPath;

	/**
	 * The type of Dependency
	 */
	private DependencyType type;

	/**
	 * Direction of the Dependency
	 */
	private DependencyDefinition.Direction direction;

	/**
	 * Filter to use to determine which messages are sent accross the dependent connection.
	 */
	private InvocationFilter filter;

	public void setMeemId(UUID meemId) {
	    this.meemId = meemId;
    }

	public UUID getMeemId() {
	    return meemId;
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

	public void setDirection(DependencyDefinition.Direction direction) {
	    this.direction = direction;
    }

	public DependencyDefinition.Direction getDirection() {
	    return direction;
    }

	public void setFilter(InvocationFilter filter) {
	    this.filter = filter;
    }

	public InvocationFilter getFilter() {
	    return filter;
    }
}
