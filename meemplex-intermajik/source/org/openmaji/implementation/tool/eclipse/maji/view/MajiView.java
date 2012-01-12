/*
 * @(#)MajiView.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.maji.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.tool.eclipse.icon.Icon;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MajiView extends ViewPart {
	Text text;
	Action startMajiAction; //, stopMajiAction;

	private boolean started = false;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.NONE);
		text.setEditable(false);
		text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		createActions();
		createToolbar();
		
		startMajiAction.run();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	public void createActions() {
		startMajiAction = new Action("Start Maji System") {
			public void run() {
				if (!started)
					startMaji();
			}
		};
		startMajiAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "plus.gif"));
		startMajiAction.setToolTipText("Start Maji System");

		//		stopMajiAction = new Action("Stop Maji System") {
		//			public void run() {
		//				stopMaji();
		//			}
		//		};
		//		stopMajiAction.setImageDescriptor(ImageDescriptor.createFromFile(Icon.class, "delete.gif"));
		//		stopMajiAction.setToolTipText("Stop Maji System");

	}

	private void createToolbar() {
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(startMajiAction);
		//mgr.add(stopMajiAction);
	}

	private void startMaji() {
		if (MajiPlugin.startMaji()) 
			text.setText("Started"); 
		else 
			text.setText("Launch properties file not set");		
	}

//	private void stopMaji() {
//	}
}
