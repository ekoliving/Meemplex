/*
 * @(#)FacetDescriptor.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.filter;

import java.io.Serializable;


/**
 * @author Peter
 */
public class FacetDescriptor implements Filter, Serializable
{
	private static final long serialVersionUID = -1178365040590887015L;

	public FacetDescriptor(String facetIdentifier, Class specification)
	{
		this.facetIdentifier = facetIdentifier;
		this.specification = specification;
	}

	public final String facetIdentifier;
	public final Class<?> specification;
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hashCode = facetIdentifier.hashCode();
		if (specification != null) hashCode ^= specification.getName().hashCode();

		return hashCode;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == this) return(true);

    if ((object instanceof FacetDescriptor) == false) return(false);

    FacetDescriptor thatFilter = (FacetDescriptor) object;

    if (facetIdentifier.equals(thatFilter.facetIdentifier) == false) {
      return(false);
    }

    if (specification != null) {
      if (specification.equals(thatFilter.specification) == false) return(false);
    }
    
    return true;
	}
	
	public String toString() {
		return(
	      getClass().getName() + "[" +
	        "facetIdentifier=" + facetIdentifier +
	        ", specification=" + specification +
	      "]"
	    );
	}
}
