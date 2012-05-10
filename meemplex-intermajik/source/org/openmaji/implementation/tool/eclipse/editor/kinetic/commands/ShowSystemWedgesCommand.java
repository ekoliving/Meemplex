/*
 * @(#)ShowSystemWedgesCommand.java
 * Created on 29/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;


/**
 * <code>ShowSystemWedgesCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class ShowSystemWedgesCommand extends Command {
	Meem meem;
	boolean lastShow;
	boolean show;
	/**
	 * Constructs an instance of <code>ShowSystemWedgesCommand</code>.
	 * <p>
	 * 
	 */
	public ShowSystemWedgesCommand(Meem meem, boolean show) {
		this.meem = meem;
		this.show = show;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		lastShow = meem.isSystemWedgeShown();
		meem.setViewMode(ViewModeConstants.VIEW_MODE_DETAILED);
		meem.showSystemWedge(show);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		meem.showSystemWedge(lastShow);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		meem.showSystemWedge(show);
	}
}
