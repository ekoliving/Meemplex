/*
 * @(#)MeemEditPolicy.java
 * Created on 5/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleProxy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.MeemAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.MeemShowSystemWedgeAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryDeleteCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.LifeCycleCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.ShowSystemWedgesCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryEntry;


/**
 * <code>MeemEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemEditPolicy extends ComponentEditPolicy {
	
	protected Meem getMeem() {
		return (Meem)getHost().getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		if(MeemAction.REQ_REMOVE.equals(request.getType())) {
			return createRemoveCommand((GroupRequest)request);
		}
		else
		if(MeemAction.REQ_DESTROY.equals(request.getType())) {
			return createDestroyCommand((GroupRequest)request);
		}
		else
		if(MeemShowSystemWedgeAction.REQ_SHOW.equals(request.getType())) {
			if(getMeem().isSystemWedgeShown()) return null;
			return createShowSystemWedgesCommand((GroupRequest)request);
		}
		else
		if(MeemShowSystemWedgeAction.REQ_HIDE.equals(request.getType())) {
			if(!getMeem().isSystemWedgeShown()) return null;
			return createHideSystemWedgesCommand((GroupRequest)request);
		}
		else
		return super.getCommand(request);
	}

	protected Category getCategory() {
		Diagram diagram = getMeem().getDiagram();
		if(diagram == null) return null;
		return diagram.getCategory();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command createRemoveCommand(GroupRequest request) {
		Category category = getCategory();
		if(category == null) return null;

		CategoryEntry entry = 
			new CategoryEntry(getMeem().getName(), getMeem().getProxy());
		return new CategoryEntryDeleteCommand(category, entry);
	}
	
	/**
	 * Creates command that permanently destroy the meem.
	 * @param request
	 * @return Command
	 */
	protected Command createDestroyCommand(GroupRequest request) {
		Command deleteCommand = createRemoveCommand(request);
		if(deleteCommand == null) return null;
		
		CompoundCommand command = new CompoundCommand();
		command.add(deleteCommand);
		
		LifeCycleProxy lifeCycle = getMeem().getProxy().getLifeCycle();
		command.add(
			new LifeCycleCommand(lifeCycle, lifeCycle.getState(), 
			LifeCycleState.ABSENT));
		
		return command;
	}
	
	protected Command createShowSystemWedgesCommand(GroupRequest request) {
		if(getMeem() == null) return null;
		if(getMeem().isSystemWedgeShown()) return null;
		return new ShowSystemWedgesCommand(getMeem(), true);
	}

	protected Command createHideSystemWedgesCommand(GroupRequest request) {
		if(getMeem() == null) return null;
		if(!getMeem().isSystemWedgeShown()) return null;
		return new ShowSystemWedgesCommand(getMeem(), false);
	}
}
