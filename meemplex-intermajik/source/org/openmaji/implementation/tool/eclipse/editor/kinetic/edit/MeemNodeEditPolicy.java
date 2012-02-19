/*
 * @(#)MeemNodeEditPolicy.java
 * Created on 21/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.EntryOfConnection;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;


/**
 * <code>MeemNodeEditPolicy</code> provides the functionality to create a 
 * category entry from the meem model to a category.
 * <p>
 * @author Kin Wong
 */
public class MeemNodeEditPolicy extends GraphicalNodeEditPolicy {
	protected Meem getMeemModel() {
		return (Meem)getHost().getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#createDummyConnection(org.eclipse.gef.Request)
	 */
	protected Connection createDummyConnection(Request req) {
		EntryOfConnection entryOf = new EntryOfConnection();
		entryOf.setForegroundColor(ColorConstants.white);
		return entryOf;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Meem meem = getMeemModel();
		CategoryEntryCreateCommand command = new CategoryEntryCreateCommand();
		command.setMeemPath(meem.getMeemPath());
		
		request.setStartCommand(command);
		request.setSourceEditPart(getHost());
		return command;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}
}
