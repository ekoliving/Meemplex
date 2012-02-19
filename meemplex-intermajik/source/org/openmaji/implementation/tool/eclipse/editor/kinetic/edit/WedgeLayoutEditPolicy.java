/*
 * @(#)WedgeLayoutEditPolicy.java
 * Created on 10/06/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleLayoutEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.AddCommand;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.IModelContainer;


/**
 * <code>WedgeLayoutEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeLayoutEditPolicy extends CollapsibleLayoutEditPolicy {
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleLayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
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

}
