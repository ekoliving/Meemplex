/*
 * @(#)DependencyDeleteCommand.java
 * Created on 24/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>DependencyDeleteCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyDeleteCommand extends DependencyCommand {
	private String facetKey;
	/**
	 * Constructs an instance of <code>DependencyDeleteCommand</code>.
	 * <p>
	 * @param metaMeem
	 * @param facetKey
	 * @param attribute
	 */
	public DependencyDeleteCommand(MetaMeem metaMeem, String facetKey, DependencyAttribute attribute) {
		super(metaMeem, attribute);
		this.facetKey = facetKey;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getMetaMeem().removeDependencyAttribute(getAttribute().getKey());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getMetaMeem().addDependencyAttribute(facetKey, getAttribute());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
