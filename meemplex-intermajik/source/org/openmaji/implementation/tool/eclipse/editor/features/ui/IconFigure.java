/*
 * @(#)IconFigure.java
 * Created on 12/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;

/**
 * <code>IconFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class IconFigure extends Figure {
	Image image;
	ImageFigure imageFigure;
	Label label;
	
	public IconFigure() {
		imageFigure = new ImageFigure();
		label = new Label();
		
		add(imageFigure);
		LayoutManager layout = new ToolbarLayout();
		setLayoutManager(layout);
	}
}
