/*
 * Created on 19/03/2003
 *
/*
 * @(#)FlatScrollBar.java
 * Created on 7/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.flat;

import org.eclipse.draw2d.ArrowButton;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.ScrollBar;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * FlatScrollBar represents a scrollbar that appears flat.
 * <p>
 * @author Kin Wong
 */
public class FlatScrollBar extends ScrollBar {
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ScrollBar#createDefaultDownButton()
	 */
	protected Clickable createDefaultDownButton() {
		Button buttonDown = new ArrowButton();
		buttonDown.setBorder(new LineBorder(ColorConstants.black));
		buttonDown.setBackgroundColor(getBackgroundColor());
		return buttonDown;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ScrollBar#createDefaultThumb()
	 */
	protected IFigure createDefaultThumb() {
		Panel thumb = new Panel();
		thumb.setMinimumSize(new Dimension(6,6));
		thumb.setBackgroundColor(getBackgroundColor());

		thumb.setBorder(new LineBorder(ColorConstants.black));
		return thumb;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.ScrollBar#createDefaultUpButton()
	 */
	protected Clickable createDefaultUpButton() {
		Button buttonUp = new ArrowButton();
		buttonUp.setBorder(new LineBorder(ColorConstants.black));
		buttonUp.setBackgroundColor(getBackgroundColor());
		return buttonUp;
	}
}
