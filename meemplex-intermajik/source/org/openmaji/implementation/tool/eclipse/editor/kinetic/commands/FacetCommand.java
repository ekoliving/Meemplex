/*
 * @(#)FacetCommand.java
 * Created on 15/08/2003
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
 * <code>FacetCommand</code> repesents the base implementation of commands
 * the issue facet attributed related MetaMeem request.
 * <p>
 * @see org.openmaji.system.meem.definition.MetaMeem
 * @see org.openmaji.meem.definition.FacetAttribute
 * @author Kin Wong
 */
abstract public class FacetCommand extends MetaMeemCommand {
	private Serializable wedgeKey;
	private FacetAttribute attribute;
	
	/**
	 * Constructs an instance of <code>FacetCommand</code>.
	 * <p>
	 * @param metaMeem The target <code>MetaMeem</code>.
	 * @param wedgeKey The wedge where this facet attribute is contained.
	 * @param attribute The <code>FacetAttribute</code> associates with this
	 * command.
	 */
	protected FacetCommand(MetaMeem metaMeem, Serializable wedgeKey, FacetAttribute attribute) {
		super(metaMeem);
		this.wedgeKey = wedgeKey;
		this.attribute = (FacetAttribute)attribute.clone();
	}
	
	/**
	 * Gets the <code>FacetAttribute</code> associates with this facet command.
	 * @return FacetAttribute The <code>FacetAttribute</code> associates with this 
	 * facet command.
	 */	
	protected FacetAttribute getAttribute() {
		return attribute;
	}
	
	/**
	 * Gets the key the identifies the wedge in which the facet is contained.
	 * @return Object The key the identifies the wedge in which the facet is 
	 * contained.
	 */
	protected Serializable getWedgeKey() {
		return wedgeKey;
	}
}
