package org.openmaji.implementation.server.space.meemstore.jpa;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity(name="Wedge")
public class WedgeEntity {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="meem_id")
	private MeemEntity meem;

	/**
	 * Name of the Wedge
	 */
	private String name = "";

	/**
	 * The Wedge class
	 */
	private String classname = "";

	/**
	 * Whether the Wedge is a System Wedge or not.
	 */
	@Column(name="is_system")
	private boolean isSystemWedge = false;

	/**
	 * For ordering 
	 */
	private Integer sortIndex;
	
	@OneToMany(mappedBy="wedge", orphanRemoval=true, cascade=CascadeType.ALL)
	@OrderBy("sortIndex")
	private List<FacetEntity> facets;
	

	public void setId(Long id) {
	    this.id = id;
    }

	public Long getId() {
	    return id;
    }

	public void setMeem(MeemEntity meem) {
	    this.meem = meem;
    }

	public MeemEntity getMeem() {
	    return meem;
    }

	public void setName(String name) {
	    this.name = name;
    }

	public String getName() {
	    return name;
    }

	public void setClassname(String classname) {
	    this.classname = classname;
    }

	public String getClassname() {
	    return classname;
    }

	public void setSystemWedge(boolean isSystemWedge) {
	    this.isSystemWedge = isSystemWedge;
    }

	public boolean isSystemWedge() {
	    return isSystemWedge;
    }

	public void setSortIndex(Integer index) {
	    this.sortIndex = index;
    }

	public Integer getSortIndex() {
	    return sortIndex;
    }

	public void setFacets(List<FacetEntity> facets) {
	    this.facets = facets;
    }

	public List<FacetEntity> getFacets() {
	    return facets;
    }

}
