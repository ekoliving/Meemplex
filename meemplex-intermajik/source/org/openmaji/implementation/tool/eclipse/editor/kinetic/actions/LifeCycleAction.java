/*
 * @(#)LifeCycleAction.java
 * Created on 15/12/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.ToolUtilities;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.ActionHelper;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.MenuAction;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.RequestStateProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.ConfigurationEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.KINeticEditPolicy;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>LifeCycleAction</code>.
 * <p>
 * @author Kin Wong
 */
public class LifeCycleAction extends MenuAction {
	static public final String 
		ID_LIFE_CYCLE_STATE = LifeCycleAction.class.getName();

	private LifeCycleStateAction dormantAction;
	private LifeCycleStateAction loadedAction;
	private LifeCycleStateAction readyAction;
	private LifeCycleStateAction lastAction;
	private boolean selected = false;

	static private final LifeCycleRequest REQ_LIFE_CYCLE_DORMANT 
		= new LifeCycleRequest(LifeCycleState.DORMANT);

	static private final LifeCycleRequest REQ_LIFE_CYCLE_LOADED
		= new LifeCycleRequest(LifeCycleState.LOADED);
		
	static private final LifeCycleRequest REQ_LIFE_CYCLE_READY
		= new LifeCycleRequest(LifeCycleState.READY);
	
	class LifeCycleStateAction extends Action {
		private LifeCycleRequest request;
		
		public LifeCycleStateAction(LifeCycleRequest request) {
			super("", IAction.AS_RADIO_BUTTON);
			setChecked(false);
			this.request = request;
		}
		
		public LifeCycleRequest getRequest() {
			return request;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run() {
			LifeCycleAction.this.executeCommand(getRequest());
		}
	}
	
	/**
	 * Constructs an instance of <code>LifeCycleAction</code>.
	 * <p>
	 * @param part
	 */
	public LifeCycleAction(IWorkbenchPart part) {
		super(part);
		setId(ID_LIFE_CYCLE_STATE);
		update();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.actions.MenuAction#createActions()
	 */
	protected List createActions() {
		List actions = super.createActions();

		// READY Life Cycle State Action
		readyAction = new LifeCycleStateAction(REQ_LIFE_CYCLE_READY);
		readyAction.setImageDescriptor(Images.ICON_MEEM_STATE_READY);
		readyAction.setHoverImageDescriptor(Images.ICON_MEEM_STATE_READY);
		actions.add(readyAction);

		// LOADED Life Cycle State Action
		loadedAction = new LifeCycleStateAction(REQ_LIFE_CYCLE_LOADED);
		loadedAction.setImageDescriptor(Images.ICON_MEEM_STATE_LOADED);
		loadedAction.setHoverImageDescriptor(Images.ICON_MEEM_STATE_LOADED);
		actions.add(loadedAction);

		// DORMANT Life Cycle State Action
		dormantAction = new LifeCycleStateAction(REQ_LIFE_CYCLE_DORMANT);
		dormantAction.setImageDescriptor(Images.ICON_MEEM_STATE_DORMANT);
		dormantAction.setHoverImageDescriptor(Images.ICON_MEEM_STATE_DORMANT);
		actions.add(dormantAction);

		return actions;
	}
	
	protected void executeCommand(LifeCycleRequest request) {
		List editparts = getEditParts();
		ToolUtilities.filterEditPartsUnderstanding(editparts, request);
		if(editparts.isEmpty()) return;
		//System.out.println("executeCommand -> " + request.getLifeCycleState() + ": " + editparts.size());
		execute(createCommand(editparts, request));
	}
	
	private List getEditParts() {
		List editparts = getSelectedObjectClone();
		selected = (!editparts.isEmpty());
		if(!selected) return editparts;
		
		if(editparts.size() == 1) {
			EditPart editPart = (EditPart)editparts.get(0);
			if(editPart instanceof ConfigurationEditPart) {
				editparts = new ArrayList();
				List editPartChildren = editPart.getChildren();
				Iterator it = editPartChildren.iterator();
				while(it.hasNext()) {
					Object obj = it.next();
					if(obj instanceof EditPart) {
						editparts.add(obj);
					}
				}
				selected = false;
			}
		}
		else {
			// get top level selected editparts.
			editparts = ToolUtilities.getSelectionWithoutDependants(editparts);
		}
		return editparts;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction#update()
	 */
	public void update() {
		super.update();
		
		Set states = new HashSet();
		List editparts = getEditParts();
		ToolUtilities.filterEditPartsUnderstanding(editparts, REQ_LIFE_CYCLE_READY);
				
		// Collection all the currently used states
		for (Iterator iter = editparts.iterator(); iter.hasNext();) {
			EditPart editPart = (EditPart)iter.next();
			EditPolicy policy = 
				editPart.getEditPolicy(KINeticEditPolicy.LIFE_CYCLE_STATE_ROLE);
			
			if(policy instanceof RequestStateProvider) {
				RequestStateProvider stateProvider = (RequestStateProvider)policy;
				states.add(stateProvider.getRequestState(LifeCycleRequest.REQ_LIFE_CYCLE));
			}
		}

		// Set check state for each action
		dormantAction.setChecked(states.contains(LifeCycleState.DORMANT));
		loadedAction.setChecked(states.contains(LifeCycleState.LOADED));
		readyAction.setChecked(states.contains(LifeCycleState.READY));
		dormantAction.setEnabled(true);
		loadedAction.setEnabled(true);
		readyAction.setEnabled(true);
		
		updateActionLabels();

		if(states.size() == 1) {
			// One state only
			LifeCycleState state = (LifeCycleState)states.iterator().next();
			if(state == LifeCycleState.PENDING) {
				lastAction = loadedAction;
			}
			else
			if(state == LifeCycleState.READY) {
				lastAction = loadedAction;
				readyAction.setEnabled(false);
			}
			else
			if(state == LifeCycleState.DORMANT) {
				lastAction = readyAction;
				dormantAction.setEnabled(false);
			}
			else
			if(state == LifeCycleState.LOADED) {
				lastAction = readyAction;
				loadedAction.setEnabled(false);
			}
			else {
				// ABSENT state
				lastAction = readyAction;
			}
		}
		else {
			// More than one state, only PAUSE button
			lastAction = loadedAction;
		}
		
		ActionHelper.copyDetails(lastAction, this);
		setText("");
		setEnabled(!editparts.isEmpty());
		
	}
	
	private void updateActionLabels() {
		
		if(selected) {
			dormantAction.setText(Messages.LifeCycleAction_Dormant_Label);
			dormantAction.setToolTipText(Messages.LifeCycleAction_Dormant_Label);
			loadedAction.setText(Messages.LifeCycleAction_Loaded_Label);
			loadedAction.setToolTipText(Messages.LifeCycleAction_Loaded_Label);
			readyAction.setText(Messages.LifeCycleAction_Ready_Label);
			readyAction.setToolTipText(Messages.LifeCycleAction_Ready_Label);
		}
		else {
			dormantAction.setText(Messages.LifeCycleAction_AllDormant_Label);
			dormantAction.setToolTipText(Messages.LifeCycleAction_AllDormant_Label);
			loadedAction.setText(Messages.LifeCycleAction_AllLoaded_Label);
			loadedAction.setToolTipText(Messages.LifeCycleAction_AllLoaded_Label);
			readyAction.setText(Messages.LifeCycleAction_AllReady_Label);
			readyAction.setToolTipText(Messages.LifeCycleAction_AllReady_Label);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		List editParts = getEditParts();
		return !editParts.isEmpty();
	}

	private Command createCommand(List objects, LifeCycleRequest request) {
		CompoundCommand compoundCommand = new CompoundCommand();
		Iterator it = objects.iterator();
		while(it.hasNext()) {
			EditPart editPart = (EditPart)it.next();
			compoundCommand.add(editPart.getCommand(request));
		}
		compoundCommand.unwrap();
		if(compoundCommand.isEmpty()) return null;
		return compoundCommand;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		lastAction.run();
	}

}
