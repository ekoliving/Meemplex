/*
 * @(#)FacetInfoTip.java
 * Created on 26/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips;

import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.images.Images;

/**
 * <code>FacetInfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetInfoTip extends InfoTip {
	//private Facet facet;
	
	/**
	 * Constructs an instance of <code>FacetInfoTip</code>.
	 * <p>
	 * 
	 */
	public FacetInfoTip(Facet facet) {
		//this.facet = facet;
		build(facet);
	}
	
	private void build(Facet facet) {
		labelLayer.setIcon(Images.getIcon("facet16.gif"));
		labelLayer.setText(facet.getAttribute().getIdentifier() + " - " + facet.getAttribute().getInterfaceName());
	}
}
