/*
 * @(#)WedgeMetaMeemEditPolicy.java
 * Created on 15/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.WedgeRemoveCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;


/**
 * <code>WedgeMetaMeemEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeMetaMeemEditPolicy extends ComponentEditPolicy {
	protected Wedge getWedgeModel() {
		return (Wedge)getHost().getModel();
	}
	
	protected Meem getMeemModel() {
		return getWedgeModel().getMeem();
	}

	protected Command createDeleteCommand(GroupRequest request) {
		Meem meem = getMeemModel();
		if(meem == null) return null;	// No meem is associated with this Wedge.
		if(!meem.isLCSLoaded()) return null;
		
		return new WedgeRemoveCommand(
			meem.getProxy().getMetaMeem(), getWedgeModel().getAttribute());
	}
}
