/*
 * @(#)CreateWorksheetDialog.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.ui.dialog;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.openmaji.system.space.hyperspace.StandardHyperSpaceCategory;


/**
 * @author mg
 */
public class CreateWorksheetDialog extends TitleAreaDialog {

	public static final String NEW_WORKSHEET_SUBSYSTEM = "New Worksheet Subsystem";

	public static final String DEFAULT_WORKSHEET_SUBSYSTEM = "Default Worksheet Subsystem";

	private Text txtWorksheetName;
	private String worksheetName;

	private Combo comboSubsystem;
	private String subsystemName;

	MeemClientProxy proxy;

	CategoryClient categoryClient;

	private IInputValidator validator;

	public CreateWorksheetDialog(Shell parentShell, IInputValidator validator) {
		super(parentShell);
		this.validator = validator;
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		setTitle("Create Worksheet");
		setMessage("Enter the name of the new worksheet and select the subsystem it should use to create new Meems");

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
		//String defaultUserName = MajiPlugin.getDefault().getPreferenceStore().getString(MajiSystemPreferencePage.MAJI_USERNAME_PROPERTIES);

		Label label = new Label(composite, SWT.WRAP);
		label.setText("&Worksheet Name");

		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_END
				| GridData.VERTICAL_ALIGN_CENTER);
		//data.widthHint = widthHint;

		label.setLayoutData(data);
		label.setFont(parent.getFont());

		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = widthHint;
		txtWorksheetName = new Text(composite, SWT.SINGLE | SWT.BORDER);
    txtWorksheetName.setFocus();
		txtWorksheetName.setLayoutData(data);
		txtWorksheetName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});

		label = new Label(composite, SWT.WRAP);
		label.setText("Subsystem");

		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_END
				| GridData.VERTICAL_ALIGN_CENTER);

		label.setLayoutData(data);
		label.setFont(parent.getFont());

		comboSubsystem = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);

		populateCombo();

		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = widthHint;

		// horizontal bar
		Label bar = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		bar.setLayoutData(data);
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	public class CategoryClientImpl implements CategoryClient {

		public void entriesAdded(CategoryEntry[] newEntries) {
			for (int i = 0; i < newEntries.length; i++) {
				comboSubsystem.add(newEntries[i].getName());
			}
		}

		public void entriesRemoved(CategoryEntry[] removedEntries) {
			// -mg- Auto-generated method stub
		}

		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			// -mg- Auto-generated method stub
		}
	}

	private void populateCombo() {
		comboSubsystem.add(DEFAULT_WORKSHEET_SUBSYSTEM);
		comboSubsystem.add(NEW_WORKSHEET_SUBSYSTEM);
		
		comboSubsystem.select(0);

		proxy = InterMajikClientProxyFactory.getInstance().getMeem(
				MeemPath.spi.create(Space.HYPERSPACE, StandardHyperSpaceCategory.MAJI_SUBSYSTEM_INSTALLED));
		categoryClient = new CategoryClientImpl();
		proxy.getCategoryProxy().addClient(categoryClient);
	}

	public String getWorksheetName() {
		return worksheetName;
	}
	
	public String getWorksheetSubsystem() {
		return subsystemName;
	}

	protected void validateInput() {
		String errorMessage = null;
		if (validator != null) {
			errorMessage = validator.isValid(txtWorksheetName.getText());
		}
		setErrorMessage(errorMessage);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (proxy != null) {
			proxy.getCategoryProxy().removeClient(categoryClient);
		}
		worksheetName = txtWorksheetName.getText();
		subsystemName = comboSubsystem.getText();
		super.buttonPressed(buttonId);
	}
}