/*
 * @(#)BendpointCreationHandle.java
 * Created on 8/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import org.eclipse.draw2d.Locator;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;

/**
 * <code>BendpointCreationHandle</code>.
 * <p>
 * @author Kin Wong
 */
public class BendpointCreationHandle
	extends org.eclipse.gef.handles.BendpointCreationHandle {

	/**
	 * Constructs an instance of <code>BendpointCreationHandle</code>.
	 * <p>
	 * @param owner
	 * @param index
	 */
	public BendpointCreationHandle(ConnectionEditPart owner, int index) {
		super(owner, index);
	}

	/**
	 * Constructs an instance of <code>BendpointCreationHandle</code>.
	 * <p>
	 * @param owner
	 * @param index
	 * @param locator
	 */
	public BendpointCreationHandle(
		ConnectionEditPart owner,
		int index,
		Locator locator) {
		super(owner, index, locator);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.handles.BendpointCreationHandle#createDragTracker()
	 */
	protected DragTracker createDragTracker() {
		if(isFixed()) return null;
		return super.createDragTracker();
	}
}
