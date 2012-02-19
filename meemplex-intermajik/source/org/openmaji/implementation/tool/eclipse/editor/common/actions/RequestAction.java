/*
 * @(#)RequestAction.java
 * Created on 17/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.ui.IWorkbenchPart;

/**
 * <code>RequestAction</code>.<p>
 * @author Kin Wong
 */
abstract public class RequestAction extends SelectionAction {
	/**
	 * Constructs an instance of <code>RequestAction</code>.<p>
	 * @param part
	 */
	public RequestAction(IWorkbenchPart part) {
		super(part);
	}

	abstract protected Request getRequest();
	
	/**
	 * Constructs an instance of <code>RequestAction</code>.<p>
	 * @param part
	 * @param style
	 */
	public RequestAction(IWorkbenchPart part, int style) {
		super(part, style);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		List selecteds = getSelectedObjectClone();
		for (Iterator iter = selecteds.iterator(); iter.hasNext();) {
			EditPart editPart = (EditPart)iter.next();
			if(!editPart.understandsRequest(getRequest())) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		execute(createCommand());
	}
	
	protected Command createCommand() {
		CompoundCommand compoundCommand = new CompoundCommand();
		List selecteds = getSelectedObjectClone();
		for (Iterator iter = selecteds.iterator(); iter.hasNext();) {
			EditPart editPart = (EditPart)iter.next();
			compoundCommand.add(editPart.getCommand(getRequest()));
		}
		return compoundCommand.unwrap();
	}
}
