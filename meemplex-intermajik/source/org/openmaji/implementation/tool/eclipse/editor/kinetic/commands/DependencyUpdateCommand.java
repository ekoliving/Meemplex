/*
 * @(#)DependencyUpdateCommand.java
 * Created on 11/06/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>DependencyUpdateCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyUpdateCommand extends DependencyCommand {
	DependencyAttribute oldAttribute;
	public DependencyUpdateCommand(MetaMeem metaMeem, DependencyAttribute attribute) {
		super(metaMeem, attribute);
		oldAttribute = (DependencyAttribute)attribute.clone();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getMetaMeem().updateDependencyAttribute(getAttribute());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getMetaMeem().updateDependencyAttribute(oldAttribute);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
