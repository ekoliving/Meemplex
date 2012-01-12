package org.openmaji.implementation.server.space.meemstore.jpa;

public class ContentPK {

	/**
	 * Identifier of the Meem
	 */
	private String meemId;
	
	/**
	 * Name of the Wedge, unique within the Meem.
	 */
	private String wedgeName;
	
	/**
	 * Name of the persisted field in the Wedge.
	 * This is unique per Wedge
	 */
	private String name;

	
	public ContentPK() {
    }
	
	public ContentPK (String meemId, String wedgeName, String name) {
		setMeemId(meemId);
		setWedgeName(wedgeName);
		setName(name);
	}
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof ContentPK)) {
			return false;
		}
		ContentPK pk = (ContentPK) obj;
		
		return 
			(meemId == null ? pk.meemId == null : meemId.equals(pk.meemId)) 
		&&
			(wedgeName == null ? pk.wedgeName == null : wedgeName.equals(pk.wedgeName))
		&&
			(name == null ? pk.name == null : name.equals(pk.meemId));
	}
	
	@Override
	public int hashCode() {
		int hash = 59829857;
		hash ^= meemId == null ? 0 : meemId.hashCode();
		hash ^= wedgeName == null ? 0 : wedgeName.hashCode();
		hash ^= name == null ? 0 : name.hashCode();
		
	    return hash;
	}
}
