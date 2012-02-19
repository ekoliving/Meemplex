/*
 * @(#)FlatScrollPane.java
 * Created on 7/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.flat;

import org.eclipse.draw2d.ScrollPane;


/**
 * FlatScrollPane represents a ScrollPane with flat scrollbar.
 * <p>
 * @author Kin Wong
 */
public class FlatScrollPane extends ScrollPane {
	/**
	 * Overridden to instantiate a flat scroll bar.
	 * @see org.eclipse.draw2d.ScrollPane#createHorizontalScrollBar()
	 */
	protected void createHorizontalScrollBar() {
		FlatScrollBar bar = new FlatScrollBar();
		bar.setHorizontal(true);
		setHorizontalScrollBar(bar);
	}
	/**
	 * Overridden to instantiate a flat scroll bar.
	 * @see org.eclipse.draw2d.ScrollPane#createVerticalScrollBar()
	 */
	protected void createVerticalScrollBar() {
		FlatScrollBar bar = new FlatScrollBar();
		setVerticalScrollBar(bar);
	}
}
