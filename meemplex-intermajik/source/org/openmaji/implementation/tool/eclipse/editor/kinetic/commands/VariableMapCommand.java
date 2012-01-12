/*
 * @(#)VariableMapCommand.java
 * Created on 5/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;



import org.eclipse.gef.commands.Command;
import org.openmaji.common.VariableMap;


/**
 * <code>VariableMapCommand</code> presents the based implementation of command 
 * that uses <code>VariableMap</code> as the target.
 * <p>
 * @see org.openmaji.common.VariableMap
 * @author Kin Wong
 */
abstract public class VariableMapCommand extends Command {
	private VariableMap variableMap;

	protected VariableMapCommand(VariableMap variableMap) {
		this.variableMap = variableMap;
	}

	public VariableMap getVariableMap() {
		return variableMap;
	}
}
