/*
 * @(#)SendErrorDialog.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.wizards.bugreport;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class SendErrorDialog extends MessageDialog {
	
	String message = "";
	
	public SendErrorDialog(Shell parentShell, String message) {
		super(parentShell, "Send Error", null, "An error has occured while trying to send the bug report.", 1, new String[] {"Copy Message", "Close"}, 0);
		this.message = message;
	}
	
	public Control createCustomArea(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout());

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblName = new Label(composite, SWT.NONE);
		lblName.setText("Error Details:\n\n" + message);
		
		String msg = "\nPlease check your mail settings. Click \"Close\" then \"Back\"\n";
		msg += "Alternativly, click the \"Copy Message\" button below to copy the report text onto the clipboard.\n";
		msg += "Please send this text to " + BugReportWizard.BUG_REPORT_EMAIL_ADDRESS + "\n";
		
		
		Label lblCopy = new Label(composite, SWT.NONE);
		lblCopy.setText(msg);
		
		return composite;
	}

}
