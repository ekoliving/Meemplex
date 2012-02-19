/*
 * @(#)MeemUpdateCommand.java
 * Created on 16/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>MeemUpdateCommand</code>.<p>
 * @author Kin Wong
 */
public class MeemUpdateCommand extends MetaMeemCommand {
	private MeemAttribute attribute;
	/**
	 * Constructs an instance of <code>MeemUpdateCommand</code>.<p>
	 * @param metaMeem
	 */
	public MeemUpdateCommand(MetaMeem metaMeem, MeemAttribute attribute) {
		super(metaMeem);
		attribute = (MeemAttribute)attribute.clone();
	}

	/**
	 * Gets the <code>MeemAttribute</code> associates with this meem command.
	 * <p>
	 * @return MeemAttribute The <code>MeemAttribute</code> associates with this 
	 * meem command.
	 */
	protected MeemAttribute getAttribute() {
		return attribute;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getMetaMeem().updateMeemAttribute(attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
