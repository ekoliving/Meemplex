package org.openmaji.implementation.server.space.meemstore.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.meemplex.meem.PropertyType;

@Entity(name="Content")
@NamedQueries({
	@NamedQuery(name="Content.selectIds", query="SELECT DISTINCT c.meemId FROM Content c"),
	@NamedQuery(name="Content.selectForMeem",
			query="SELECT c FROM Content c WHERE c.meemId=?1"
		),
	@NamedQuery(name="Content.selectForWedge",
		query="SELECT c FROM Content c WHERE c.meemId=?1 AND c.wedgeName=?2"
	)
})
@IdClass(ContentPK.class)
public class ContentEntity {
	
	@Id
	@Column(name="meem_id")
	private String meemId;
	
	@Id
	@Column(name="wedge_name")
	private String wedgeName;
	
	/**
	 * Name of the persited field in the Wedge.
	 */
	@Id
	private String name;
	
	/**
	 * The type of persisted field
	 */
	@Enumerated(EnumType.STRING)
	private PropertyType type;
	
	/**
	 * The stored object
	 */
	@Lob
	private Serializable value;


	public void setMeemId(String meemId) {
	    this.meemId = meemId;
    }

	public String getMeemId() {
	    return meemId;
    }

	public void setWedgeName(String wedgeName) {
	    this.wedgeName = wedgeName;
    }

	public String getWedgeName() {
	    return wedgeName;
    }

	public void setName(String name) {
	    this.name = name;
    }

	public String getName() {
	    return name;
    }

	public void setType(PropertyType type) {
	    this.type = type;
    }

	public PropertyType getType() {
	    return type;
    }

	public void setValue(Serializable object) {
	    this.value = object;
    }

	public Serializable getValue() {
	    return value;
    }
}
