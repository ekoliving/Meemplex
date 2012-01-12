/*
 * @(#)DependencyAction.java
 * Created on 24/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import java.util.ArrayList;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.tools.ToolUtilities;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>DependencyAction</code>.
 * <p>
 * @author Kin Wong
 */
public class DependencyAction extends SelectionAction {
	static public final String ID_STRENGTH = DependencyAction.class.getName() +  ".strength";
	static public final String REQ_STRONG = DependencyAction.class.getName() + ".strong";
	static public final String REQ_WEAK = DependencyAction.class.getName() + "weak";

	static private Request requestStrong = new Request(REQ_STRONG);
	static private Request requestWeak = new Request(REQ_WEAK);
	
	boolean weak = false;

	/**
	 * Constructs an instance of <code>DependencyAction</code>.
	 * <p>
	 * @param part 
	 */
	public DependencyAction(IWorkbenchPart part) {
		super(part, IAction.AS_RADIO_BUTTON);
		setId(ID_STRENGTH);
		
		setText(Messages.DependencyAction_Weak_Label);
		setToolTipText(Messages.DependencyAction_Weak_ToolTip);
		setImageDescriptor(Images.ICON_DEPENDENCY_WEAK_SINGLE);
	}
	
	boolean isWeak() {
		return weak;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction#update()
	 */
	public void update() {
		super.update();
		
		if(isEnabled()) {
			//setChecked(false);
		}
		else {
			//setChecked(!weak);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		ArrayList editpartWeaks = getSelectedObjectClone();
		ArrayList editpartStrongs = (ArrayList)editpartWeaks.clone();
		ToolUtilities.filterEditPartsUnderstanding(editpartWeaks, requestWeak);
		ToolUtilities.filterEditPartsUnderstanding(editpartStrongs, requestStrong);
		
		if(editpartWeaks.isEmpty()) {
			if(editpartStrongs.isEmpty()) return false;
			// Making Strong dependencies
			weak = true;
			setChecked(true);
		}
		else {
			if(!editpartStrongs.isEmpty()) return false;
			// Making Weak dependencies
			weak = false;
			setChecked(false);
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		execute(getCommand());
		update();
	}
	
	private Command getCommand() {
		Request request = weak? requestStrong: requestWeak;
		ArrayList editparts = getSelectedObjectClone();
		ToolUtilities.filterEditPartsUnderstanding(editparts, request);
		
		CompoundCommand cc = new CompoundCommand();
		for (int i=0; i < editparts.size(); i++) {
			EditPart part = (EditPart)editparts.get(i);
			cc.add(part.getCommand(request));
		}
		if(cc.isEmpty()) return null;
		return cc.unwrap();
	}
}
