/*
 * @(#)WedgeCommand.java
 * Created on 15/08/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MetaMeem;


/**
 * <code>WedgeCommand</code> repesents the base implementation of commands
 * the issue wedge attributed related MetaMeem request.
 * <p>
 * @see org.openmaji.system.meem.definition.MetaMeem
 * @see org.openmaji.meem.definition.WedgeAttribute
 * @author Kin Wong
 */
abstract public class WedgeCommand extends MetaMeemCommand {
	private WedgeAttribute attribute;
	
	/**
	 * Constructs an instance of <code>WedgeCommand</code>.
	 * <p>
	 * @param metaMeem The target <code>MetaMeem</code>.
	 * @param attribute The <code>WedgeAttribute</code> associates with this
	 * wedge command.
	 */	
	protected WedgeCommand(MetaMeem metaMeem, WedgeAttribute attribute) {
		super(metaMeem);
		this.attribute = (WedgeAttribute)attribute.clone();
	}
	
	/**
	 * Gets the <code>WedgeAttribute</code> associates with this wedge command.
	 * <p>
	 * @return WedgeAttribute The <code>WedgeAttribute</code> associates with this 
	 * wedge command.
	 */
	protected WedgeAttribute getAttribute() {
		return attribute;
	}
}
