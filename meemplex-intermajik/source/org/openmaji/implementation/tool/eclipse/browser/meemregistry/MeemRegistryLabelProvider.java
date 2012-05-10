/*
 * @(#)MeemRegistryLabelProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.meemregistry;


import org.eclipse.jface.viewers.LabelProvider;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemRegistryLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		
		if (element instanceof Meem) {
			MeemPath meemPath = ((Meem)element).getMeemPath();

			return meemPath.toString();
		}
		if (element instanceof MeemRegistryClientImpl) {
			return "MeemRegistry";
		}
		
		return super.getText(element);
	}

}
