/*
 * @(#)LoginDialog.java
 * Created on 7/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.dialog;

import javax.security.auth.login.LoginException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.maji.MajiSystemPreferencePage;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;


/**
 * <code>LoginDialog</code>.
 * <p>
 * @author Kin Wong
 */
public class LoginDialog extends TitleAreaDialog {
	private final static int LOGIN_ID = IDialogConstants.CLIENT_ID + 1;
	private final String QUICK_PASSWORD = "org.openmajik.intermajik.quickpassword";

//	private Image dlgTitleImage;
	private Text txtUserName;
	private Text txtPassword;
	
	private String userName;
	private String password;
	
	private SecurityManager securityManager;
	boolean loggingIn = false;
	
	/**
	 * Constructs an instance of <code>LoginDialog</code>.
	 * <p>
	 * @param parentShell
	 */
	private LoginDialog(Shell parentShell, SecurityManager securityManager) {
		super(parentShell);
		this.securityManager = securityManager;
	}

	static public void promptLogin() {
		SecurityManager securityManager = SecurityManager.getInstance();
		if(securityManager.isLoggedIn()) return;

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		LoginDialog loginDialog = new LoginDialog(shell, securityManager);
		loginDialog.open();
		//if(loginDialog.getReturnCode() == Window.CANCEL) break;
	}
	
	/**
	 * Gets the user name.
	 * @return The user name.
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Gets the password.
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		//dlgTitleImage = Images.getImage(Display.getDefault(), Images.class, "majitek.bmp");
		//setTitleImage(dlgTitleImage);
		setTitle("Login");
		setMessage("Type the user name and password to login to InterMajik.");
		
		return control;
	}


	/**
	 * Overridden to create only on button for OK_ID with "Login" as text.
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, LOGIN_ID, "Login", true);
		createButton(
			parent,
			IDialogConstants.CANCEL_ID,
			IDialogConstants.CANCEL_LABEL,
			false);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);
		
		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parentComposite.getFont());

		layout.numColumns = 2;
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		String defaultUserName = MajiPlugin.getDefault().getPreferenceStore().getString(MajiSystemPreferencePage.MAJI_USERNAME_PROPERTIES);
		
		// User name
		Label label = new Label(composite, SWT.WRAP);
		label.setText("&User Name");

		GridData data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_END |
			GridData.VERTICAL_ALIGN_CENTER);
		//data.widthHint = widthHint;

		label.setLayoutData(data);
		label.setFont(parent.getFont());

		data = new GridData(
		GridData.GRAB_HORIZONTAL |
		GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = widthHint;
		txtUserName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		txtUserName.setLayoutData(data);
		txtUserName.setText(defaultUserName);

		// Password
		// User name
		label = new Label(composite, SWT.WRAP);
		label.setText("&Password");
			
		// Enable Quick-Password Feature for develper
		String quickPassword = System.getProperty(QUICK_PASSWORD);
		if((quickPassword != null)  && (quickPassword.equalsIgnoreCase("true"))) {
			label.addMouseListener(new MouseListener(){
				public void mouseDoubleClick(MouseEvent e) {
					txtPassword.setText(txtUserName.getText() + "99");
				}
				public void mouseDown(MouseEvent e) {}
				public void mouseUp(MouseEvent e) {}
			});
		}

		data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_END|
			GridData.VERTICAL_ALIGN_CENTER);

		label.setLayoutData(data);
		label.setFont(parent.getFont());

		txtPassword = new Text(composite, SWT.SINGLE | SWT.BORDER);
		txtPassword.setEchoChar('*');
		data = new GridData(
		GridData.GRAB_HORIZONTAL |
		GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = widthHint;
		txtPassword.setLayoutData(data);
		if(defaultUserName.length() > 0) txtPassword.setFocus();

		// horizontal bar
		Label bar = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		bar.setLayoutData(data);
		return composite;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if(buttonId == LOGIN_ID) {
			userName = txtUserName.getText();
			password = txtPassword.getText();
			LoginPressed();
		} 
		else
		super.buttonPressed(buttonId);
	}

	protected void LoginPressed() {
		try {
			securityManager.login(userName, password);
			close();
			
			MajiPlugin.getDefault().getPluginPreferences().setValue(MajiSystemPreferencePage.MAJI_USERNAME_PROPERTIES, userName);
			MajiPlugin.getDefault().savePluginPreferences();
		}
		catch(LoginException e) {
			setErrorMessage("Your details could not be authenticated - Please try again.");
			//setErrorMessage(e.getLocalizedMessage());
		}
	}
}
