/*
 * @(#)DependencyConnectionEditPolicy.java
 * Created on 5/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.DependencyAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.DependencyDeleteCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.DependencyUpdateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;


/**
 * <code>DependencyConnectionEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyConnectionEditPolicy extends ConnectionEditPolicy {

	protected Dependency getDependency() {
		return (Dependency)getHost().getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConnectionEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		Object type = request.getType();
		
		if(getDependency() != null) {
			Dependency dependency = getDependency();
			Meem meem = dependency.getSourceMeem();
			if(meem != null) {
				DependencyAttribute attribute = dependency.getAttribute();
			
				if(DependencyAction.REQ_WEAK.equals(type)) {
					if(getDependency().isWeak()) return null;	// already a weak dependency
	
					DependencyAttribute newAttribute = 
						(DependencyAttribute)attribute.clone();
					
					newAttribute.setDependencyType((dependency.isSingle())? 
						DependencyType.WEAK : DependencyType.WEAK_MANY);
					
					return new DependencyUpdateCommand(
						meem.getProxy().getMetaMeem(), newAttribute);
				}
				else
				if(DependencyAction.REQ_STRONG.equals(type)) {
					if(getDependency().isStrong()) return null;

					DependencyAttribute newAttribute = 
						(DependencyAttribute)attribute.clone();
					
					newAttribute.setDependencyType((dependency.isSingle())? 
						DependencyType.STRONG: DependencyType.STRONG_MANY);

					return new DependencyUpdateCommand(
						meem.getProxy().getMetaMeem(), newAttribute);
				}
			}
		}
		return super.getCommand(request);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	public boolean understandsRequest(Request req) {
		if(getDependency() == null) return false;
		
		Object type = req.getType();
		if(DependencyAction.REQ_WEAK.equals(type)) {
			return getDependency().isStrong();
		}
		else
		if(DependencyAction.REQ_STRONG.equals(type)) {
			return getDependency().isWeak();
		}
		else
		return super.understandsRequest(req);
	}

	/**
	 * Overridden to return a <code>DependencyDeleteCommand</code>.
	 * @see org.eclipse.gef.editpolicies.ConnectionEditPolicy#getDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command getDeleteCommand(GroupRequest request) {
		Dependency dependency = getDependency();
		if(dependency == null) return null;

		Facet facet = dependency.getSourceFacet();
		if(facet == null) return null;
		
		Meem meem = facet.getMeem();
		if(meem == null) return null;

		DependencyAttribute attribute = dependency.getAttribute();
		if(attribute == null) return null;

		return new DependencyDeleteCommand(
				meem.getProxy().getMetaMeem(), 
				facet.getAttributeIdentifier(), 
				attribute
			);
	}
}
