/*
 * Created on 9/09/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openmaji.implementation.rpc.server;

/**
 * 
 * @author Warren Bloomer
 *
 */
public class FacetKey {
	private String  meemPath;
	private String  facetId;
	
	FacetKey(String meemPath, String  facetId) {
		this.meemPath = meemPath;
		this.facetId = facetId;
	}
	
	public boolean equals(Object object) {
		
		if ( object instanceof FacetKey) {
			FacetKey other = (FacetKey) object;

			boolean test1 = (meemPath == null) ? meemPath == other.meemPath : meemPath.equals(other.meemPath);
			boolean test2 = (facetId == null)  ? facetId == other.facetId   : facetId.equals(other.facetId);
			
			return test1 && test2;
		}
		return false;
	}

	public int hashCode() {
		int hash1 = (meemPath == null) ? 0 : meemPath.hashCode();
		int hash2 = (facetId  == null) ? 0 : facetId.hashCode();
		return hash1 ^ hash2;
	}
}
