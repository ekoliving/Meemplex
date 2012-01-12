/*
 * @(#)FacetNodeEditPolicy.java
 * Created on 21/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.DependencyCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.DependencyConnection;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.definition.Scope;


/**
 * <code>FacetNodeEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetNodeEditPolicy extends GraphicalNodeEditPolicy {
	private Facet getFacetModel() {
		return (Facet)getHost().getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#createDummyConnection(org.eclipse.gef.Request)
	 */
	protected Connection createDummyConnection(Request req) {
		CreateConnectionRequest create = (CreateConnectionRequest)req;
		DependencyConnection dependency = new DependencyConnection();
		dependency.setForegroundColor(ColorConstants.white);
		
		boolean single = true;
		boolean sourceIsOutbound = true;
		
		if(create.getSourceEditPart().getModel() instanceof Facet) {
			Facet sourceFacet = (Facet)create.getSourceEditPart().getModel();
			sourceIsOutbound= sourceFacet.isOutbound();
		}
		
		if(create.getTargetEditPart() != null) {
			Object targetModel = create.getTargetEditPart().getModel();
			if(targetModel instanceof Category) {
				single = false;
			}
		}
		dependency.update(true, single, sourceIsOutbound);
		return dependency;
	}
	
	/**
	 * Overridden to create a <code>DependencyCreateCommand</code>.
	 * <p>
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Meem meem = getFacetModel().getMeem();
		if(meem == null) return null;
		
		if(!getFacetModel().getSourceConnections().isEmpty()) return null;
		
		DependencyAttribute attribute = new DependencyAttribute(DependencyType.STRONG, Scope.LOCAL, (MeemPath)null, null, null, true);
		DependencyCreateCommand command = 
			new DependencyCreateCommand(	meem.getProxy().getMetaMeem(), 
																getFacetModel().getAttributeIdentifier(), 
																attribute);
		request.setSourceEditPart(getHost());
		request.setStartCommand(command);
		return command;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		if(!(request.getSourceEditPart().getModel() instanceof Facet)) return null;
		if(!(request.getTargetEditPart().getModel() instanceof Facet)) return null;
		if(!(request.getStartCommand() instanceof DependencyCreateCommand)) return null;

		Facet sourceFacet = (Facet)request.getSourceEditPart().getModel();
		Facet targetFacet = (Facet)request.getTargetEditPart().getModel();
		if(sourceFacet.isInbound() == targetFacet.isInbound()) return null;
		
//		if(!sourceFacet.getAttribute().getInterfaceName().equals(targetFacet.getAttribute().getInterfaceName())) {
//			return null;
//		} 

		DependencyCreateCommand command = (DependencyCreateCommand)request.getStartCommand();
		command.getAttribute().setFacetIdentifier(targetFacet.getAttribute().getIdentifier());
		command.getAttribute().setMeemPath(targetFacet.getMeem().getMeemPath());
		command.getAttribute().setDependencyType(DependencyType.STRONG);
		return command;
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
