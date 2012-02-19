/*
 * @(#)BendpointMoveHandle.java
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
 * <code>BendpointMoveHandle</code>.
 * <p>
 * @author Kin Wong
 */
public class BendpointMoveHandle
	extends org.eclipse.gef.handles.BendpointMoveHandle {
	/**
	 * Constructs an instance of <code>BendpointMoveHandle</code>.
	 * <p>
	 * @param owner
	 * @param index
	 */
	public BendpointMoveHandle(ConnectionEditPart owner, int index) {
		super(owner, index);
	}

	/**
	 * Constructs an instance of <code>BendpointMoveHandle</code>.
	 * <p>
	 * @param owner
	 * @param index
	 * @param locator
	 */
	public BendpointMoveHandle(
		ConnectionEditPart owner,
		int index,
		Locator locator) {
		super(owner, index, locator);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.handles.BendpointMoveHandle#createDragTracker()
	 */
	protected DragTracker createDragTracker() {
		if(isFixed()) return null;
		return super.createDragTracker();
	}

}
