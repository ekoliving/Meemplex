/*
 * @(#)CategoryLabelProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view.category;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.icon.Icon;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeem;



/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class CategoryLabelProvider extends LabelProvider {

	private static final Image image = ImageDescriptor.createFromFile(Icon.class, "meem.gif").createImage();;

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof NamedMeem) {		
			return image;
		}
		
		return super.getImage(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof NamedMeem) {
			return ((NamedMeem)element).getName();
		}
		
		return super.getText(element);
	}

}
