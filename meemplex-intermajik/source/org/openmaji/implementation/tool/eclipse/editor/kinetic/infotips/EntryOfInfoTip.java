/*
 * @(#)EntryOfInfoTip.java
 * Created on 26/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips;


import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.images.Images;

/**
 * <code>EntryOfInfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class EntryOfInfoTip extends InfoTip {
	//private EntryOf entryOf;
	
	/**
	 * Constructs an instance of <code>EntryOfInfoTip</code>.<p>
	 * @param entryOf
	 */
	public EntryOfInfoTip(EntryOf entryOf) {
		//this.entryOf = entryOf;
		build(entryOf);
	}
	
	private void build(EntryOf entryOf) {
			setIcon(Images.getIcon("entryof16.gif"));
			setCaption(entryOf.getName());
	}
}
