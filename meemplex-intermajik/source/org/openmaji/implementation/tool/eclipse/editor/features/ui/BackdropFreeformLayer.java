/*
 * @(#)BackdropFreeformLayer.java
 * Created on 7/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;


import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * <code>BackdropFreeformLayer</code> extends the <code>ImageFreeformLayer</code> 
 * by tiling the image as background.
 * <p>
 * @author Kin Wong
 */
public class BackdropFreeformLayer extends ImageFreeformLayer {
	/**
	 * Constructs an instance of <code>BackdropFreeformLayer</code>.
	 * <p>
	 */
	public BackdropFreeformLayer() {
		super();
	}
	
	/**
	 * Constructs an instance of <code>BackdropFreeformLayer</code> with an
	 * image.
	 * <p>
	 * 
	 */
	public BackdropFreeformLayer(Image image) {
		super(image);
	}
	
	/**
	 * Constructs an instance of <code>BackdropFreeformLayer</code> with the
	 * location and filename of the backdrop image.
	 * <p>
	 * @param location The location of the image.
	 * @param filename The filename of the image.
	 * @see org.eclipse.jface.resource.ImageDescriptor#createFromFile(java.lang.Class, java.lang.String)
	 */
	public BackdropFreeformLayer(Class location, String filename) {
		super(location, filename);
	}
	
	/**
	 * Overridden to paint the tiled backdrop image.
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure(Graphics graphics) {
		Image image = getImage();
		if(image == null) {
			super.paintFigure(graphics);
			return;
		}
		graphics.fillRectangle(getBounds());
		
		Rectangle clip = Rectangle.SINGLETON;
		graphics.getClip(clip);

		Rectangle rectangle = new Rectangle(image.getBounds().x, image.getBounds().y, 
																				image.getBounds().width, image.getBounds().height);
//		translateToRelative(rectangle);
		//translateToAbsolute(rectangle);
		int imageWidth = rectangle.width;// image.getBounds().width;
		int imageHeight = rectangle.height; //image.getBounds().height;
		int right = clip.right();
		int bottom = clip.bottom();
		
		
		//clip.x -= (imageBounds.width - 1);
		//clip.y -= (imageBounds.height - 1);
//		Rectangle target = new Rectangle();
//		Point point = new Point();
		clip.y = clip.y - (clip.y % imageHeight);
		clip.x = clip.x - (clip.x % imageWidth);
		
		
		for(int y = clip.y; y < bottom; y += imageHeight)
		for(int x = clip.x; x < right; x += imageWidth) {
			/*
			target.x = x;
			target.y = y;
			
			point.x = x;
			point.y = y;
			translateToRelative(point);
			translateToAbsolute(point);
			target.setLocation(point);
			point.x += imageWidth;
			point.y += imageHeight;
			translateToRelative(point);
			translateToAbsolute(point);
			*/
			//target.setSize(point.x - target.x + 1, point.y - target.y + 1);
			graphics.drawImage(image, x, y);
			//graphics.fillRectangle(target);//x, y, imageWidth, imageHeight);//target);
		}
		//graphics.fillRectangle(rectangle);
	}
}
