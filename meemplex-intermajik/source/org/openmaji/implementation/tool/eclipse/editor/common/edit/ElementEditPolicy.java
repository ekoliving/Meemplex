/*
 * @(#)ElementEditPolicy.java
 * Created on 10/03/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.DeleteCommand;


/**
 * <code>ElementEditPolicy</code> is defined to intrepret delete request of 
 * element by creating a command that removes the host model from its container.
 * <p>
 * @author Kin Wong
 */
public class ElementEditPolicy extends ComponentEditPolicy {
	/**
	 * Gets the host model as an element.
	 * <p>
	 * @return Element The host model as an element.
	 */
	protected Element getElementModel() {
		return (Element)getHost().getModel();
	}
	
	/**
	 * Overridden to return a <code>DeleteCommand</code> that removes the host 
	 * model as a from its container.
	 * <p>
	 * @see DeleteCommand
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createDeleteCommand(GroupRequest request) {
		if(getHost().getParent() == null) return null;
		ElementContainer parent = (ElementContainer)getHost().getParent().getModel();
		DeleteCommand command = new DeleteCommand(parent, getElementModel());
		return command;
	}
}
