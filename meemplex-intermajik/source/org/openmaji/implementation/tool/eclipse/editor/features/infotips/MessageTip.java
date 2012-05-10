/*
 * @(#)MessageTip.java
 * Created on 24/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.infotips;

import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.ToolTip;


/**
 * <code>MessageTip</code>.
 * <p>
 * @author Kin Wong
 */
public class MessageTip extends ToolTip {

	/**
	 * Constructs an instance of <code>MessageTip</code>.
	 * <p>
	 * 
	 */
	public MessageTip() {
	}

	/**
	 * Constructs an instance of <code>MessageTip</code>.
	 * <p>
	 * @param i
	 */
	public MessageTip(Image i) {
		super(i);
	}

	/**
	 * Constructs an instance of <code>MessageTip</code>.
	 * <p>
	 * @param s
	 * @param i
	 */
	public MessageTip(String s, Image i) {
		super(s, i);
	}

	/**
	 * Constructs an instance of <code>MessageTip</code>.
	 * <p>
	 * @param text
	 */
	public MessageTip(String text) {
		super(text);
	}

}
