/*
 * @(#)ClassLabelProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.openmaji.implementation.tool.eclipse.library.classlibrary.ClassDescriptor;


/**
 * @author mg
 * Created on 13/01/2003
 */
public class ClassLabelProvider extends LabelProvider{

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (element instanceof ClassDescriptor) {
			return ((ClassDescriptor)element).getClassName();
		}
		return super.getText(element);
		
	}

}
