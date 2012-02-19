/*
 * @(#)LogFilter.java
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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.swzoo.log2.core.LogEvent;
import org.swzoo.log2.core.LogPayload;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LogLevelFilter extends ViewerFilter {
	private int traceLevel = 100;
	private boolean showTrace = true;
	
	public LogLevelFilter() {
			updateFilterSettings();
		}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		// allow all non trace events
		Boolean trace = (Boolean)((LogEvent)element).payload.get(LogPayload.TRACE);
		if (trace == null || !trace.booleanValue())
			return true;
		
		if (showTrace) {
			// filter by trace level	
			Object value = ((LogEvent)element).payload.get(LogPayload.LEVEL);
			if (value instanceof Integer) {
				int level = ((Integer) value).intValue();
				if (level < traceLevel)
					return true;
			}
		}
		return false;
	}
	
	public void updateFilterSettings() {
		IDialogSettings settings = MajiPlugin.getDefault().getDialogSettings();
		
		showTrace = settings.getBoolean(MajiLogView.LOG_TRACE_ENABLED);
		String level = settings.get(MajiLogView.LOG_TRACE_LEVEL);
		if (level == null)
			traceLevel = 100;
		else 
			traceLevel = Integer.parseInt(level); 
	}

}
