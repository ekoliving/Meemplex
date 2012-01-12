/*
 * @(#)ConnectionElementEditPolicy.java
 * Created on 16/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.ConnectionCommand;


/**
 * <code>ConnectionElementEditPolicy</code> is the generic connection edit 
 * policy that issues command for <code>IConnection</code> and 
 * <code>IConnectable</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionElementEditPolicy extends ConnectionEditPolicy {
	/**
	 * @see org.eclipse.gef.editpolicies.ConnectionEditPolicy#getDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command getDeleteCommand(GroupRequest request) {
		ConnectionCommand connectionCommand = new ConnectionCommand();
		connectionCommand.setConnection((ConnectionElement)getHost().getModel());
		return connectionCommand;
	}
}
