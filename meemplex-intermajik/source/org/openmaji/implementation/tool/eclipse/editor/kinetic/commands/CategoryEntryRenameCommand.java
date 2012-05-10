/*
 * @(#)CategoryEntryRenameCommand.java
 * Created on 25/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.Category;


/**
 * <code>CategoryEntryRenameCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryEntryRenameCommand extends CategoryCommand {
	//private MeemPath meemPath;
	private String oldName;
	private String newName;
	
	/**
	 * Constructs an instance of <code>CategoryEntryRenameCommand</code>.
	 * <p>
	 * @param category
	 * @param meemPath
	 * @param oldName
	 * @param newName
	 */	
	public CategoryEntryRenameCommand(
		Category category, 
		MeemPath meemPath, 
		String oldName,
		String newName) 
	{
		super(category);
		//this.meemPath = meemPath;
		this.oldName = oldName;
		this.newName = newName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getCategory().renameEntry(oldName, newName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getCategory().renameEntry(newName, oldName);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		execute();
	}
}
