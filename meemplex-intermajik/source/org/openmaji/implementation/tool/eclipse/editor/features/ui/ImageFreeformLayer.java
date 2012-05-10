/*
 * @(#)ImageFreeformLayer.java
 * Created on 11/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * <code>ImageFreeformLayer</code> extends the <code>FreeformLayer</code> with
 * the support of an associated image.
 * <p>
 * @author Kin Wong
 */
abstract public class ImageFreeformLayer extends FreeformLayer {
	private Image image;
	
	/**
	 * Constructs an instance of <code>ImageFreeformLayer</code>.
	 * <p>
	 */
	protected ImageFreeformLayer() {
	}

	/**
	 * Constructs an instance of <code>ImageFreeformLayer</code> with an
	 * image.
	 * <p>
	 * @param image The image to be used in the layer.
	 */
	protected ImageFreeformLayer(Image image) {
		setImage(image);
	}
	
	/**
	 * Constructs an instance of <code>ImageFreeformLayer</code> with the
	 * location and filename of the backdrop image.
	 * <p>
	 * @param location The location of the image.
	 * @param filename The filename of the image.
	 * @see org.eclipse.jface.resource.ImageDescriptor#createFromFile(java.lang.Class, java.lang.String)
	 */
	protected ImageFreeformLayer(Class location, String filename) {
		setImage(location, filename);
	}

	/**
	 * Sets the image associates with this layer.
	 * @param image The backdrop image.
	 */
	public void setImage(Image image) {
		this.image = image;
		repaint();
	}
	
	/**
	 * Gets the image associates with this layer.
	 * @return Image The image associates with this layer.
	 */
	protected Image getImage() {
		return image;
	}
	
	/**
	 * Sets the image associates with this layer from the location and filename.
	 * @param location The location of the image.
	 * @param filename The filename of the image.
	 * @see org.eclipse.jface.resource.ImageDescriptor#createFromFile(java.lang.Class, java.lang.String)
	 */
	public void setImage(Class location, String filename) {
		clearImage();
		this.image = ImageDescriptor.createFromFile(location, filename).createImage();
	}
	
	protected void clearImage() {
		if(image == null) return;
		image.dispose();
		image = null;
	}
}
