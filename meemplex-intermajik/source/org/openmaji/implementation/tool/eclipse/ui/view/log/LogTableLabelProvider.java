/*
 * @(#)LogTableLabelProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Add Icons for different log levels (info, warn, error)
 */
package org.openmaji.implementation.tool.eclipse.ui.view.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.swzoo.log2.core.LogEvent;
import org.swzoo.log2.core.LogLevel;
import org.swzoo.log2.core.LogPayload;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LogTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof LogEvent) {
			return getText((LogEvent) element, columnIndex);
		}
		return null;
	}

	private String getText(LogEvent event, int columnIndex) {
		if (columnIndex == 0) {
			// time
			Date date = (Date) event.payload.get(LogPayload.TIMESTAMP);
			if (date != null) 
				return FORMATTER.format(date);
		}
		if (columnIndex == 1) {
			Object value = event.payload.get(LogPayload.LEVEL);
			if (value instanceof Integer) {
				return LogLevel.formatter.toString((Integer) value);
			}
		}
		if (columnIndex == 2) {
			Object value = event.payload.get(LogPayload.CLASS);
			if (value instanceof String) {
				return (String) value;
			}
		}
		if (columnIndex == 3) {
			Object value = event.payload.get(LogPayload.TEXT);
			if (value instanceof String) {
				return (String) value;
			}
		}
		return null;
	}

}
