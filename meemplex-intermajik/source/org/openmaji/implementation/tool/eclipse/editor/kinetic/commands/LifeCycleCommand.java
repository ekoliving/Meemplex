/*
 * @(#)LifeCycleCommand.java
 * Created on 11/12/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;


import org.eclipse.gef.commands.Command;
import org.openmaji.meem.wedge.lifecycle.LifeCycle;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

/**
 * <code>LifeCycleCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class LifeCycleCommand extends Command {
	private LifeCycle lifeCycle;
	private LifeCycleState prevState;
	private LifeCycleState state;
	
	/**
	 * Constructs an instance of <code>MeemDestroyCommand</code>.
	 * <p>
	 */
	public LifeCycleCommand(LifeCycle lifeCycle, LifeCycleState currentState, LifeCycleState state) {
		this.lifeCycle = lifeCycle;
		this.prevState = currentState;
		this.state = state;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 */
	public boolean canUndo() {
		return !state.equals(LifeCycleState.ABSENT);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		lifeCycle.changeLifeCycleState(state);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		lifeCycle.changeLifeCycleState(prevState);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
