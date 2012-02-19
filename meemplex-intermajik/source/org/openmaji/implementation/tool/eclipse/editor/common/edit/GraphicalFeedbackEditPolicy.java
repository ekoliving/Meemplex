/*
 * @(#)GraphicalFeedbackEditPolicy.java
 * Created on 11/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.gef.requests.LocationRequest;

/**
 * <code>GraphicalFeedbackEditPolicy</code> is a graphical edit policy that
 * support multiple feedback figures in feedback layer.<p>
 * @author Kin Wong
 */
abstract public class GraphicalFeedbackEditPolicy extends GraphicalEditPolicy {
	List feedbacks;
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		removeFeedback();
		super.deactivate();
	}
	
	protected boolean isFeedbackDefined() {
		return (feedbacks != null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#showTargetFeedback(org.eclipse.gef.Request)
	 */
	public void showTargetFeedback(Request request) {
		if(!understandsRequest(request)) {
			createFeedback(request);
		}
		if(isFeedbackDefined()) {
			updateFeedback(request);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
	public void eraseTargetFeedback(Request request) {
		removeFeedback();
	}
	
	protected void removeFeedback() {
		if(feedbacks == null) return;
		for (Iterator iter = feedbacks.iterator(); iter.hasNext();) {
			removeFeedback((IFigure)iter.next());
		}
		feedbacks = null;
	}
	
	protected Point getAbsPointFromRequest(LocationRequest request) {
		GraphicalEditPart editPart = (GraphicalEditPart)getHost();
		Point point = request.getLocation().getCopy();
	
		GraphicalViewer viewer = 
		(GraphicalViewer)editPart.getRoot().getViewer();
		org.eclipse.swt.graphics.Point point2 = 
			viewer.getControl().toControl(point.x, point.y);

		point = new Point(point2.x, point2.y);
		getHostFigure().translateToRelative(point);
		return point;
	}
	
	protected Point getPrintablePointFromRequest(LocationRequest request) {
		Point point = getAbsPointFromRequest(request);
		getHostFigure().translateToRelative(point);
		return point;
	}

	
	abstract protected void createFeedback(Request request);
	abstract protected void updateFeedback(Request reqest);
}
