/*
 * @(#)FacetSpecificationPair.java
 * Created on 6/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

/**
 * <code>FacetSpecificationPair</code> contains the specifications of a pair of 
 * facets.
 * <p>
 * @author Kin Wong
 * @see org.openmaji.implementation.tool.eclipse.client.FacetInboundSpecification
 * @see org.openmaji.implementation.tool.eclipse.client.FacetOutboundSpecification
 */
public class FacetSpecificationPair implements Cloneable {
	private FacetOutboundSpecification outboundSpec;
	private FacetInboundSpecification inboundSpec;

	/**
	 * Constructs an instance of <code>FacetSpecificationPair</code>.
	 * <p>
	 * @param inboundSpec The specification of the inbound facet.
	 * @param outboundSpec The specification of the outbound facet.
	 */
	public FacetSpecificationPair(FacetInboundSpecification inboundSpec, FacetOutboundSpecification outboundSpec) {
		this.inboundSpec = inboundSpec;
		this.outboundSpec = outboundSpec;
	}
	
	/**
	 * Constructs an instance of <code>FacetSpecificationPair</code>.
	 * <p>
	 * @param outboundSpec The specification of the outbound facet.
	 */
	public FacetSpecificationPair(FacetOutboundSpecification outboundSpec) {
		this.outboundSpec = outboundSpec;
	}
	
	/**
	 * Gets the specification of the inbound facet.
	 * <p>
	 * @return FacetInboundSpecification The specification of the inbound facet.
	 */
	public FacetInboundSpecification getInboundSpecification() {
		return inboundSpec;
	}
	
	/**
	 * Gets the specification of the outbound facet.
	 * <p>
	 * @return FacetOutboundSpecification The specification of the outbound facet.
	 */
	public FacetOutboundSpecification getOutboundSpecification() {
		return outboundSpec;
	}
	
	/**
	 * Gets whether this facet pair is read only.
	 * <p>
	 * @return boolean true if the facet pair is read only, false otherwise.
	 */
	public boolean isReadOnly() {
		return (inboundSpec == null);
	}
	
	/**
	 * Creates and returns a copy of this specification pair.  
	 * <p>
	 * @return Object A cloned specification pair.
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch(CloneNotSupportedException e) {
			// Should never get to here!
			return null;			
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof FacetSpecificationPair)) return false;
		FacetSpecificationPair that = (FacetSpecificationPair)obj;
		
		if(isReadOnly()) {
			if(!that.isReadOnly()) return false;
		}
		else
		if(!getInboundSpecification().equals(that.getInboundSpecification())) return false;
		return getOutboundSpecification().equals(that.getOutboundSpecification());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if(isReadOnly()) {
			return getOutboundSpecification().hashCode();
		}
		else {
			return 	getInboundSpecification().hashCode() +
							getOutboundSpecification().hashCode();
		}
	}
}
