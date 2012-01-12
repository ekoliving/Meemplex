/*
 * @(#)FacetUpdateCommand.java
 * Created on 17/11/2003
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
 * <code>FacetUpdateCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetUpdateCommand extends FacetCommand {
	private FacetAttribute oldAttribute;
	/**
	 * Constructs an instance of <code>FacetUpdateCommand</code>.
	 * <p>
	 * @param metaMeem
	 * @param wedgeKey
	 * @param attribute
	 */
	protected FacetUpdateCommand(MetaMeem metaMeem, Serializable wedgeKey, FacetAttribute attribute) {
		super(metaMeem, wedgeKey, attribute);
		oldAttribute = (FacetAttribute)attribute.clone();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getMetaMeem().updateFacetAttribute(getAttribute());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getMetaMeem().updateFacetAttribute(oldAttribute);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
