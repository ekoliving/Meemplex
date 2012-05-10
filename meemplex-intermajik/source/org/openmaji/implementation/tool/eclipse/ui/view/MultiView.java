/*
 * @(#)MultiView.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.tool.eclipse.icon.Icon;


/**
    * <p>
    * This allows you to duplicate views within views to get around
    * Eclipse's inability to have more than one instance of a view. 
    * </p>
    * @author  mg
    * @version 1.0
    */
public abstract class MultiView extends ViewPart {

	private GridLayout gridLayout;
	private Composite parentComposite;
	private List columnContents = new ArrayList();

	Action addColumnAction, removeColumnAction;

	public MultiView() {
		super();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		gridLayout = new GridLayout();
		gridLayout.numColumns = 0;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);

		parentComposite = parent;

		createActions();
		createMenu();
		
		addView();

		initialize();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	protected Composite addColumn() {
		Composite contentPane = new Composite(parentComposite, SWT.NO_FOCUS);
		contentPane.setLayoutData(new GridData(GridData.FILL_BOTH));

		columnContents.add(gridLayout.numColumns, contentPane);

		gridLayout.numColumns++;

		return contentPane;

	}

	protected abstract void addView();
	protected abstract void initialize();

	private void remove() {
		if (gridLayout.numColumns == 1)
			return; 
		
		gridLayout.numColumns--;
		
		Composite contentPane = (Composite)columnContents.remove(gridLayout.numColumns);
		contentPane.dispose();		
		
		parentComposite.layout(true);
	}

	public int getColumnCount() {
		return gridLayout.numColumns;
	}

	private void createActions() {
		addColumnAction = new Action("Add Duplicate") {
			public void run() {
				addView();
				parentComposite.layout(true);
			}
		};
		addColumnAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "plus.gif"));
		addColumnAction.setToolTipText("Add Duplicate");

		removeColumnAction = new Action("Remove Duplicate") {
			public void run() {
				remove();
			}
		};
		removeColumnAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "delete.gif"));
		removeColumnAction.setToolTipText("Remove Duplicate");
	}

	private void createMenu() {
		IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		mgr.add(addColumnAction);
		mgr.add(removeColumnAction);
	}

}
