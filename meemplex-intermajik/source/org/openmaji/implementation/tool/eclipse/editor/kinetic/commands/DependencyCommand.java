/*
 * @(#)DependencyCommand.java
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
 * <code>DependencyCommand</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class DependencyCommand extends MetaMeemCommand {
	private DependencyAttribute attribute;
	/**
	 * Constructs an instance of <code>DependencyCommand</code>.
	 * <p>
	 * @param metaMeem
	 * @param attribute The <code>DependencyAttribute</code> associates with this
	 * dependency command.
	 */
	protected DependencyCommand(MetaMeem metaMeem, DependencyAttribute attribute) {
		super(metaMeem);
		this.attribute = (DependencyAttribute)attribute.clone();
	}
	
	public DependencyAttribute getAttribute() {
		return attribute;
	}
}
