/*
 * @(#)CategoryCommand.java
 * Created on 28/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.commands;


import org.eclipse.gef.commands.Command;
import org.openmaji.system.space.Category;

/**
 * <code>CategoryCommand</code> represents the based implementation of command 
 * that uses <code>Category</code> as the target.
 * <p>
 * @see org.openmaji.system.space.Category
 * @author Kin Wong
 */
public class CategoryCommand extends Command {
	private Category category;
	
	/**
	 * Constructs an instance of <code>CategoryCommand</code>.
	 * <p>
	 */
	public CategoryCommand() {
	}

	/**
	 * Constructs an instance of <code>CategoryCommand</code>.
	 * <p>
	 * @param category The category associates with this category command.
	 */
	public CategoryCommand(Category category) {
		this.category = category;
	}
	
	/**
	 * Gets the category associates with this command.
	 * @return Category The category associates with this category command.
	 */
	public Category getCategory() {
		return category;
	}
	
	/**
	 * Sets the category associates with this command.
	 * @param category
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
}
