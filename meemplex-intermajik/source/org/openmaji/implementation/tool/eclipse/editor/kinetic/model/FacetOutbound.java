/*
 * @(#)FacetOutbound.java
 * Created on 9/08/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.meem.definition.FacetOutboundAttribute;



/**
 * <code>FacetOutbound</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetOutbound extends Facet {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>FacetOutbound</code>.
	 * <p>
	 * @param attribute The facet attribute associates with this 
	 * outbound facet.
	 */
	public FacetOutbound(MeemClientProxy proxy, FacetOutboundAttribute attribute) {
		super(proxy, attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet#isInbound()
	 */
	public boolean isInbound() {
		return false;
	}
}
