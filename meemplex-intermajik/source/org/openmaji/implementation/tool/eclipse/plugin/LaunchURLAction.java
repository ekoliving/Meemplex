/*
 * @(#)LaunchURLAction.java
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

import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.internal.browser.BrowserManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class LaunchURLAction extends Action {
	
	String url;
	String label;
	
	public LaunchURLAction(String label, String url) {
		this.url = url;
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
		if (SWT.getPlatform().equals("win32")) {
			Program.launch(url);
		} else {
			IBrowser browser = BrowserManager.getInstance().createBrowser();
			try {
				browser.displayURL(url);
			} catch (Exception e) {
			}
		}
	}
}
