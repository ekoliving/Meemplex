/*
 * @(#)CollapsibleLayoutEditPolicy.java
 * Created on 14/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.AddCommand;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.ReorderCommand;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.ToolbarLayoutEditPolicy;


/**
 * <code>CollapsibleLayoutEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class CollapsibleLayoutEditPolicy extends ToolbarLayoutEditPolicy {
	/**
	 * @see org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createAddCommand(EditPart child, EditPart after) {
		IModelContainer container = (IModelContainer)getHost().getModel();
		int index = (after == null)? 0 : container.childIndexOf(after.getModel());
		Element element = (Element)child.getModel();
		if(!container.isValidNewChild(element)) {
			// New Object is invalid in this container
			getLineFeedback().setForegroundColor(ColorConstants.red);
			return null;			
		}
		getLineFeedback().setForegroundColor(ColorConstants.white);
		return new AddCommand((ElementContainer)container, element, index);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createMoveChildCommand(org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
//		Object childObject = child.getModel();
		IModelContainer parent = (IModelContainer)getHost().getModel();
		
		int oldIndex = getHost().getChildren().indexOf(child);
		int newIndex = getHost().getChildren().indexOf(after);

		if(oldIndex == (newIndex - 1)) {
			getLineFeedback().setForegroundColor(ColorConstants.red);
			return null;
		}
		else
		getLineFeedback().setForegroundColor(ColorConstants.white);
		return new ReorderCommand(parent, oldIndex, newIndex);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		Object newChild = request.getNewObject();
		IModelContainer parent = (IModelContainer)getHost().getModel();
		if(!parent.isValidNewChild(newChild)) {
			getLineFeedback().setForegroundColor(ColorConstants.red);
			 return null; 
		} 
		getLineFeedback().setForegroundColor(ColorConstants.white);
		return new AddCommand((ElementContainer)parent, (Element)newChild);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

}
