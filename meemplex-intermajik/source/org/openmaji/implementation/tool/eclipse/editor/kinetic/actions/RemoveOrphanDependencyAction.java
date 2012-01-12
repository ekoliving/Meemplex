/*
 * @(#)RemoveOrphanDependencyAction.java
 * Created on 16/06/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.SelectionAction;


/**
 * <code>RemoveOrphanDependencyAction</code>.<p>
 * @author Kin Wong
 */
public class RemoveOrphanDependencyAction extends SelectionAction {

	/**
	 * Constructs an instance of <code>RemoveOrphanDependencyAction</code>.<p>
	 * @param part
	 */
	public RemoveOrphanDependencyAction(IWorkbenchPart part) {
		super(part);
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.actions.WorkbenchPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		super.run();
	}

}
