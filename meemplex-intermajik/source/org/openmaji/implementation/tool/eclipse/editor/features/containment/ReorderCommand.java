/*
 * @(#)ReorderCommand.java
 * Created on 14/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.containment;

import org.eclipse.gef.commands.Command;

/**
 * <code>ReorderCommand</code> provides the ability to reorder the index of a
 * child object in a container that implements <code>IModelContainer</code>.
 * <p>
 * @author Kin Wong
 */
public class ReorderCommand extends Command {
	private int oldIndex;
	private int newIndex;
	private IModelContainer parent;

	/**
	 * Constructs an instance of <code>ReorderCommand</code>.
	 * <p>
	 * @param parent The parent container.
	 * @param oldIndex The current index of the child.
	 * @param newIndex The new index of the child.
	 */
	public ReorderCommand(IModelContainer parent, int oldIndex, int newIndex) {
		this.parent = parent;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		parent.moveChild(oldIndex, newIndex);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		parent.moveChild(newIndex, oldIndex);
	}

	/**
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
