/*
 * @(#)FacetSpecification.java
 * Created on 6/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;

import org.openmaji.meem.definition.Direction;


/**
 * <code>FacetSpecification</code> represents the abstract specification of a 
 * facet used in <code>FacetProxy</code>.<p> 
 * @author Kin Wong
 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy
 * @see org.openmaji.implementation.tool.eclipse.client.FacetInboundSpecification
 * @see org.openmaji.implementation.tool.eclipse.client.FacetOutboundSpecification
 * @see org.openmaji.implementation.tool.eclipse.client.MeemClientProxy
 */
abstract public class FacetSpecification implements Serializable, Cloneable {
	private Class type; // The type of the facet
	private String identifier; // The identifier of the facet

	/**
	 * Constructs an instance of <code>FacetSpecification</code>.<p>
	 * @param type The type of the facet.
	 * @param identifier The identifier of the facet.
	 */	
	protected FacetSpecification(Class type, String identifier) {
		this.type = type;
		this.identifier = identifier;
	}
	
	/**
	 * Gets the type of the facet.<p>
	 * @return Class The type of the facet.
	 */
	public Class getType() {
		return type;		
	}
	
	/**
	 * Gets the identifier of the facet.<p>
	 * @return String The identifier of the facet.
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Gets the direction of the facet.<p>
	 * @return Direction The direction of the facet.
	 */
	abstract public Direction getDirection();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch(CloneNotSupportedException e) {
			// Should never get to here.
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof FacetSpecification)) return false;
		
		FacetSpecification that = (FacetSpecification)obj;
		if(!getDirection().equals(that)) return false;
		
		if(!getType().equals(that.getType())) return false;
		return getIdentifier().equals(that.getIdentifier());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 	getDirection().hashCode() + 
						getType().hashCode() + 
						getIdentifier().hashCode();
	}
}
