/*
 * @(#)VariableMapDeleteCommand.java
 * Created on 6/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import java.io.Serializable;

import org.openmaji.common.VariableMap;


/**
 * <code>VariableMapDeleteCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class VariableMapDeleteCommand extends VariableMapCommand {
	private Serializable key;
	private Serializable value;
	/**
	 * Constructs an instance of <code>VariableMapDeleteCommand</code>.
	 * <p>
	 * @param variableMap
	 */
	public VariableMapDeleteCommand(VariableMap variableMap, Serializable key, Serializable value) {
		super(variableMap);
		this.key = key;
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getVariableMap().remove(key);		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getVariableMap().update(key, value);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
