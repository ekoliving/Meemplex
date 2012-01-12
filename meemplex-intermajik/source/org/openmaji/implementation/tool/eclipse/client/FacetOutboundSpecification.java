/*
 * @(#)FacetOutboundSpecification.java
 * Created on 6/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.definition.Direction;

/**
 * <code>FacetOutboundSpecification</code> represents the specification of an 
 * outbound facet used in <code>FacetProxy</code>.<p> 
 * @author Kin Wong
 */
public class FacetOutboundSpecification extends FacetSpecification {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>FacetOutboundSpecification</code>.
	 * <p>
	 * @param type The type of the facet.
	 * @param identifier The identifier of the facet.
	 */
	public FacetOutboundSpecification(Class type, String identifier) {
		super(type, identifier);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetSpecification#getDirection()
	 */
	public Direction getDirection() {
		return Direction.OUTBOUND;
	}
}
