/*
 * @(#)ImagesFigure.java
 * Created on 8/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * <code>ImagesFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class ImagesFigure extends Figure {
	
	private final int TEMPLATE_INDEX = 0;
	private final int IMAGE_INDEX = 1;
	
	private Image[] images = new Image[2];
	private Dimension size = new Dimension();
	
	public ImagesFigure() {
		setOpaque(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return size;
	}
	
	public void setImage(Image image) {
		images[IMAGE_INDEX] = image;
		updateSize();
		revalidate();
		repaint();
	}
	
	public void setTemplate(Image image) {
		images[TEMPLATE_INDEX] = image;
		updateSize();
		revalidate();
		repaint();
	}
	
	private void updateSize() {
		size = new Dimension();
		for(int i = 0; i < images.length; i++) {
			Image image = images[i];
			if(image != null) {
				int width = image.getBounds().width;
				int height = image.getBounds().height;
				if(size.width < width) size.width = width;
				if(size.height < height) size.height = width;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Rectangle area = getClientArea();
		
		for(int i = 0; i < images.length; i++) {
			Image image = images[i];
			if(image == null) continue;
			org.eclipse.swt.graphics.Rectangle bounds = image.getBounds();
			int x = (area.width - bounds.width) / 2 + area.x;
			int y = (area.height - bounds.height) / 2 + area.y;
			graphics.drawImage(image, x, y);
		}
	}
}
