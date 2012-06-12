/*
 * @(#)ClassnameFilter.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view.log;

import java.util.*;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class ClassnameFilter extends ViewerFilter {

	Set excludeSet = new HashSet();
	
	public ClassnameFilter() {
		updateFilterSettings();
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
//		String className = (String)((LogEvent)element).payload.get(LogPayload.CLASS);
//		
//		for (Iterator i = excludeSet.iterator(); i.hasNext();) {
//			Pattern p = (Pattern)i.next();
//			
//			if (p.matcher(className).find())
//				return false;
//		}
		
		return true;
	}

	public void updateFilterSettings() {
		IDialogSettings settings = MajiPlugin.getDefault().getDialogSettings();
		
		String filterString = settings.get(MajiLogView.LOG_CLASS_FILTER);
		
		excludeSet.clear();
		
		if (filterString == null)
			return;
		
		StringTokenizer tok = new StringTokenizer(filterString, ", ");
		while (tok.hasMoreTokens()) {
			excludeSet.add(Pattern.compile(((String)tok.nextElement()).trim()));
		}
	}

}
