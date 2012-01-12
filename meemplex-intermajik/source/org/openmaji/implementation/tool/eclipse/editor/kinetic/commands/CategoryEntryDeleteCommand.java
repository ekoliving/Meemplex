/*
 * @(#)CategoryEntryDeleteCommand.java
 * Created on 24/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;

import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryEntry;



/**
 * <code>CategoryEntryDeleteCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryEntryDeleteCommand extends CategoryCommand {
	private String entryName;
	private MeemPath meemPath;
	
	/**
	 * Constructs an instance of <code>CategoryEntryDeleteCommand</code>.
	 * <p>
	 * @param category The category when the entry is deleted from.
	 * @param entry The name of the entry to be deleted.
	 */
	public CategoryEntryDeleteCommand(Category category, CategoryEntry entry) {
		super(category);
		this.entryName = entry.getName();
		this.meemPath = entry.getMeem().getMeemPath();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		getCategory().removeEntry(entryName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getCategory().addEntry(entryName, SecurityManager.getInstance().getGateway().getMeem(meemPath));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		getCategory().removeEntry(entryName);		
	}
}
