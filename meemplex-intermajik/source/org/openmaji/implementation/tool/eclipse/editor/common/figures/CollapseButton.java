/*
 * @(#)CollapseButton.java
 * Created on 19/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.figures;

import org.eclipse.draw2d.Toggle;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.openmaji.implementation.tool.eclipse.editor.common.shapes.Lines;


/**
 * <code>CollapseButton</code> is a <code>Figure</code> that represents the
 * collapse/expand toggle button of a <code>CollapsibleFigure</code>.
 * <p>
 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.CollapsibleFigure
 * @author Kin Wong
 */
public final class CollapseButton extends Toggle {
	private Lines lines = new Lines();
	/**
	 * Constructs an instance of <code>CollapseButton</code>.
	 * <p>
	 */
	public CollapseButton() {
		setOpaque(true);
		setStyle(STYLE_TOGGLE);
		lines.setForegroundColor(getForegroundColor());
		setContents(lines);

		setRequestFocusEnabled(false);
		setFocusTraversable(false);
		setRolloverEnabled(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setForegroundColor(org.eclipse.swt.graphics.Color)
	 */
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		lines.setForegroundColor(fg);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	public void validate() {
		super.validate();
		Rectangle r = getBounds().getCopy();
		r.crop(getInsets());
		r.resize(-1,-1);
		r.shrink(2,2);
		
		lines.removeAllPoints();
		Point center = r.getCenter();
		
		if(isSelected()) {
			// Show plus
			lines.addLine(r.x, center.y, r.right(), center.y);
			lines.addLine(center.x, r.y, center.x, r.bottom());
		}
		else {
			// Show minus
			lines.addLine(r.x, center.y, r.right(), center.y);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Clickable#setSelected(boolean)
	 */
	public void setSelected(boolean value) {
		super.setSelected(value);
		validate();
	}
}
