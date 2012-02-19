/*
 * @(#)IViewModeProvider.java
 * Created on 11/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.multiview;

import java.util.List;

import org.openmaji.implementation.intermajik.model.ViewMode;


/**
 * The <code>IViewModeProvider</code> defines the contract that allows the 
 * client to enumerates all the view modes supported by an implementation, 
 * queries the current view mode and change it.
 * 
 * <p>
 * @author Kin Wong
 */
public interface IViewModeProvider {
	/**
	 * Gets the current view mode of the implementation.
	 * @return Object The current view mode of the implementation.
	 */
	ViewMode getViewMode();
	
	/**
	 * Sets the view mode of the implementation.
	 * @param viewMode The new view mode of the implementation.
	 */
	void setViewMode(ViewMode viewMode);
	
	/**
	 * Gets all the view modessupported by the implementation  as 
	 * <code>ViewModeDescriptor</code>.
	 * @return List All the view mode as <code>ViewModeDescriptor</code>.
	 * @see ViewModeDescriptor
	 */
	List<ViewModeDescriptor> getViewModes();

	/**
	 * Checks whether a view mode is supported by the implementation.
	 * @param viewMode view object the implementation supports.
	 * @return boolean true is the view mode is supported by the implementation,
	 * false otherwise.
	 */
	boolean supportViewMode(ViewMode viewMode);
}
