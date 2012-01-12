/*
 * @(#)BoundsCommand.java
 * Created on 23/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.model.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.editor.common.Messages;
import org.openmaji.implementation.tool.eclipse.editor.common.model.BoundsObject;


/**
 * @author Kin Wong
 */
public class BoundsCommand extends Command {
	static private Dimension NO_SIZING_SIZE = new Dimension(-1,-1);
	private Point newPos;
	private Dimension newSize;
	private Point oldPos;
	private Dimension oldSize;
	private BoundsObject boundsObject;
	
	/**
	 * Constructs an instance of BoundsCommand.
	 * @param boundsObject The object with bounds to be changed.
	 */
	public BoundsCommand(BoundsObject boundsObject) {
		this.boundsObject = boundsObject;
	}
	public void execute() {
		// Remember the old size and location
		oldSize = boundsObject.getSize();
		oldPos = boundsObject.getLocation();
		boundsObject.setLocation(newPos);
		if((newSize != null) && !NO_SIZING_SIZE.equals(newSize)) boundsObject.setSize(newSize);
	}

	public String getLabel() {
		if(oldSize.equals(newSize)) return Messages.BoundsCommand_Label_Location;
		return Messages.BoundsCommand_Label_Resize;
	}

	public void setLocation(Rectangle bounds) {
		setLocation(bounds.getLocation());
		setSize(bounds.getSize());
	}
	public void setLocation(Point location) {
		newPos = location;
	}
	public void setSize(Dimension size) {
		if(!size.equals(NO_SIZING_SIZE))
		newSize = size;
	}

	public void undo() {
		if(!oldSize.equals(NO_SIZING_SIZE)) boundsObject.setSize(oldSize);
		boundsObject.setLocation(oldPos);
	}
	public void redo() {
		if(!newSize.equals(NO_SIZING_SIZE)) boundsObject.setSize(newSize);
		boundsObject.setLocation(newPos);
	}
}
