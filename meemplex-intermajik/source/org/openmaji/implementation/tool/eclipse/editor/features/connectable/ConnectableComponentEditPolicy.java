/*
 * @(#)ConnectableComponentEditPolicy.java
 * Created on 23/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;


/**
 * <code>ConnectableComponentEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectableComponentEditPolicy extends ComponentEditPolicy {
	private GroupRequest deleteReq = new GroupRequest(RequestConstants.REQ_DELETE);
	
	/**
	 * Constructs an instance of <code>ConnectableComponentEditPolicy</code>.
	 * <p>
	 */
	public ConnectableComponentEditPolicy() {
	}
	
	/**
	 * Override to contribute to the component's being deleted. DELETE will also 
	 * be sent to the parent. DELETE must be handled by either the child or the 
	 * parent, or both.
	 * @param deleteRequest the DeleteRequest
	 * @return Command <code>null</code> or a contribution to the delete
	 */
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		CompoundCommand command = new CompoundCommand();
		createSourceConnectionDeleteCommands(command);
		createTargetConnectionDeleteCommands(command);
		createChildrenDeleteCommands(command);
		if(command.size() > 0) return command;
		return null;
	}
	
	protected void createChildrenDeleteCommands(CompoundCommand command) {
		Iterator it = getHost().getChildren().iterator();
		while(it.hasNext()) {
			EditPart editPart = (EditPart)it.next();
			command.add(editPart.getCommand(deleteReq));
		}
	}
	
	protected void createSourceConnectionDeleteCommands(CompoundCommand command) {
		if(getHost() == null) return;
		List connections = ((GraphicalEditPart)getHost()).getSourceConnections();
		createConnectionDeleteCommands(command, connections);
	}
	protected void createTargetConnectionDeleteCommands(CompoundCommand command) {
		if(getHost() == null) return;
		List connections = ((GraphicalEditPart)getHost()).getTargetConnections();
		createConnectionDeleteCommands(command, connections);
	}
	protected void createConnectionDeleteCommands(	CompoundCommand command, 
													List connections) {
		if(getHost().getParent() == null) return;
		if(getHost().getRoot().getViewer() == null) return;
		
		Map registry = getHost().getRoot().getViewer().getEditPartRegistry();
		if(registry == null) return;
		Iterator it = connections.iterator();
		while(it.hasNext()) {
			EditPart editPart = (EditPart)it.next();
			if((editPart != null) && (editPart instanceof ConnectionEditPart)) {
				command.add(editPart.getCommand(deleteReq));
			}
		}
	}
}
