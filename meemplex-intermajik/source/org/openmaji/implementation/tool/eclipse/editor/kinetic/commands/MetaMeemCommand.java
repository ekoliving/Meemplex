/*
 * @(#)MetaMeemCommand.java
 * Created on 10/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;


import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;
import org.openmaji.system.meem.definition.MetaMeem;

/**
 * <code>MetaMeemCommand</code> represents the based implementation of command
 * that uses <code>MetaMeem</code> as the target.
 * <p>
 * @see org.openmaji.system.meem.definition.MetaMeem
 * @author Kin Wong
 */
abstract public class MetaMeemCommand extends Command {
	private MetaMeem metaMeem;
	/**
	 * Constructs an instance of <code>MetaMeemCommand</code>.
	 * <p>
	 * @param metaMeem The target <code>MetaMeem</code> associates with this 
	 * MetaMeem command.
	 */
	protected MetaMeemCommand(MetaMeem metaMeem) {
		Assert.isNotNull(metaMeem);
		this.metaMeem = metaMeem;
	}

	/**
	 * Gets the target <code>MetaMeem</code> of this MetaMeem command.
	 * <p>
	 * @return MetaMeem The target <code>MetaMeem</code> of this MetaMeem command.
	 */
	protected MetaMeem getMetaMeem() {
		return metaMeem;
	}
}
