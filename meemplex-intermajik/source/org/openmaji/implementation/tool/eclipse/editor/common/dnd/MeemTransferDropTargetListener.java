/*
 * @(#)MeemTransferDropTargetListener.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.editor.common.dnd;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.MeemCloneRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.MeemDropRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.NamedMeemRequest;
import org.openmaji.implementation.tool.eclipse.ui.dnd.NamedMeemTransfer;


/**
 * <code>MeemTransferDropTargetListener</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemTransferDropTargetListener 
	extends AbstractTransferDropTargetListener {
	/**
	 * Constructs an instance of <code>MeemTransferDropTargetListener</code>.
	 * <p>
	 * @param viewer
	 * @param transfer
	 */
	public MeemTransferDropTargetListener(EditPartViewer viewer, NamedMeemTransfer transfer) {
		super(viewer, transfer);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#createTargetRequest()
	 */
	protected Request createTargetRequest() {
		DropTargetEvent event = getCurrentEvent();
		if(event.currentDataType.type == NamedMeemTransfer.TYPE_CLONE)
		return new MeemCloneRequest();
		return new MeemDropRequest();
	}

	protected NamedMeemRequest getNamedMeemRequest() {
		return (NamedMeemRequest)getTargetRequest();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#isEnabled(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public boolean isEnabled(DropTargetEvent event) {
		boolean enabled = super.isEnabled(event);
		if(!enabled) return false;
		return enabled;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#handleDragOperationChanged()
	 */
	protected void handleDragOperationChanged() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOperationChanged();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#handleDragOver()
	 */
	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		getCurrentEvent().feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
		super.handleDragOver();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#updateTargetRequest()
	 */
	protected void updateTargetRequest() {
		getNamedMeemRequest().setLocation(new Point(getCurrentEvent().x, getCurrentEvent().y));
		getNamedMeemRequest().setData(getCurrentEvent().data);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
	 */
	public void dragEnter(DropTargetEvent event) {
		super.dragEnter(event);
		getCurrentEvent().detail = DND.DROP_COPY;		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#setCurrentEvent(org.eclipse.swt.dnd.DropTargetEvent)
	 */

	protected void handleDrop() {
		super.handleDrop();
		selectAddedObjects();
	}

	private void selectAddedObjects() {
		List models = getNamedMeemRequest().getNewObjects();
		if (models.isEmpty()) return;
		
		EditPartViewer viewer = getViewer();
		viewer.getControl().forceFocus();
	
		viewer.flush();
		viewer.deselectAll();
		for (Iterator iter = models.iterator(); iter.hasNext();) {
			Object editpart = viewer.getEditPartRegistry().get(iter.next());
			if (editpart instanceof EditPart) {
				//Force a layout first.
				viewer.appendSelection((EditPart)editpart);
			}
		}
	}
}
