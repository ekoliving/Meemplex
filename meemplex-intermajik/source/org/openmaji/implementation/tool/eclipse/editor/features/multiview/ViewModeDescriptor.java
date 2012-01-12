/*
 * @(#)ViewModeDescriptor.java
 * Created on 19/06/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.multiview;

import java.io.Serializable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.openmaji.implementation.intermajik.model.ViewMode;


/**
 * <code>ViewModeDescriptor</code> describes a view mode supported by 
 * a <code>IViewModeProvider</code> implementation.
 * <p>
 * @author Kin Wong
 */
public class ViewModeDescriptor implements Serializable {
	private static final long serialVersionUID = 0L;
	
	private ViewMode viewMode;
	private String label;
	private String tooltip;
	private ImageDescriptor normalImage;
	//private ImageDescriptor hoverImage;
	
	/**
	 * Constructs an instance of <code>ViewModeDescriptor</code>.
	 * <p>
	 * @param viewMode The view mode of this descriptor.
	 * @param label The textual name of the view mode.
	 * @param image The image associates with this view mode.
	 */
	public ViewModeDescriptor(ViewMode viewMode, String label, ImageDescriptor image) {
		this.viewMode = viewMode;
		this.label = label;
		this.normalImage = image;
	}

	/**
	 * Constructs an instance of <code>ViewModeDescriptor</code>.
	 * <p>
	 * @param viewMode The view mode of this descriptor.
	 */
	public ViewModeDescriptor(ViewMode viewMode) {
		this.viewMode = viewMode;
	}
	
	/**
	 * Gets the view mode which uniquely identifies this view mode in the 
	 * object.
	 * @return Object The view mode which can be used as ID to identifies this
	 * view mode in the provider.
	 */
	public ViewMode getViewMode() {
		return viewMode;		
	}
	
	/**
	 * Gets the label of this view mode.
	 * @return String The label of this view model.
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Gets the tooltip text of this view mode.
	 * @return String The tooltip text of this view mode.
	 */
	public String getTooltip() {
		return tooltip;
	}
	
	/**
	 * Gets the normal image associates with this view mode.
	 * @return ImageDescriptor The normal image associates with this view mode.
	 */
	public ImageDescriptor getImage() {
		return normalImage;
	}
	
	/**
	 * Sets the label of this view mode.
	 * @param label The label of this view mode.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Sets the tooltip text of this view mode.
	 * @param tooltip The tooltip text of this view mode.
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	
	/**
	 * Sets the normal image of this view mode.
	 * @param image The normal image of this view mode.
	 */
	public void setImage(ImageDescriptor image) {
		this.normalImage = image;
	}
	
	/**
	 * Sets the hover image of this view mode descriptor.
	 * @param image The hover image of this view mode descriptor.
	 */
	public void setHoverImage(ImageDescriptor image) {
		//hoverImage = image;
	}
}
