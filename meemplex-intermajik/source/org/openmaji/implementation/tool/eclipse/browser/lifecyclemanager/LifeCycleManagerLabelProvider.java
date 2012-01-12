/*
 * @(#)LifeCycleManagerLabelProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.browser.lifecyclemanager;


import org.eclipse.jface.viewers.LabelProvider;
import org.openmaji.meem.Meem;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LifeCycleManagerLabelProvider extends LabelProvider {
	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(final Object element) {

		if (element instanceof Meem) {
			return ((Meem)element).getMeemPath().toString();
		}

		if (element instanceof LifeCycleManagerClientImpl) {
			return "LifeCycle Manager: " + ((LifeCycleManagerClientImpl)element).getMeemPath().toString();
		}

		return super.getText(element);
	}
}
