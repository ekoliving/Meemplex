/*
 * @(#)MeemAction.java
 * Created on 29/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.WorkbenchImages;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>MeemAction</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemAction extends SelectionAction {
	static public String ID_REMOVE = MeemAction.class + ".Remove";
	static public String ID_DESTROY = MeemAction.class + ".Destroy";
	static public String REQ_DESTROY = ID_DESTROY;
	static public String REQ_REMOVE = ID_REMOVE;
	
	Object requestType;
	/**
	 * Constructs an instance of <code>MeemAction</code>.
	 * <p>
	 * @param part
	 * @param id
	 */
public MeemAction(IWorkbenchPart part, String id) {
	super(part);

		if(ID_REMOVE.equals(id))	{
			requestType = REQ_REMOVE;
			setId(ID_REMOVE);
			setText(Messages.MeemAction_Remove_Label);
			setToolTipText(Messages.MeemAction_Remove_Label);
			setDescription(Messages.MeemAction_Remove_Description);

			setHoverImageDescriptor(
				WorkbenchImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));

			setImageDescriptor(
				WorkbenchImages.getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));
	}
		else
		if(ID_DESTROY.equals(id))	{
			requestType = REQ_DESTROY;
			setId(ID_DESTROY);
			setText(Messages.MeemAction_Destroy_Label);
			setToolTipText(Messages.MeemAction_Destroy_Label);
			setDescription(Messages.MeemAction_Destroy_Description);
			setImageDescriptor(Images.ICON_MEEM_STATE_ABSENT);
			setHoverImageDescriptor(getImageDescriptor());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		Command command = createCommand(getSelectedObjects());
		if(command == null) return false;
		return command.canExecute();
	}

	private Command createCommand(List objects) {
		if (objects.isEmpty()) return null;
		if (!(objects.get(0) instanceof EditPart)) return null;
		
		GroupRequest request = new GroupRequest(requestType);
		CompoundCommand compoundCommand = new CompoundCommand();
		for (int i = 0; i < objects.size(); i++) {
			EditPart editPart = (EditPart)objects.get(i);
			compoundCommand.add(editPart.getCommand(request));
		}
		if(compoundCommand.isEmpty()) return null;
		return compoundCommand.unwrap();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		execute(createCommand(getSelectedObjects()));
	}
}
