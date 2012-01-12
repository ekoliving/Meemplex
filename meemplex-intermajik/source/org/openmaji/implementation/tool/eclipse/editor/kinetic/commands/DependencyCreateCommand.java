/*
 * @(#)DependencyCreateCommand.java
 * Created on 24/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>DependencyCreateCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyCreateCommand extends DependencyCommand {
	private String facetKey;
	
	/**
	 * Constructs an instance of <code>DependencyCreateCommand</code>.
	 * <p>
	 * @param metaMeem
	 * @param facetKey 
	 * @param attribute
	 */
	public DependencyCreateCommand(
	MetaMeem metaMeem, String facetKey, DependencyAttribute attribute) {
		super(metaMeem, attribute);
		this.facetKey = facetKey;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		//System.out.println("executing command that add Dependency: facetKey = " + facetKey + " , Attribute = " + getAttribute());
		getMetaMeem().addDependencyAttribute(facetKey, getAttribute());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getMetaMeem().removeDependencyAttribute(getAttribute().getKey());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
