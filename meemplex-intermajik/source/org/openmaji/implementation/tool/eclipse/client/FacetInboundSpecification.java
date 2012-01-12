/*
 * @(#)FacetInboundSpecification.java
 * Created on 6/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.meem.definition.Direction;

/**
 * <code>FacetInboundSpecification</code> represents the specification of an 
 * inbound facet used in <code>FacetProxy</code>.<p> 
 * @author Kin Wong
 */
public class FacetInboundSpecification extends FacetSpecification {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>FacetInboundSpecification</code>.
	 * <p>
	 * @param type The type of the facet.
	 * @param identifier The identifier of the facet.
	 */
	public FacetInboundSpecification(Class type, String identifier) {
		super(type, identifier);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetSpecification#getDirection()
	 */
	public Direction getDirection() {
		return Direction.INBOUND;
	}
}
