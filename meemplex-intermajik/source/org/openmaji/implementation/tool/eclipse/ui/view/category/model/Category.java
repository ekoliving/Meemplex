/*
 * @(#)Category.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view.category.model;

import java.io.Serializable;

import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class Category extends ElementContainer {
	private static final long serialVersionUID = 6424227717462161145L;

	private String name = "";
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getId()
	 */
	public Serializable getId() {
		return "Category";
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.model.Element#getName()
	 */
	public String getName() {
		return name;
	}
	
	

}
