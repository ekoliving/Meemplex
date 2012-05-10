/*
 * @(#)FacetInbound.java
 * Created on 9/08/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.model;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.meem.definition.FacetInboundAttribute;



/**
 * <code>FacetInbound</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetInbound extends Facet {
	private static final long serialVersionUID = 6424227717462161145L;

	/**
	 * Constructs an instance of <code>FacetInbound</code>.
	 * <p>
	 * @param attribute The facet attribute associates with this inbound
	 * facet.
	 */
	public FacetInbound(MeemClientProxy proxy, FacetInboundAttribute attribute) {
		super(proxy, attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet#isInbound()
	 */
	public boolean isInbound() {
		return true;
	}

}
