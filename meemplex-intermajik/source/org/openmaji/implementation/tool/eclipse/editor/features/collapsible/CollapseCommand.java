/*
 * @(#)CollapseCommand.java
 * Created on 16/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.util.Assert;


/**
 * <code>CollapseCommand</code> works with <code>ICollapsible</code> to 
 * collapse and expand it.<p>
 * @author Kin Wong
 */
public class CollapseCommand extends Command {
	private ICollapsible collapsible;
	private boolean collapsed;
	private boolean collapsing;

	/**
	 * Constructs an instance of <code>CollapseCommand</code> with the 
	 * collapsible and whether the collapsible should be collapsing.<p>
	 * @param collapsible The collapsible object this command is executed upon.
	 * @param collapsing Whether the object is collapsing.
	 */
	public CollapseCommand(ICollapsible collapsible, boolean collapsing) {
		Assert.isNotNull(collapsible);
		setCollapsible(collapsible);
		this.collapsing = collapsing;	
	}
	
	/**
	 * Gets the collapsible object.<p>
	 * @return ICollapsible The collapsible object this command is executed 
	 * upon.
	 */
	public ICollapsible getCollapsible() {
		return collapsible;
	}
	
	/**
	 * Sets the collapsible object.<p>
	 * @param collapsible The collapsible object this command is executed upon.
	 */
	public void setCollapsible(ICollapsible collapsible) {
		this.collapsible = collapsible;
	}
	
	/**
	 * Sets whether the collapsible should be collapsed.<p>
	 * @param collapsing true is the collapsible should be collapsed, 
	 * false otherwise.
	 */
	public void setCollapsing(boolean collapsing) {
		this.collapsing = collapsing;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		collapsed = collapsible.isCollapsed();
		collapsible.setCollapse(collapsing);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		collapsible.setCollapse(collapsed);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		collapsible.setCollapse(collapsing);
	}
}
