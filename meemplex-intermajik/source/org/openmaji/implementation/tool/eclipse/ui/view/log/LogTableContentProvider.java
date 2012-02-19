/*
 * @(#)LogTableContentProvier.java
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

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.client.SWTClientSynchronizer;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.util.log.EclipseLog;
import org.openmaji.implementation.tool.eclipse.util.log.EclipseLogListener;
import org.swzoo.log2.core.LogEvent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LogTableContentProvider implements EclipseLogListener, IStructuredContentProvider {

	TableViewer viewer;
	Set logEntries;
	boolean autoscroll = true;

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.util.log.EclipseLogListener#event(org.swzoo.log2.core.LogEvent)
	 */
	public void event(final LogEvent event) {
		Display display = viewer.getControl().getDisplay();
		SWTClientSynchronizer.get(display).execute(new Runnable() {
			public void run() {
				if (viewer != null)
					viewer.add(event);
					if (autoscroll)
						viewer.reveal(event);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		viewer = null;
		EclipseLog.getInstance().removeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput != null) {
			this.viewer = (TableViewer) viewer;
			// connect to log
			logEntries = EclipseLog.getInstance().addListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return (Object[]) logEntries.toArray(new Object[logEntries.size()]);
	}

	public void setAutoscroll() {
		IDialogSettings settings = MajiPlugin.getDefault().getDialogSettings();
		autoscroll = settings.getBoolean(MajiLogView.LOG_AUTOSCROLL);
	}
}
