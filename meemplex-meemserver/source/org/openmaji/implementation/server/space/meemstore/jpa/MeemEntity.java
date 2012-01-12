package org.openmaji.implementation.server.space.meemstore.jpa;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.openmaji.meem.definition.MeemDefinition;

@Entity(name="Meem")
@NamedQueries( {
	@NamedQuery(name="Meem.selectIds", query="SELECT DISTINCT m.id FROM Meem m")
})
public class MeemEntity {

	/**
	 * The UUID of the meem
	 */
	@Id
	@Column(name="meem_id")
	private String id;

	/**
	 * The name of the Meem
	 */
	@Column(nullable=true)
	private String name = "";

	/**
	 * A decription of the Meem
	 */
	@Column(nullable=true)
	private String description = "";

	@OneToMany(mappedBy="meem", cascade=CascadeType.ALL, orphanRemoval=true)
	@MapKey(name="name")
	@OrderBy("sortIndex")
	private Map<String, WedgeEntity> wedges = new LinkedHashMap<String, WedgeEntity>();

	@Version
	private int version;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_created")
	private Date dateCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="date_modified")
	private Date dateModified;

	@Column(name="meem_key")
	private String key;
	
	//@Enumerated(EnumType.STRING)
	private String scope;
	
	
	@Lob
	private MeemDefinition definition;
	
	
	
	public MeemEntity() {
    }
	
	public void setId(String id) {
	    this.id = id;
    }

	public String getId() {
	    return id;
    }

	public void setName(String name) {
	    this.name = name;
    }

	public String getName() {
	    return name;
    }

	public void setDescription(String description) {
	    this.description = description;
    }

	public String getDescription() {
	    return description;
    }

	public void setWedges(Map<String, WedgeEntity> wedges) {
	    this.wedges = wedges;
    }

	public Map<String, WedgeEntity> getWedges() {
	    return wedges;
    }
	
	public WedgeEntity getWedge(String name) {
		if (getWedges() != null) {
			return getWedges().get(name);
		}
		return null;
	}

	public void setDefinition(MeemDefinition definition) {
	    this.definition = definition;
    }

	public MeemDefinition getDefinition() {
	    return definition;
    }
	
//	public void setDefinitionObject(MeemDefinition meemDefinition) {
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(bos);
//			oos.writeObject(definition);
//			oos.close();
//			setDefinition(bos.toByteArray());						
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public MeemDefinition getDefinitionObject() {
//		MeemDefinition meemDefinition = null;
//		try {
//			InputStream is = new ByteInputStream(getDefinition(), 0);
//			ObjectInputStream ois = new ObjectInputStream(is);
//			meemDefinition = (MeemDefinition) ois.readObject();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return meemDefinition;
//	}

	public void setVersion(int version) {
	    this.version = version;
    }

	public int getVersion() {
	    return version;
    }

	public void setDateCreated(Date dateCreated) {
	    this.dateCreated = dateCreated;
    }

	public Date getDateCreated() {
	    return dateCreated;
    }

	public void setDateModified(Date dateModified) {
	    this.dateModified = dateModified;
    }

	public Date getDateModified() {
	    return dateModified;
    }
	
	@PrePersist
	@PreUpdate
	protected void update() {
		if (getDateCreated() == null) {
			setDateCreated(new Date());
		}
		setDateModified(new Date());
	}

	public void setKey(String key) {
	    this.key = key;
    }

	public String getKey() {
	    return key;
    }

	public void setScope(String scope) {
	    this.scope = scope;
    }

	public String getScope() {
	    return scope;
    }
}
