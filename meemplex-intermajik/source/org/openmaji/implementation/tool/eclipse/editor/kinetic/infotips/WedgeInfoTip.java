/*
 * @(#)WedgeInfoTip.java
 * Created on 26/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips;

import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.implementation.tool.eclipse.images.Images;

/**
 * <code>WedgeInfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeInfoTip extends InfoTip {
	Wedge wedge;
	/**
	 * Constructs an instance of <code>WedgeInfoTip</code>.
	 * <p>
	 * 
	 */
	public WedgeInfoTip(Wedge wedge) {
		this.wedge = wedge;
		build(wedge);
	}
	
	private void build(Wedge wedge) {
		labelLayer.setIcon(Images.getIcon("wedge16.gif"));
		labelLayer.setText(wedge.getAttribute().getImplementationClassName());
	}
}
