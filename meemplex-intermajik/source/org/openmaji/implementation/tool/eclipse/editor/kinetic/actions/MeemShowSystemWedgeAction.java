/*
 * @(#)MeemShowSystemWedgeAction.java
 * Created on 7/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.action.IAction;

import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>MeemShowSystemWedgeAction</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemShowSystemWedgeAction extends SelectionAction {
	static public String ID_SHOW_SYSTEM_WEDGES = MeemShowSystemWedgeAction.class + ".ShowSystemWedges";
	static public String REQ_SHOW = MeemShowSystemWedgeAction.class + ".show";
	static public String REQ_HIDE = MeemShowSystemWedgeAction.class + ".hide";

	static private GroupRequest showRequest = new GroupRequest(REQ_SHOW);
	static private GroupRequest hideRequest = new GroupRequest(REQ_HIDE);
	
	private boolean showing = false;

	/**
	 * Constructs an instance of <code>MeemShowSystemWedgeAction</code>.
	 * <p>
	 * @param part
	 */
	public MeemShowSystemWedgeAction(IWorkbenchPart part) {
		super(part, IAction.AS_RADIO_BUTTON);
		setId(ID_SHOW_SYSTEM_WEDGES);
		setText(Messages.MeemAction_ShowSystemWedges_Label);
		setToolTipText(Messages.MeemAction_ShowSystemWedges_Label);
		setDescription(Messages.MeemAction_ShowSystemWedges_Description);
		setImageDescriptor(Images.ICON_SHOW_SYSTEM_WEDGES);
		setHoverImageDescriptor(getImageDescriptor());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		if(createCommand() == null) {
			setChecked(false);
			return false;
		} 
		setChecked(!showing);
		return true;
	}

	private Command createCommand() {
		List objects = getSelectedObjects();
		if (objects.isEmpty()) return null;
		if (!(objects.get(0) instanceof EditPart)) return null;
		
		CompoundCommand showCompoundCommand = new CompoundCommand();
		CompoundCommand hideCompoundCommand = new CompoundCommand();
		
		for (int i = 0; i < objects.size(); i++) {
			EditPart editPart = (EditPart)objects.get(i);
			showCompoundCommand.add(editPart.getCommand(showRequest));
			hideCompoundCommand.add(editPart.getCommand(hideRequest));
			if((!showCompoundCommand.isEmpty()) && (!hideCompoundCommand.isEmpty()))
			return null;
		}
		
		if(!showCompoundCommand.isEmpty()) {
			// Showing meem
			showing = true;
			return showCompoundCommand.unwrap();
		}
		else
		if(!hideCompoundCommand.isEmpty()) {
			showing = false;
			return hideCompoundCommand.unwrap();			
		}
		else
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		execute(createCommand());
		update();
	}
}
