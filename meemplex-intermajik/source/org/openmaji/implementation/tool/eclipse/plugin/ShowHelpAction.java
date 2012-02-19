/*
 * @(#)ShowHelpAction.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.plugin;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class ShowHelpAction extends Action  {
	
	String helpUrl;
	String label;
	
	public ShowHelpAction(String label, String href) {
		this.helpUrl = href;
		this.label = label;
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	public String getText() {
		return label;
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		final String href = helpUrl;
			if (href != null) {
				BusyIndicator.showWhile( MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay(), new Runnable() {
					public void run() {
						WorkbenchHelp.displayHelpResource(href);
					}
				});
			}	
	}

}
