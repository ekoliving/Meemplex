/*
 * @(#)SpaceDialog.java
 * Created on 5/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.ui.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.SpaceBrowserLabelProvider;
import org.openmaji.implementation.tool.eclipse.client.InterMajikClientProxyFactory;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.CategoryNode;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.MeemPathConstructor;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.Node;
import org.openmaji.implementation.tool.eclipse.hierarchy.nodes.NodeTreeContentProvider;


/**
 * <code>SpaceDialog</code>.
 * <p>
 * @author Kin Wong
 */
public class SpaceDialog extends TitleAreaDialog {
	private TreeViewer treeViewer;
	private Text txtMeemPath;
	/**
	 * Constructs an instance of <code>SpaceDialog</code>.
	 * <p>
	 * @param parentShell
	 */
	public SpaceDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		//dlgTitleImage = Images.getImage(Display.getDefault(), Images.class, "majitek.bmp");
		//setTitleImage(dlgTitleImage);
		setTitle("Select Meem");
		setMessage("Select the meem in the space view.");
		return control;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = (Composite) super.createDialogArea(parent);

		Composite composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout();
//		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
//		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
//		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
//		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parentComposite.getFont());

		layout.numColumns = 2;
//		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
//		String defaultUserName = MajiPlugin.getDefault().getPreferenceStore().getString(MajiSystemPreferencePage.MAJI_USERNAME_PROPERTIES);
		
		// Set Tree
		treeViewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				treeSelectionChanged(event);
			}
		});
		
		String rootName = "<HyperSpace> ";
		MeemClientProxy hyperspaceRoot = InterMajikClientProxyFactory.getInstance().locateHyperSpace();

		rootName += "(" + SecurityManager.getInstance().getUser().getName() + ")";
		CategoryNode rootNode = new CategoryNode(rootName, hyperspaceRoot);
		treeViewer.setContentProvider(new NodeTreeContentProvider());
		treeViewer.setLabelProvider(new SpaceBrowserLabelProvider());
		treeViewer.setInput(rootNode);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 400;
		data.heightHint = 200;
		data.horizontalSpan = 2;
		treeViewer.getControl().setLayoutData(data);

		// Meem Path
		Label label = new Label(composite, SWT.WRAP);
		label.setText("&Meem Path:");

		data = new GridData(
			GridData.HORIZONTAL_ALIGN_BEGINNING |
			GridData.VERTICAL_ALIGN_CENTER);
		//data.widthHint = widthHint;
		//data.horizontalSpan = 1;
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		data = new GridData(
	//	GridData.GRAB_HORIZONTAL |
		GridData.HORIZONTAL_ALIGN_FILL |
		GridData.VERTICAL_ALIGN_CENTER);
//		data.widthHint = widthHint;
		txtMeemPath = new Text(composite, SWT.SINGLE | SWT.BORDER);
		txtMeemPath.setLayoutData(data);
		
		return parentComposite;
	}

	public void treeSelectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		Object[] selected = selection.toArray();
		
		String path = "hyperspace:/";
		if((selected.length > 0) && (selected[0] instanceof Node)) {
			Node node = (Node)selected[0];
			path = MeemPathConstructor.getPath(node);
		}
		txtMeemPath.setText(path);
	}
}
