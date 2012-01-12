/*
 * @(#)InfoTip.java
 * Created on 24/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.infotips;

import org.openmaji.implementation.tool.eclipse.images.Images;

/**
 * <code>InfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class InfoTip extends MessageTip {
	/**
	 * Constructs an instance of <code>InfoTip</code>.
	 * <p>
	 * @param text
	 */
	public InfoTip(String text) {
		super(text, Images.getIcon("popup_info16.gif"));
	}
	
	public InfoTip() {
		super(Images.getIcon("popup_info16.gif"));
	}
}
