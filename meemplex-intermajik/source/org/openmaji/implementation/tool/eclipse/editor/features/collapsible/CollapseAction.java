/*
 * @(#)CollapseAction.java
 * Created on 10/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction;
import org.openmaji.implementation.tool.eclipse.editor.features.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>CollapseAction</code>.<p>
 * @author Kin Wong
 */
public class CollapseAction extends SelectionAction {
	public static String ID_COLLAPSE = CollapseAction.class + "";
	public static String REQ_COLLAPSE = ID_COLLAPSE + ".collapse";
	public static String REQ_EXPAND = ID_COLLAPSE + ".expand";
	
	static private GroupRequest collapseRequest = new GroupRequest(REQ_COLLAPSE);
	static private GroupRequest expandRequest = new GroupRequest(REQ_EXPAND);

	private boolean collapsing = false;
	
	/**
	 * Construct an instance of <code>CollapseAction</code>.
	 * @param part
	 */
	public CollapseAction(IWorkbenchPart part) {
		super(part, IAction.AS_RADIO_BUTTON);
		init();
		update();
	}
	
	/**
	 * Initializes this CollapseAction.
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#init()
	 */
	protected void init() {
		setId(ID_COLLAPSE);
		setText(Messages.CollapseModeCollapse_Label);
		setToolTipText(Messages.CollapseModeCollapse_Tooltip);
		setDescription(Messages.CollapseModeCollapse_Tooltip);
		setImageDescriptor(Images.ICON_COLLAPSE);
		setHoverImageDescriptor(Images.ICON_COLLAPSE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		Command command = createCommand();
		if(command == null) {
			setChecked(false);
			return false;
		}
		setChecked(!collapsing);
		return true;
	}
	
	public boolean run(EditPart editPart, boolean collapsing) {
		Command command = editPart.getCommand((collapsing)? collapseRequest:expandRequest);
		if(command == null) return false;
		execute(command);
		update();
		return true;
	}
	
	protected Command createCommand() {
		List selecteds = getSelectedObjectClone();
		if (selecteds.isEmpty()) return null;
		if (!(selecteds.get(0) instanceof EditPart)) return null;

		CompoundCommand collapseCompoundCommand = new CompoundCommand();
		CompoundCommand expandCompoundCommand = new CompoundCommand();
		
		for (int i = 0; i < selecteds.size(); i++) {
			EditPart editPart = (EditPart)selecteds.get(i);
			collapseCompoundCommand.add(editPart.getCommand(collapseRequest));
			expandCompoundCommand.add(editPart.getCommand(expandRequest));
		}
		
		collapsing = true;
		if(collapseCompoundCommand.isEmpty()) {
			if(expandCompoundCommand.isEmpty()) {
				return null;	// both empty so no command
			}
			else {
				// There are expand commands
				collapsing = false;	// Expanding
			}
		}
		else {
			// There are collapse commands
			if(!expandCompoundCommand.isEmpty()) {
				// Both exists, see which list is bigger.
				collapsing = 
					(collapseCompoundCommand.size() > expandCompoundCommand.size());
			}
		}
		if(collapsing)
		return collapseCompoundCommand.unwrap();
		else
		return expandCompoundCommand.unwrap();					
	}

	public void run() {
		execute(createCommand());
		update();
	}
}
