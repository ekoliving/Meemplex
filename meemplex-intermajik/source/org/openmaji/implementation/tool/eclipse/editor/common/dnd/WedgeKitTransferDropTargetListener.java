/*
 * @(#)WedgeKitTransferDropTargetListener.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.dnd;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.WedgeAddRequest;


/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class WedgeKitTransferDropTargetListener extends AbstractTransferDropTargetListener {
    /* TODO[ben] rename class to ToolkitWedgeTransferDropTargetListener */
	public WedgeKitTransferDropTargetListener(EditPartViewer viewer, Transfer transfer) {
		super(viewer, transfer);
	}

	protected Request createTargetRequest() {
		return new WedgeAddRequest();
	}

	protected WedgeAddRequest getWedgeAddRequest() {
		return (WedgeAddRequest) getTargetRequest();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public boolean isEnabled(DropTargetEvent event) {
		boolean enabled = super.isEnabled(event);
		if(!enabled) return false;
		event.detail = DND.DROP_COPY;
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#updateTargetRequest()
	 */
	protected void updateTargetRequest() {
		getWedgeAddRequest().setLocation(new Point(getCurrentEvent().x, getCurrentEvent().y));
		getWedgeAddRequest().setData(getCurrentEvent().data);
	}

}
