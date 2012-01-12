/*
 * @(#)MeemSpaceLabelProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemstore;


import org.eclipse.jface.viewers.LabelProvider;
import org.openmaji.meem.MeemPath;

/**
 * @author mg
 * Created on 20/01/2003
 */
public class MeemStoreLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		if (obj instanceof MeemPath) 
			return ((MeemPath)obj).toString();
			
		return super.getText(obj);
	}

}
