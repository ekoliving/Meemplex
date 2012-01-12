/*
 * @(#)FacetAction.java
 * Created on 18/08/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * <code>FacetAction</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetAction extends SelectionAction {

	/**
	 * Constructs an instance of <code>FacetAction</code>.
	 * <p>
	 * @param part
	 */
	public FacetAction(IWorkbenchPart part) {
		super(part);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return false;
	}

}
