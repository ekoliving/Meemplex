/*
 * @(#)UserDetailsPage.java
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
import org.eclipse.swt.events.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
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
public class UserDetailsPage extends WizardPage implements ModifyListener, SelectionListener {
	
	private Text txtName;
	private Text txtEmail;
	private Text txtEmailHost;
	private Text txtEmailUser;
	private Text txtEmailPassword;
	private Button chkSaveEmailPassword;
	private Button chkEmailRequiresLogin;
	
	private String name = "";
	private String email = "";
	private String emailHost = "";
	private String emailUser = "";
	private String emailPassword = "";
	private boolean saveEmailPassword = false;
	private boolean emailRequiresLogin = false;
	
	private Composite compositeMailDetails;
	
	public UserDetailsPage() {
		super("User Details Page");
		setTitle("User Details Page");
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout());

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		parent.layout(true);
		
		Composite compositeUserDetails = new Composite(composite, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		
		compositeUserDetails.setLayout(gridLayout);

		compositeUserDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblName = new Label(compositeUserDetails, SWT.NONE);
		lblName.setText("Name");		
		
		txtName = new Text(compositeUserDetails, SWT.SINGLE | SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtName.setText(name);
		txtName.addModifyListener(this);
		
		Label lblEmail = new Label(compositeUserDetails, SWT.NONE);
		lblEmail.setText("Email Address");		
	
		txtEmail = new Text(compositeUserDetails, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtEmail.setLayoutData(gridData);
		txtEmail.setText(email);
		txtEmail.addModifyListener(this);
		
		Label lblEmailHost = new Label(compositeUserDetails, SWT.NONE);
		lblEmailHost.setText("Email Server (SMTP)");		
	
		txtEmailHost = new Text(compositeUserDetails, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtEmailHost.setLayoutData(gridData);
		txtEmailHost.setText(emailHost);
		txtEmailHost.addModifyListener(this);
		
		chkEmailRequiresLogin = new Button(compositeUserDetails, SWT.CHECK);
    chkEmailRequiresLogin.setText("My Email server requires me to log in.");
    chkEmailRequiresLogin.setSelection(emailRequiresLogin);
    chkEmailRequiresLogin.addSelectionListener(this);
		
		compositeMailDetails = new Composite(composite, SWT.NONE);
		
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		compositeMailDetails.setLayout(gridLayout);

		compositeMailDetails.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		Label lblEmailUser = new Label(compositeMailDetails, SWT.NONE);
		lblEmailUser.setText("Email UserName");		
		
		Label lblEmailPassword = new Label(compositeMailDetails, SWT.NONE);
		lblEmailPassword.setText("Email Password");	
	
		txtEmailUser = new Text(compositeMailDetails, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtEmailUser.setLayoutData(gridData);
		txtEmailUser.setText(emailUser);
		txtEmailUser.addModifyListener(this);
		
			
		txtEmailPassword = new Text(compositeMailDetails, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		txtEmailPassword.setLayoutData(gridData);
		txtEmailPassword.setText(emailPassword);
		txtEmailPassword.setEchoChar('*');
		txtEmailPassword.addModifyListener(this);
		
		chkSaveEmailPassword = new Button(compositeMailDetails, SWT.CHECK);
    chkSaveEmailPassword.setText("Save email password");
    chkSaveEmailPassword.setSelection(saveEmailPassword);
		
		setLoginDetailsEnabled();
		
		setControl(composite);
		
		setPageComplete(validatePage());
	}
	
	private void setLoginDetailsEnabled() {
		compositeMailDetails.setEnabled(emailRequiresLogin);		
		compositeMailDetails.setVisible(emailRequiresLogin);
		compositeMailDetails.redraw();
	}

	public void modifyText(ModifyEvent e) {
  	setPageComplete(validatePage());
  }

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		emailRequiresLogin = !emailRequiresLogin;
		setLoginDetailsEnabled();
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		// don't care	
	}

	public boolean validatePage() {
		setMessage(null);
		if (txtName.getText().length() == 0) {
			setMessage("Please enter your name");
			return false;
		}
		if (txtEmail.getText().length() == 0) {
			setMessage("Please enter your email address");
			return false;
		}
		setMessage("Click Next to continue");
		return true;
	}
	
	public String getName() {
		return txtName.getText();
	}

	public String getEmail() {
		return txtEmail.getText();
	}
	
	public String getEmailHost() {
		return txtEmailHost.getText();
	}
	
	public String getEmailUser() {
		return txtEmailUser.getText();
	}
	
	public String getEmailPassword() {
		return txtEmailPassword.getText();
	}
	
	public boolean getSaveEmailPassword() {
		return chkSaveEmailPassword.getSelection();
	}
	
	public boolean getEmailRequiresLogin() {
		return chkEmailRequiresLogin.getSelection();
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}
	
	public void setEmailUser(String emailUser) {
		this.emailUser = emailUser;
	}
	
	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}
	
	public void setSaveEmailPassword(boolean save) {
		saveEmailPassword = save;
	}
	
	public void setEmailRequiresLogin(boolean requireLogin) {
		emailRequiresLogin = requireLogin;
	}

}
