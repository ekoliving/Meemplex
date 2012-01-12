/*
 * @(#)FacetMetaMeemEditPolicy.java
 * Created on 15/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.FacetRemoveDependencyAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.DependencyDeleteCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.FacetRemoveCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.meem.definition.DependencyAttribute;


/**
 * <code>FacetMetaMeemEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetMetaMeemEditPolicy extends ComponentEditPolicy {
	protected Facet getFacetModel() {
		return (Facet)getHost().getModel();
	}
	
	protected Meem getMeemModel() {
		return getFacetModel().getMeem();
	}
	
	protected Wedge getWedgeModel() {
		return getFacetModel().getWedge();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#getCommand(org.eclipse.gef.Request)
	 */

	public Command getCommand(Request request) {
		Object type = request.getType();
		if(type.equals(FacetRemoveDependencyAction.REQ_REMOVE_DEPENDENCY)) {
			return createDependencyDeleteCommand(request);
		}
		else
		return super.getCommand(request);
	}
	
	private boolean isEditable() {
		Meem meem = getMeemModel();
		if(meem == null) return false;	
		return meem.isLCSLoaded();
	}

	protected Command createDeleteCommand(GroupRequest request) {
		Meem meem = getMeemModel();
		Wedge wedge = getWedgeModel();
		if((meem == null) || (wedge == null)) return null;	

		if(!isEditable()) return null;
		
		return new FacetRemoveCommand(
			meem.getProxy().getMetaMeem(), 
			wedge.getAttributeIdentifier(), 
			getFacetModel().getAttribute());
	}
	
	protected Command createDependencyDeleteCommand(Request request) {
		Facet facet = getFacetModel();
		if(facet.getDependencyKey() == null) return null;
		if(!isEditable()) return null;

		Meem meem = getMeemModel();
		DependencyAttribute attribute = 
			meem.getProxy().getMetaMeem().getStructure().
			getDependencyAttribute(facet.getDependencyKey());
		
		return new DependencyDeleteCommand(
			meem.getProxy().getMetaMeem(), 
			facet.getAttributeIdentifier(), 
			attribute);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	public boolean understandsRequest(Request req) {
		Object type = req.getType();
		if(type.equals(FacetRemoveDependencyAction.REQ_REMOVE_DEPENDENCY)) {
			return isEditable() && (getFacetModel().getDependencyKey() != null);
		}
		else
		return super.understandsRequest(req);
	}
}
