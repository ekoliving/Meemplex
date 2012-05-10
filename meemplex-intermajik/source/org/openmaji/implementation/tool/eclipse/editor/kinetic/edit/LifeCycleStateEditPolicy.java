/*
 * @(#)LifeCycleStateEditPolicy.java
 * Created on 3/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.ui.IEditorPart;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.RequestStateProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LifeCycleAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LifeCycleRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.LifeCycleCommand;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * <code>LifeCycleStateEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class LifeCycleStateEditPolicy 
	extends AbstractEditPolicy implements RequestStateProvider {
	private MeemClientProxy proxy;
	
	LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {}
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			if(getHost().getParent() != null)
			updateLifeCycleAction();
		}
	};
	
	/**
	 * Constructs an instance of <code>LifeCycleStateEditPolicy</code>.<p>
	 */
	public LifeCycleStateEditPolicy(MeemClientProxy proxy) {
		this.proxy = proxy;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	public void activate() {
		proxy.getLifeCycle().addClient(lifeCycleClient);
		super.activate();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		proxy.getLifeCycle().removeClient(lifeCycleClient);
		super.deactivate();
	}

	public void updateLifeCycleAction() {
		IEditorPart editorPart = EditPartHelper.getEditorPart(getHost());	
		if(editorPart == null) return;
		ActionRegistry actionRegistry = 
			(ActionRegistry)editorPart.getAdapter(ActionRegistry.class);
		if(actionRegistry == null) return;
		
		LifeCycleAction action = (LifeCycleAction)
			actionRegistry.getAction(LifeCycleAction.ID_LIFE_CYCLE_STATE);
		if(action == null) return;
		action.update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		if(request.getType().equals(LifeCycleRequest.REQ_LIFE_CYCLE)) {
			return getLifeCycleCommand((LifeCycleRequest)request);
		}
		else
		return super.getCommand(request);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	public boolean understandsRequest(Request req) {
		if(!req.getType().equals(LifeCycleRequest.REQ_LIFE_CYCLE)) return false;
		MeemClientProxy proxy = getMeemProxy();
		if(proxy == null) return false;
		if(proxy.getLifeCycle().isReadOnly()) return false;
		return true;
	}
	
	public Object getRequestState(Object type) {
		if(!type.equals(LifeCycleRequest.REQ_LIFE_CYCLE)) return null;
		MeemClientProxy proxy = getMeemProxy();
		if(proxy == null) return null;
		return proxy.getLifeCycle().getState();
	}
	
	protected Command getLifeCycleCommand(LifeCycleRequest request) {
		LifeCycleProxy lifeCycle = getMeemProxy().getLifeCycle();
		LifeCycleState currentState = lifeCycle.getState();
		return new LifeCycleCommand(
			lifeCycle, currentState,  
			request.getLifeCycleState());
	}
	
	protected MeemClientProxy getMeemProxy() {
		return proxy;
	}

}
