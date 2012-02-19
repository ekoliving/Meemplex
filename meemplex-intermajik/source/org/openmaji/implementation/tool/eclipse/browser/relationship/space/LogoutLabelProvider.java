/*
 * @(#)LogoutLabelProvider.java
 * Created on 5/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.relationship.space;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>LogoutLabelProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class LogoutLabelProvider extends LabelProvider {
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		return Images.ICON_LOGIN.createImage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return "Click here to login to InterMajik";
	}
}
