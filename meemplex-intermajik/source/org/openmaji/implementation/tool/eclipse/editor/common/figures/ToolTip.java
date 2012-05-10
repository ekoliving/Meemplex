/*
 * @(#)Tooltip.java
 * Created on 29/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.swt.graphics.Image;

/**
 * <code>Tooltip</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class ToolTip extends Label {
	public ToolTip() {
		initialize();
	}
	/**
	 * Constructs an instance of <code>Tooltip</code>.
	 * <p>
	 * @param i
	 */
	public ToolTip(Image i) {
		super(i);
		initialize();
	}
	/**
	 * Constructs an instance of <code>Tooltip</code>.
	 * <p>
	 * @param s
	 * @param i
	 */
	public ToolTip(String s, Image i) {
		super(s, i);
		initialize();
	}

	public ToolTip(String text) {
		super(text);
		initialize();
	}
	
	private void initialize() {
		setBorder(new MarginBorder(1,2,1,2));
	}
}
