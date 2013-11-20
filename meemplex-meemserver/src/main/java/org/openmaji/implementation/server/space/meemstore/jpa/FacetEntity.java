package org.openmaji.implementation.server.space.meemstore.jpa;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.meemplex.service.model.Direction;

/**
 * Persists details about a Facet
 * 
 * @author stormboy
 *
 */
@Entity(name="Facet")
public class FacetEntity {

	/**
	 * used to identify this Facet in the system. 
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The name of the Facet. This is unique per Wedge.
	 */
	private String name;

	/**
	 * The interface class for this facet
	 */
	private String facetClass;
	
	@Enumerated(EnumType.STRING)
	private Direction direction;

	/**
	 * The name of the field in the Wedge class.
	 */
	private String fieldName;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="wedge_id")
	private WedgeEntity wedge;

	/**
	 * The index of this facet in the Wedge. for ordering
	 */
	private Integer sortIndex;
	
	public void setId(Long id) {
	    this.id = id;
    }

	public Long getId() {
	    return id;
    }

	public void setFacetName(String facetName) {
	    this.name = facetName;
    }

	public String getName() {
	    return name;
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

	public void setFieldName(String fieldName) {
	    this.fieldName = fieldName;
    }

	public String getFieldName() {
	    return fieldName;
    }

	public void setWedge(WedgeEntity wedge) {
	    this.wedge = wedge;
    }

	public WedgeEntity getWedge() {
	    return wedge;
    }

	public void setSortIndex(Integer index) {
	    this.sortIndex = index;
    }

	public Integer getSortIndex() {
	    return sortIndex;
    }
	
}
