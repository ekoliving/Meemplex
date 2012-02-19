/*
 * @(#)MajiLogView.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - max entries limit
 */
package org.openmaji.implementation.tool.eclipse.ui.view.log;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.tool.eclipse.icon.Icon;
import org.swzoo.log2.core.LogEvent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MajiLogView extends ViewPart {

	public static final String LOG_TRACE_ENABLED = "LogTraceEnabled";
	public static final String LOG_TRACE_LEVEL = "LogTraceLevel";
	public static final String LOG_AUTOSCROLL = "LogAutoscroll";
	public static final String LOG_CLASS_FILTER = "LogClassFilter";

	TableViewer viewer;
	Action filterAction;
	LogLevelFilter logLevelfilter = new LogLevelFilter();
	ClassnameFilter classNamefilter = new ClassnameFilter();
	LogTableContentProvider contentProvider = new LogTableContentProvider();

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);

		GridData gd = new GridData(GridData.FILL_BOTH);
		Table table = viewer.getTable();
		table.setLayoutData(gd);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Time");
		column.setWidth(160);
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Level");
		column.setWidth(50);
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Class");
		column.setWidth(340);
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Text");
		column.setWidth(350);

		table.pack();
		
		viewer.addFilter(logLevelfilter);
		viewer.addFilter(classNamefilter);

		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new LogTableLabelProvider());
		viewer.setInput(new Object());
		viewer.addDoubleClickListener(new DoubleClickListener());

		createActions();
		createToolbar();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	public void createActions() {
		filterAction = new Action("Filters") {
			public void run() {
				showFilterDialog();
			}
		};
		filterAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "filter.gif"));
		filterAction.setToolTipText("Filters");
	}

	protected void showFilterDialog() {
		LogViewPreferencesDialog prefsDialog = new LogViewPreferencesDialog(getViewSite().getWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell());
		prefsDialog.open();
		logLevelfilter.updateFilterSettings();
		classNamefilter.updateFilterSettings();	
		contentProvider.setAutoscroll();
		viewer.refresh();		
	}
	
	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(filterAction);
	}


	private class DoubleClickListener implements IDoubleClickListener {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
		 */
		public void doubleClick(DoubleClickEvent event) {
			LogEvent logEvent = (LogEvent)((IStructuredSelection)event.getSelection()).getFirstElement();
			LogDetailDialog eventDialog = new LogDetailDialog(getViewSite().getWorkbenchWindow().getActivePage().getWorkbenchWindow().getShell(), logEvent);
			eventDialog.open();
		}

	}
}
