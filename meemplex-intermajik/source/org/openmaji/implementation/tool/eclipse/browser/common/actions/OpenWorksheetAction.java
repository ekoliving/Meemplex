/*
 * @(#)OpenWorksheetAction.java
 * Created on 26/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.common.actions;


import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.dialogs.DialogUtil;
import org.eclipse.ui.part.ViewPart;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerAction;
import org.openmaji.implementation.tool.eclipse.plugin.MajiPlugin;
import org.openmaji.implementation.tool.eclipse.util.MeemEditorInput;
import org.openmaji.meem.MeemPath;


/**
 * <code>OpenWorksheetAction</code>.
 * <p>
 * @author Kin Wong
 */
public class OpenWorksheetAction extends ControllerAction {
	/**
	 * Constructs an instance of <code>OpenWorksheetAction</code>.<p>
	 */
	public OpenWorksheetAction(Controller controller) {
		super(controller, "Open Worksheet");
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.hierarchy.controllers.ControllerAction#update()
	 */
	public void update() {
		super.update();
		boolean isOpened = false;
		MeemClientProxy proxy = (MeemClientProxy)
			getController().getAdapter(MeemClientProxy.class);
		if(proxy != null) {
			MeemEditorInput editorInput = new MeemEditorInput(proxy.getMeemPath());
			isOpened = isEditorOpened(getController().getViewPart(), 
																editorInput);
		}

		if(isOpened) {
			setText("Show Worksheet");
		}
		else {
			setText("Open Worksheet");
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		 MeemClientProxy proxy = 
		 	(MeemClientProxy)getController().getAdapter(MeemClientProxy.class);
		 if(proxy == null) return;
		openWorksheet(getController().getViewPart(), proxy.getMeemPath());
	}
	
	static public boolean isEditorOpened(ViewPart viewPart, MeemEditorInput editorInput) {
		IWorkbenchPage page = getActivePage(viewPart);
		return (page.findEditor(editorInput) != null);
	}
	
	static public boolean createWorksheet(ViewPart viewPart, IEditorInput editorInput) {
		IWorkbenchPage page = getActivePage(viewPart);
		try {
			page.openEditor(editorInput, MajiPlugin.KINETIC_EDITOR);
		} 
		catch (PartInitException e) {
			DialogUtil.
				openError(page.getWorkbenchWindow().getShell(), 
				"editor not opened", e.getMessage(), e);
				return false;
		}
		return true;		
	}
	
	static private IWorkbenchPage getActivePage(ViewPart viewPart) {
		return viewPart.getViewSite().getWorkbenchWindow().getActivePage();
	}
	
	static public boolean openWorksheet(ViewPart viewPart, MeemPath meemPath) {
		// open it in the config editor
		IWorkbenchPage page = getActivePage(viewPart);
		MeemEditorInput input = new MeemEditorInput(meemPath);
		IEditorPart editorPart = page.findEditor(input);

		if(editorPart == null) {
			return createWorksheet(viewPart, input);
		}
		else {
			showEditor(page, editorPart);
		}
		return true;
	}
	
	
	
	static private void showEditor(IWorkbenchPage page, IEditorPart editorPart) {
		page.activate(editorPart);
	}
}
