/*
 * @(#)BugDetailsPage.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.wizards.bugreport;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class BugDetailsPage extends WizardPage implements ModifyListener {

	private Text txtSummary;
	private Text txtDetails;

	public BugDetailsPage() {
		super("Bug Details Page"); 
		setTitle("Bug Details Page");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout());

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		parent.layout(true);
		
		Label lblSummary = new Label(composite, SWT.NONE);
		lblSummary.setText("Bug summary");		
		
		txtSummary = new Text(composite, SWT.SINGLE | SWT.BORDER);
		txtSummary.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtSummary.addModifyListener(this);
		
		Label lblDetails = new Label(composite, SWT.NONE);
		lblDetails.setText("Bug Details");		
	
		txtDetails = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		txtDetails.setTextLimit(Integer.MAX_VALUE);
		txtDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		txtDetails.addModifyListener(this);
		
		setControl(composite);
				
		setPageComplete(validatePage());
	}
	
	public void modifyText(ModifyEvent e) {
  	setPageComplete(validatePage());
  }

	public boolean validatePage() {
		setMessage(null);
		if (txtSummary.getText().length() == 0) {
			setMessage("Please enter a bug summary");
			return false;
		}
		if (txtDetails.getText().length() == 0) {
			setMessage("Please enter detailed information about the bug");
			return false;
		}
		
		setMessage("Click Next to continue");

		return true;
	}
	
	public String getSummary() {
		return txtSummary.getText();
	}
	
	public String getDetails() {
		return txtDetails.getText();
	}

}
