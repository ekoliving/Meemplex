/*
 * @(#)DockedImageFreeformLayer.java
 * Created on 11/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.editor.features.util.FigureHelper;


/**
 * <code>DockedImageFreeformLayer</code> allows an image to be docked at one of 
 * the four corners of the viewport.
 * <p>
 * @author Kin Wong
 */
public class DockedImageFreeformLayer extends ImageFreeformLayer {
	private AncestorListener ancestorListener = new AncestorListener() {
		public void ancestorAdded(IFigure ancestor) {
			updateViewport();
		}
		public void ancestorMoved(IFigure ancestor) {
			updateViewport();
		}
		public void ancestorRemoved(IFigure ancestor) {
			updateViewport();
		}
	};
	
	private Viewport viewport;
	
	/**
	 * Constructs an instance of <code>DockedImageFreeformLayer</code>.
	 * <p>
	 * 
	 */
	public DockedImageFreeformLayer() {
	}

	/**
	 * Constructs an instance of <code>DockedImageFreeformLayer</code>.
	 * <p>
	 * @param image
	 */
	public DockedImageFreeformLayer(Image image) {
		super(image);
	}

	/**
	 * Constructs an instance of <code>DockedImageFreeformLayer</code>.
	 * <p>
	 * @param location
	 * @param filename
	 */
	public DockedImageFreeformLayer(Class location, String filename) {
		super(location, filename);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		Image image = getImage();
		if(image == null) return;

		Rectangle bounds = getBounds();
		if(viewport != null) {
			graphics.translate(viewport.getViewLocation());
			bounds = viewport.getBounds();
		}
		graphics.drawImage(	image, 
		bounds.right() - image.getBounds().width, 
		bounds.bottom() - image.getBounds().height);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#setParent(org.eclipse.draw2d.IFigure)
	 */
	public void setParent(IFigure p) {
		if(getParent() != null)  {
			getParent().removeAncestorListener(ancestorListener);
		}

		super.setParent(p);

		if(getParent() != null) {
			getParent().addAncestorListener(ancestorListener);
		}
	}
	
	private void updateViewport() {
		Viewport newViewport = (Viewport)FigureHelper.findAncestor(this, Viewport.class);
		if(viewport == newViewport) return;

		viewport = newViewport;
		if(viewport != null) {
			viewport.addPropertyChangeListener(Viewport.PROPERTY_VIEW_LOCATION, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					repaint();
				}
			});
		}
	}
}
