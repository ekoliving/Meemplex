/*
 * @(#)MeemCollapsibleLayoutEditPolicy.java
 * Created on 18/08/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleLayoutEditPolicy;


/**
 * <code>MeemCollapsibleLayoutEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemCollapsibleLayoutEditPolicy
	extends CollapsibleLayoutEditPolicy {
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleLayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
		 */
		protected Command getCreateCommand(CreateRequest request) {
			Command viewCommand = super.getCreateCommand(request);
			if(viewCommand == null) return null;
			
      
      /*
			Wedge wedge = (Wedge)request.getNewObject();
			WedgeAttribute wedgeAttribute = new WedgeAttribute(BinaryWedge.class.getName());
			wedge.setAttributeKey(wedgeAttribute.getKey());
			
			MetaMeem metaMeem = ((Meem)getHost().getModel()).getProxy().getMetaMeem();
			WedgeAddCommand wedgeAddCommand = new WedgeAddCommand(metaMeem, wedgeAttribute);
			*/
			return viewCommand;//.chain(wedgeAddCommand);
		}


}
