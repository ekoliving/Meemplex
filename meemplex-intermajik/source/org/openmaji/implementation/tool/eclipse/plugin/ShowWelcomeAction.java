/*
 * @(#)ShowWelcomeAction.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.plugin;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ide.AboutInfo;
import org.eclipse.ui.internal.ide.dialogs.WelcomeEditorInput;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class ShowWelcomeAction extends Action {
	
	private static final String EDITOR_ID = "org.eclipse.ui.internal.dialogs.WelcomeEditor";

	/**
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	public String getText() {
		return "Welcome Page";
	}


	public void run() {
		
		AboutInfo feature = AboutInfo.readFeatureInfo("org.openmajik.intermajik.feature", "1.1.0");
		
		IWorkbenchPage page = MajiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		page.setEditorAreaVisible(true);

		// create input
		WelcomeEditorInput input = new WelcomeEditorInput(feature);

		// see if we already have a welcome editor
		IEditorPart editor = page.findEditor(input);
		if (editor != null) {
			page.activate(editor);
			return;
		}

		try {
			page.openEditor(input, EDITOR_ID);
		} catch (PartInitException e) {
//			IStatus status = new Status(IStatus.ERROR, WorkbenchPlugin.PI_WORKBENCH, 1, WorkbenchMessages.getString("QuickStartAction.openEditorException"), e); //$NON-NLS-1$
//			ErrorDialog.openError(
//				window.getShell(),
//				WorkbenchMessages.getString("Workbench.openEditorErrorDialogTitle"),  //$NON-NLS-1$
//				WorkbenchMessages.getString("Workbench.openEditorErrorDialogMessage"), //$NON-NLS-1$
//				status);
		}
	}

}
