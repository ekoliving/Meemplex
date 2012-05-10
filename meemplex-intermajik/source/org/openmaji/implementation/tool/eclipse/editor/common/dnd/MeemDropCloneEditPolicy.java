/*
 * @(#)MeemDropCloneEditPolicy.java
 * Created on 30/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.dnd;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.gef.requests.LocationRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.MeemCloneRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.MeemDropRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.requests.NamedMeemRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart;


/**
 * <code>MeemDropCloneEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class MeemDropCloneEditPolicy extends GraphicalEditPolicy {
	private IFigure[] feedbacks;
	Dimension LOCATION_DELTA_SIZE = new Dimension(
		MeemIconicEditPart.ICON_SIZE.width >> 1, 
		MeemIconicEditPart.ICON_SIZE.height >> 1);

	/**
	 * Overridden to intrepret <code>MeemDropRequest</code> and 
	 * <code>MeemCloneRequest</code>.
	 * @see org.eclipse.gef.EditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		if (MeemDropRequest.REQ_MEEM_DROP.equals(request.getType()))
			return getNamedMeemCommand((NamedMeemRequest) request);
		else
		if (MeemCloneRequest.REQ_MEEM_CLONE.equals(request.getType()))
			return getNamedMeemCommand((NamedMeemRequest) request);
		else
		return super.getCommand(request);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#showTargetFeedback(org.eclipse.gef.Request)
	 */
	public void showTargetFeedback(Request request) {
		Object type = request.getType();
		
		if( (!MeemDropRequest.REQ_MEEM_DROP.equals(type)) && 
				(!MeemCloneRequest.REQ_MEEM_CLONE.equals(type)))
			return;
			
		NamedMeemRequest namedMeemRequest = (NamedMeemRequest)request;
		if (feedbacks == null) {
			// Use a ghost rectangle for feedback
			feedbacks = new IFigure[namedMeemRequest.getSize()];
			for(int i = 0; i < feedbacks.length; i++) {
				RectangleFigure r = new RectangleFigure();
				FigureUtilities.makeGhostShape(r);
				r.setLineStyle(Graphics.LINE_DASHDOT);
				r.setForegroundColor(ColorConstants.white);
				r.setSize(MeemIconicEditPart.ICON_SIZE);
				addFeedback(r);
				feedbacks[i] = r;
			}
		}
		
		if(feedbacks != null) {
			Point point = getPointFromRequest(namedMeemRequest);
			getHostFigure().translateToAbsolute(point);
			getLayer(LayerConstants.FEEDBACK_LAYER).translateToRelative(point);
			
			for(int i = 0; i < feedbacks.length; i++) {
				feedbacks[i].setLocation(point);
				point = point.getTranslated(LOCATION_DELTA_SIZE);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
	public void eraseTargetFeedback(Request request) {
		if(feedbacks != null) {
			for(int i = 0; i < feedbacks.length; i++) {
				removeFeedback(feedbacks[i]);
			}
			feedbacks = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		eraseTargetFeedback(null);
		super.deactivate();
	}

	/**
	 * 
	 * @param request
	 * @return Command
	 */
	abstract protected Command getNamedMeemCommand(NamedMeemRequest request);
	
	protected Point getPointFromRequest(LocationRequest request) {
		GraphicalEditPart editPart = (GraphicalEditPart) getHost();
		Point point = request.getLocation().getCopy();
	
		GraphicalViewer viewer = (GraphicalViewer) editPart.getRoot().getViewer();
		org.eclipse.swt.graphics.Point point2 = 
			viewer.getControl().toControl(point.x, point.y);

		point = new Point(point2.x, point2.y);
		getHostFigure().translateToRelative(point);
		return point;
	}
	
	/**
	 * Overridden to check if the request is of type 
	 * <code>MeemDropRequest</code> and return the host editpart if so.
	 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	public EditPart getTargetEditPart(Request request) {
		if ( 	(MeemDropRequest.REQ_MEEM_DROP.equals(request.getType())) || 
					(MeemCloneRequest.REQ_MEEM_CLONE.equals(request.getType())) )
			return getHost();
		return super.getTargetEditPart(request);
	}
}
