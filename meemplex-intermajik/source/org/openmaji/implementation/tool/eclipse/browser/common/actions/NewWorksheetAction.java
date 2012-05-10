/*
 * @(#)NewWorksheetAction.java
 * Created on 16/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.browser.common.actions;

import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.hierarchy.controllers.Controller;
import org.openmaji.implementation.tool.eclipse.util.MeemEditorInput;
import org.openmaji.meem.MeemPath;


/**
 * <code>NewWorksheetAction</code>.<p>
 * @author Kin Wong
 */
public class NewWorksheetAction extends OpenWorksheetAction {

	/**
	 * Constructs an instance of <code>NewWorksheetAction</code>.<p>
	 * @param controller
	 */
	public NewWorksheetAction(Controller controller) {
		super(controller);
		setText("Open New View to Worksheet");
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.actions.OpenWorksheetAction#update()
	 */
	public void update() {
		setEnabled(isOpened());
	}
	
	public boolean isOpened() {
		MeemClientProxy proxy = (MeemClientProxy)
			getController().getAdapter(MeemClientProxy.class);
		if(proxy != null) {
			MeemEditorInput editorInput = new MeemEditorInput(proxy.getMeemPath());
			return isEditorOpened(getController().getViewPart(), editorInput);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.browser.common.actions.OpenWorksheetAction#run()
	 */
	public void run() {
		MeemClientProxy proxy = 
		   (MeemClientProxy)getController().getAdapter(MeemClientProxy.class);
		if(proxy == null) return;
		MeemEditorInput editorInput = createEditorInput(proxy.getMeemPath());
		createWorksheet(getController().getViewPart(), editorInput);
	}
	
	MeemEditorInput createEditorInput(MeemPath meemPath) {
		for(int ordinal = 1; ordinal < 10; ordinal++) {
			MeemEditorInput editorInput = new MeemEditorInput(meemPath, ordinal);
			if(!isEditorOpened(getController().getViewPart(), editorInput)) 
			return editorInput;
		}
		return new MeemEditorInput(meemPath);
	}
}
