/*
 * @(#)ConnectionBendpointEditPolicy.java
 * Created on 30/03/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.handles.BendpointHandle;
import org.eclipse.gef.requests.BendpointRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.BendpointCommand;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.BendpointCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.BendpointDeleteCommand;
import org.openmaji.implementation.tool.eclipse.editor.common.model.commands.BendpointMoveCommand;


/**
 * <code>ConnectionBendpointEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionBendpointEditPolicy extends BendpointEditPolicy {
	private boolean readOnly = false;
	
	public ConnectionBendpointEditPolicy(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public ConnectionBendpointEditPolicy() {
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		propertyChange(null);
	}
	
	/**
	 * @see org.eclipse.gef.editpolicies.BendpointEditPolicy#getCreateBendpointCommand(org.eclipse.gef.requests.BendpointRequest)
	 */
	protected Command getCreateBendpointCommand(BendpointRequest request) {
		Connection connection = getConnection();
		if(connection.getConnectionRouter()  instanceof ManhattanConnectionRouter)
		return null;
		
		BendpointCreateCommand createCommand = new BendpointCreateCommand();
		Point p = request.getLocation();
		connection.translateToRelative(p);
		createCommand.setLocation(p);
		
		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();
		connection.translateToRelative(ref1);
		connection.translateToRelative(ref2);
		
		createCommand.setRelativeDimensions(
			p.getDifference(ref1),
			p.getDifference(ref2));
		createCommand.setConnectionElement((ConnectionElement)request.getSource().getModel());
		createCommand.setIndex(request.getIndex());
		return createCommand;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.BendpointEditPolicy#getDeleteBendpointCommand(org.eclipse.gef.requests.BendpointRequest)
	 */
	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		Connection connection = getConnection();
		if(connection.getConnectionRouter()  instanceof ManhattanConnectionRouter)
		return null;

		BendpointCommand com = new BendpointDeleteCommand();
		Point p = request.getLocation();
		com.setLocation(p);
		com.setConnectionElement((ConnectionElement)request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.BendpointEditPolicy#getMoveBendpointCommand(org.eclipse.gef.requests.BendpointRequest)
	 */
	protected Command getMoveBendpointCommand(BendpointRequest request) {
		Connection connection = getConnection();
		if(connection.getConnectionRouter()  instanceof ManhattanConnectionRouter)
		return null;

		BendpointMoveCommand com = new BendpointMoveCommand();
		Point p = request.getLocation();
		connection.translateToRelative(p);
		com.setLocation(p);
		
		Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
		Point ref2 = getConnection().getTargetAnchor().getReferencePoint();
		connection.translateToRelative(ref1);
		connection.translateToRelative(ref2);
		
		com.setRelativeDimensions(p.getDifference(ref1),
						p.getDifference(ref2));
		com.setConnectionElement((ConnectionElement)request.getSource().getModel());
		com.setIndex(request.getIndex());
		return com;
	}

	private boolean isAutomaticallyBending() {
		List constraint = (List)getConnection().getRoutingConstraint();
		PointList points = getConnection().getPoints();
		boolean autoBending = 
			((points.size() > 2) && (constraint == null || constraint.isEmpty()));
		return autoBending;
	}

	protected List createSelectionHandles() {
		List list = new ArrayList();
		if (isAutomaticallyBending())
			list = createHandlesForAutomaticBendpoints();
		else
			list = createHandlesForUserBendpoints();
		return list;
	}


	private List createHandlesForAutomaticBendpoints() {
		List list = new ArrayList();
		ConnectionEditPart connEP = (ConnectionEditPart)getHost();
		PointList points = getConnection().getPoints();
		for (int i = 0; i < points.size() - 2; i++) {
			BendpointHandle handle = new BendpointMoveHandle(connEP, i);
			handle.setFixed(true);
			list.add(handle);
		}
		return list;
	}

	private List createHandlesForUserBendpoints() {
		List list = new ArrayList();
		ConnectionEditPart connEP = (ConnectionEditPart)getHost();
		PointList points = getConnection().getPoints();
		BendpointCreationHandle creationHandle = null;
		BendpointMoveHandle moveHandle = null;
		
		for (int i = 0; i < points.size() - 2; i++) {
			creationHandle = new BendpointCreationHandle(connEP, i);
			moveHandle = new BendpointMoveHandle(connEP, i);
			if(isReadOnly()) {
				creationHandle.setFixed(true);
				moveHandle.setFixed(true);
			}
			list.add(creationHandle);
			list.add(moveHandle);
		}
		creationHandle = new BendpointCreationHandle(connEP, points.size() - 2);
		if(isReadOnly()) creationHandle.setFixed(true);
		list.add(creationHandle);
		return list;
	}
}
