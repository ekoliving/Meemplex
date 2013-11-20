package org.meemplex.meem;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
	private static final long serialVersionUID = 0L;
	
	private Address from;
	
	private Address to;
	
	private String method;
	
	private Serializable[] params;


	public void setFrom(Address from) {
	    this.from = from;
    }

	public Address getFrom() {
	    return from;
    }

	public void setTo(Address to) {
	    this.to = to;
    }

	public Address getTo() {
	    return to;
    }

	public void setMethod(String method) {
	    this.method = method;
    }

	public String getMethod() {
	    return method;
    }

	public void setParams(Serializable[] params) {
	    this.params = params;
    }

	public Serializable[] getParams() {
	    return params;
    }
	
	public class Address {
		private UUID meemId;
		
		private String facetPath;

		public Address() {
        }
		
		public Address(UUID meemId, String facetPath) {
			setMeemId(meemId);
			setFacetPath(facetPath);
        }
		
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
	}
}
