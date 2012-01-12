/*
 * @(#)ElementXYLayoutEditPolicy.java
 * Created on 26/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.model.BoundsObject;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.BoundsCommand;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.CreateCommand;


/**
 * @author Kin Wong
 */
public class ElementXYLayoutEditPolicy extends XYLayoutEditPolicy {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createAddCommand(EditPart child, Object constraint) {
		BoundsObject boundsObject = (BoundsObject)child.getModel();
		ElementContainer parent = (ElementContainer)getHost().getModel();
		if(!parent.isValidNewChild(boundsObject)) return null;

		Rectangle bounds = ((Rectangle)constraint).getCopy();
		bounds.setSize(-1,-1);
		return new CreateCommand(parent, boundsObject, bounds);
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	protected Command createChangeConstraintCommand(
		EditPart child,
		Object constraint) {
			BoundsObject boundsObject = (BoundsObject)child.getModel();
			BoundsCommand boundsCommand = new BoundsCommand(boundsObject);
			boundsCommand.setLocation((Rectangle)constraint);
			return boundsCommand;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		return new NonResizableEditPolicy();
	}
	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		ElementContainer parent = (ElementContainer)getHost().getModel();
		Object newObject = request.getNewObject();
		if(!(newObject instanceof BoundsObject)) return null;
		if(!parent.isValidNewChild(newObject)) return null;

		Rectangle bounds = (Rectangle)getConstraintFor(request);
		BoundsObject boundsObject = (BoundsObject)newObject;
		boundsObject.setBounds(bounds);
		return new CreateCommand(parent, (BoundsObject)newObject, bounds.getCopy());
	}
	
	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}
/* (non-Javadoc)
 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#getConstraintFor(org.eclipse.gef.requests.CreateRequest)
 */
protected Object getConstraintFor(CreateRequest request) {
	return super.getConstraintFor(request);
}

}
