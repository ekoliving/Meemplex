/*
 * @(#)AddWedgeCommand.java
 * Created on 24/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>WedgeAddCommand</code> adds a new wedge attribute to 
 * <code>MetaMeem</code>.
 * <p>
 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.WedgeCommand
 * @author Kin Wong
 */
public class WedgeAddCommand extends WedgeCommand {
	/**
	 * Constructs an instance of <code>WedgeAddCommand</code>.
	 * <p>
	 * @param metaMeem The target <code>MetaMeem</code>.
	 * @param attribute The <code>WedgeAttribute</code> associates with this
	 * wedge add command.
	 */
	public WedgeAddCommand(MetaMeem metaMeem, WedgeAttribute attribute) {
		super(metaMeem, attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getMetaMeem().addWedgeAttribute(getAttribute());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getMetaMeem().removeWedgeAttribute(getAttribute().getIdentifier());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
