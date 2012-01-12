/*
 * @(#)CreateCommand.java
 * Created on 29/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.model.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.editor.common.model.BoundsObject;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;


/**
 * <code>CreateCommand</code> represents the logic of adding a newly create 
 * element to a container.
 * <p>
 * @author Kin Wong
 */
public class CreateCommand extends Command {
	protected BoundsObject child;
	protected Rectangle bounds;
	protected ElementContainer container;
	protected int index = -1;

	/**
	 * Constructs an instance of <code>AddCommand</code>.
	 * <p>
	 * @param container The container of the child object.
	 * @param child The child object to be added.
	 * @param location The bounds of this newly created object.
	 */
	public CreateCommand(
		ElementContainer container, BoundsObject child, Rectangle location) {
		this.container = container;
		this.child = child;
		this.bounds = location;
	}
	
	public void setIndex( int index ){
		this.index = index;
	}
	
	public BoundsObject getChild() {
		return child;
	}

	public ElementContainer getParent() {
		return container;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (bounds != null) {
			child.setLocation(bounds.getLocation());
			if (!bounds.isEmpty())
				child.setSize(bounds.getSize());
		}
		if( index < 0 )
		index = container.getChildren().size();
		container.addChild(index, child);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		container.removeChild(child);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		if (bounds != null) {
			child.setLocation(bounds.getLocation());
			child.setSize    (bounds.getSize());
		}
		container.addChild(index, child);
	}
}
