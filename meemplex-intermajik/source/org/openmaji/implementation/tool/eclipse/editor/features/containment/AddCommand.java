/*
 * @(#)AddCommand.java
 * Created on 7/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.containment;

import org.eclipse.gef.commands.Command;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;


/**
 * @author Kin Wong
 */
public class AddCommand extends Command {
	private Element child;
	private ElementContainer exParent;
	private int exIndex = -1;
	private ElementContainer  parent;
	private int index = -1;

	public AddCommand(ElementContainer parent, Element child) {
		this.parent = parent;
		this.child = child;
	}

	public AddCommand(ElementContainer parent, Element child, int index) {
		this.parent = parent;
		this.child = child;
		this.index = index;
	}

	public void execute() {
		exParent = child.getParent();
		if(exParent != null) {
			exIndex = exParent.getChildren().indexOf(child);
		}
		
		if(index == -1) index = parent.getChildren().size();
		parent.addChild(index, child);
	}
	
	public ElementContainer getParent() {
		return parent;
	}
	
	public void undo() {
		parent.removeChild(child);
		if(exParent != null) {
			exParent.addChild(exIndex, child);
		}
	}
	
	public void redo() {
		parent.addChild(index, child);
	}
	
}
