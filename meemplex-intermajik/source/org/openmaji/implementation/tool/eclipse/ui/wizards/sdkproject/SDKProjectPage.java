/*
 * @(#)SDKProjectPage.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.wizards.sdkproject;

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
 * @author  ben
 * @version 1.0
 */
public class SDKProjectPage extends WizardPage implements ModifyListener {

	private Text txtProjectName;
	private Text txtPackageName;
    private Text txtProjectPath;

	public SDKProjectPage() {
		super("SDK Project Creator Page"); 
		setTitle("SDK Project Creator Page");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout());

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		parent.layout(true);
		
		Label lblProjectName = new Label(composite, SWT.NONE);
		lblProjectName.setText("Project Name");		
		
		txtProjectName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		txtProjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtProjectName.addModifyListener(this);
		
		Label lblPackageName = new Label(composite, SWT.NONE);
		lblPackageName.setText("Project Package Name");		
	
		txtPackageName = new Text(composite, SWT.MULTI | SWT.BORDER );
        txtPackageName.setTextLimit(Integer.MAX_VALUE);
        txtPackageName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtPackageName.addModifyListener(this);
		
        Label lblProjectPath = new Label(composite, SWT.NONE);
        lblProjectPath.setText("Project Path");       
    
        txtProjectPath = new Text(composite, SWT.MULTI | SWT.BORDER );
        txtProjectPath.setTextLimit(Integer.MAX_VALUE);
        txtProjectPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtProjectPath.addModifyListener(this);
         
		setControl(composite);
				
		setPageComplete(validatePage());
	}
	
	public void modifyText(ModifyEvent e) {
  	setPageComplete(validatePage());
  }

	public boolean validatePage() {
		setMessage(null);
		if (txtProjectName.getText().length() == 0) {
			setMessage("Please enter the project name");
			return false;
		}
		if (txtPackageName.getText().length() == 0) {
			setMessage("Please enter the package name");
			return false;
		}
        
        if (txtProjectPath.getText().length() == 0) {
            setMessage("Please enter the package path");
            return false;
        }
		setMessage("Click Next to continue");

		return true;
	}
    
    public String getProjectName() {
        return txtProjectName.getText();
    }
    
	public String getPackageName() {
		return txtPackageName.getText();
    }
       
    public String getProjectPath() {
         return txtProjectPath.getText();
         
	}
	


}
