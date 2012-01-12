/*
 * @(#)LogDetailDialog.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.*;
import org.swzoo.log2.core.LogEvent;
import org.swzoo.log2.core.LogLevel;
import org.swzoo.log2.core.LogPayload;
import org.swzoo.nursery.stack.TransferrableThrowable;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LogDetailDialog extends Dialog {
	static int COPY_TO_CLIPBOARD_ID = IDialogConstants.CLIENT_ID + 1;
	
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS EEEE dd MMMM yyyy");

	private LogEvent event;

	public LogDetailDialog(Shell parentShell, LogEvent event) {
		super(parentShell);
		this.event = event;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(
			parent,
			COPY_TO_CLIPBOARD_ID,
			"&Copy to Clipboard", false);
		super.createButtonsForButtonBar(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if(buttonId == COPY_TO_CLIPBOARD_ID) {
			copyToClipboard();
		}
		super.buttonPressed(buttonId);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Label lblTime = new Label(composite, SWT.LEFT);
		lblTime.setText("Time: " + FORMATTER.format((Date) event.payload.get(LogPayload.TIMESTAMP)));

		Label lblLevel = new Label(composite, SWT.LEFT);
		lblLevel.setText("Level: " + LogLevel.formatter.toString((Integer) event.payload.get(LogPayload.LEVEL)));

		Label lblClass = new Label(composite, SWT.LEFT);
		lblClass.setText("Class: " + event.payload.get(LogPayload.CLASS));

		Label lblText = new Label(composite, SWT.LEFT);
		lblText.setText("Message: " + event.payload.get(LogPayload.TEXT));

		Object entry = event.payload.get(LogPayload.THROWABLE);
		if (entry != null) {
			Label lblThrowable = new Label(composite, SWT.LEFT);
			
			if (entry instanceof TransferrableThrowable) {
				lblThrowable.setText(formatTransferrableThrowable((TransferrableThrowable) entry));
			}
			if (entry instanceof Throwable) {
				lblThrowable.setText(formatThrowable((Throwable) entry));
			}
		}

		return composite;
	}

	private void copyToClipboard() {
		Clipboard clipboard = new Clipboard(Display.getCurrent());
		String textData = getReport();
		TextTransfer textTransfer = TextTransfer.getInstance();
		clipboard.setContents(new Object[] { textData }, new Transfer[] { textTransfer });
		clipboard.dispose();
		
		MessageBox msgBox = new MessageBox(Display.getDefault().getActiveShell());
		msgBox.setText("Copy to Clipboard");
		msgBox.setMessage("Log details has been copied to the clipboard.");
		msgBox.open();
	}
	
	private String getReport() {
		StringBuffer report = new StringBuffer();
		String eol = "\n";
		report.append("Time: " + FORMATTER.format((Date) event.payload.get(LogPayload.TIMESTAMP)));
		report.append(eol);
		report.append("Level: " + LogLevel.formatter.toString((Integer) event.payload.get(LogPayload.LEVEL)));
		report.append(eol);
		report.append("Class: " + event.payload.get(LogPayload.CLASS));
		report.append(eol);
		report.append("Message: " + event.payload.get(LogPayload.TEXT));
		report.append(eol);
		return report.toString();
	}

	protected String formatThrowable(Throwable t) {
		return formatTransferrableThrowable(new TransferrableThrowable(t));
	}

	protected String formatTransferrableThrowable(TransferrableThrowable tt) {
		return tt.getFullString();
	}

}
