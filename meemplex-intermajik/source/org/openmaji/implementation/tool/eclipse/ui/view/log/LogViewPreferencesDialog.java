/*
 * @(#)LogViewPreferencesDialog.java
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LogViewPreferencesDialog extends Dialog {

	Button chkTrace;
	Button chkAutoscroll;
	Label lblTrace;
	Text txtTraceLevel;
	
	Label lblClassFilter;
	Text txtClassFilter;

	public LogViewPreferencesDialog(Shell parentShell) {
		super(parentShell);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		IDialogSettings settings = MajiPlugin.getDefault().getDialogSettings();
		
		Composite composite = (Composite) super.createDialogArea(parent);

		chkTrace = new Button(composite, SWT.CHECK);
		chkTrace.setText("Show trace messages");
		chkTrace.setSelection(settings.getBoolean(MajiLogView.LOG_TRACE_ENABLED));
		chkTrace.addSelectionListener(new CheckListener());

		lblTrace = new Label(composite, SWT.LEFT);
		lblTrace.setText("Trace Level");
		
		String level = settings.get(MajiLogView.LOG_TRACE_LEVEL);
		if (level == null)
			level = "100";
			
		txtTraceLevel = new Text(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtTraceLevel.setText(level);
		
		if (!chkTrace.getSelection()) {
			lblTrace.setEnabled(false);
			txtTraceLevel.setEnabled(false);
		}

		chkAutoscroll = new Button(composite, SWT.CHECK);
		chkAutoscroll.setText("Autoscroll new log events");
		chkAutoscroll.setSelection(settings.getBoolean(MajiLogView.LOG_AUTOSCROLL));


		lblClassFilter = new Label(composite, SWT.LEFT);
		lblClassFilter.setText("Class name filter.\n Any classes that contain any of the comma separated list of strings will not be shown");
				
		String classFilter = settings.get(MajiLogView.LOG_CLASS_FILTER);
		if (classFilter == null)
			classFilter = "";
			
		txtClassFilter = new Text(composite, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		txtClassFilter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtClassFilter.setText(classFilter);

		return composite;
	}

	public class CheckListener extends SelectionAdapter {

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			boolean checked = chkTrace.getSelection();
			if (checked) {
				lblTrace.setEnabled(true);
				txtTraceLevel.setEnabled(true);
			} else {
				lblTrace.setEnabled(false);
				txtTraceLevel.setEnabled(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		IDialogSettings settings = MajiPlugin.getDefault().getDialogSettings();
		
		settings.put(MajiLogView.LOG_TRACE_ENABLED, chkTrace.getSelection());
		settings.put(MajiLogView.LOG_TRACE_LEVEL, txtTraceLevel.getText());
		settings.put(MajiLogView.LOG_AUTOSCROLL, chkAutoscroll.getSelection());
		settings.put(MajiLogView.LOG_CLASS_FILTER, txtClassFilter.getText());
		
		super.okPressed();
	}

}
