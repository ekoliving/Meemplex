/*
 * @(#)SimpleSelectionHandlesEditPolicy.java
 * Created on 24/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy;
import org.eclipse.gef.handles.NonResizableHandleKit;

/**
 * <code>SimpleSelectionHandlesEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class SimpleSelectionHandlesEditPolicy
	extends SelectionHandlesEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#createSelectionHandles()
	 */
	protected List createSelectionHandles() {
		List list = new ArrayList();
		NonResizableHandleKit.addHandles((GraphicalEditPart)getHost(), list);
		return list;
	}

}
