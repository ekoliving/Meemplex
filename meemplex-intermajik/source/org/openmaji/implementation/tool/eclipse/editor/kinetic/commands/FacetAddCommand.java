/*
 * @(#)AddFacetCommand.java
 * Created on 24/07/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import java.io.Serializable;

import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>AddFacetCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetAddCommand extends FacetCommand {
	/**
	 * Constructs an instance of <code>FacetAddCommand</code>.
	 * <p>
	 * @param metaMeem The target <code>MetaMeem</code>.
	 * @param wedgeKey The wedge where this facet attribute is contained.
	 * @param attribute The <code>FacetAttribute</code> to be added.
	 */
	public FacetAddCommand(MetaMeem metaMeem, Serializable wedgeKey, FacetAttribute attribute) {
		super(metaMeem, wedgeKey, attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getMetaMeem().addFacetAttribute(getWedgeKey(), getAttribute());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getMetaMeem().removeFacetAttribute(getAttribute().getIdentifier());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
