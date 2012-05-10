/*
 * @(#)FigureLabel.java
 * Created on 22/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.labels;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.implementation.tool.eclipse.editor.features.util.FigureHelper;


/**
 * <code>FigureLabel</code> represents a text label in the label layer for any
 * Figure in the primary layer.
 * <p>
 * @author Kin Wong
 */
public class FigureLabel extends AbstractLabel implements PositionConstants {
	public FigureLabel() {
		this(null);
	}
	
	public FigureLabel(Figure owner) {
		super(owner);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.labels.AbstractLabel#getVisibility()
	 */
	protected boolean getVisibility() {
		return FigureHelper.isVisibleInRootFigure(owner);
	}

	/**
	 * Updates the position of this label.
	 */
	public void update() {
		if((owner != null) && (paint = getVisibility())) {

			Rectangle labelBounds = Rectangle.SINGLETON.setBounds(owner.getBounds());
			owner.translateToAbsolute(labelBounds);
			
			translateToRelative(labelBounds);
			Point center = labelBounds.getCenter();
			Dimension size = label.getPreferredSize();
			labelBounds.x = center.x - size.width / 2;
			labelBounds.y = labelBounds.bottom() + 4;
			labelBounds.setSize(size);
			setBounds(labelBounds);
			labelBounds.setLocation(0,0);
			backdrop.setBounds(labelBounds);
			label.setBounds(labelBounds);
		}
		repaint();
	}
}
