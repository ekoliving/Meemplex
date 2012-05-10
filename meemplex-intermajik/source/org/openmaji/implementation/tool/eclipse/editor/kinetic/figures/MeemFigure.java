/*
 * @(#)MeemFigure.java
 * Created on 21/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;

/**
 * <code>MeemFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemFigure extends Figure {
	private ImagesFigure images;
	
	//static private Dimension FIGURE_SIZE = new Dimension(36,36);
	
	/**
	 * Constructs an instance of <code>MeemFigure</code>.
	 * <p>
	 * 
	 */
	public MeemFigure() {
		// Icon Image
		setLayoutManager(new ToolbarLayout());
		images = new ImagesFigure();
		add(images);
	}
	
	public void setMeemIcon(Image image) {
		images.setImage(image);
	}
	
	public void setTemplate(Image image) {
		images.setTemplate(image);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
}
