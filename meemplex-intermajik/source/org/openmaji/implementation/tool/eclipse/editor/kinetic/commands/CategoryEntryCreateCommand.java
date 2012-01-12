/*
 * @(#)CategoryEntryCreateCommand.java
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



/**
 * <code>CategoryEntryCreateCommand</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryEntryCreateCommand extends CategoryCommand {
	private String entryName;
	private MeemPath meemPath;
	
	/**
	 * Constructs an instance of <code>CategoryEntryCreateCommand</code>.
	 * <p>
	 */
	public CategoryEntryCreateCommand() {
	}

	/**
	 * Constructs an instance of <code>CategoryEntryCreateCommand</code>.
	 * <p>
	 * @param category The category where the new entry is created in.
	 * @param entryName The name of this category entry.
	 * @param meemPath The meem path of meem.
	 */
	public CategoryEntryCreateCommand(Category category, String entryName, MeemPath meemPath) {
		super(category);
		this.entryName = entryName;
		this.meemPath = meemPath;
	}
	
	/**
	 * Sets the category entry name.
	 * @param entryName The category entry name.
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
	
	/**
	 * Sets the meem path of the meem.
	 * @param meemPath The meem path of the meem.
	 */
	public void setMeemPath(MeemPath meemPath) {
		this.meemPath = meemPath;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
			getCategory().addEntry(entryName, SecurityManager.getInstance().getGateway().getMeem(meemPath));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		getCategory().removeEntry(entryName);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		getCategory().addEntry(entryName, SecurityManager.getInstance().getGateway().getMeem(meemPath));
	}
}
