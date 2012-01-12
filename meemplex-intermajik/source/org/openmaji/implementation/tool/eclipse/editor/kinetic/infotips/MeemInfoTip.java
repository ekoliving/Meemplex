/*
 * @(#)MeemInfoTip.java
 * Created on 26/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips;


import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.images.Images;

/**
 * <code>MeemInfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemInfoTip extends InfoTip {
	//private Meem meem;
	
	/**
	 * Constructs an instance of <code>MeemInfoTip</code>.
	 * <p>
	 */
	public MeemInfoTip(Meem meem) {
		//this.meem = meem;
		build(meem);
	}
	
	private void build(Meem meem) {
		if(meem instanceof Category) {
			setIcon(Images.getIcon("category16.gif"));
		}
		else {
			setIcon(Images.getIcon("meem16.gif"));
		}
		setCaption(meem.getName());
	}
}
