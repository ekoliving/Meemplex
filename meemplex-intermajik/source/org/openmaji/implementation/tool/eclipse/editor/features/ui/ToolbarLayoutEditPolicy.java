/*
 * @(#)ToolbarLayoutEditPolicy.java
 * Created on 14/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Transposer;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

/**
 * The <code>ToolbarLayoutEditPolicy</code> is an EditPolicy for use with
 * {@link org.eclipse.draw2d.ToolbarLayout}. It understands how to map an 
 * <x,y> coordinate on the layout container to the appropriate index for the 
 * operation being performed. It also shows target feedback consisting of an 
 * insertion line at the appropriate location.
 * <p>
 * @author Kin Wong
 */
abstract public class ToolbarLayoutEditPolicy extends FlowLayoutEditPolicy {
	static private int CURSOR_SIZE = 5;
	/**
	 * Gets the content pane of the editpart. 
	 * @return Figure The content pane of the editpart.
	 */
	protected IFigure getContentPane() {
		return ((GraphicalEditPart)getHost()).getContentPane();
	}
	/**
	 * Overridden to return the whether the <code>ToolbarLayout</code> of the 
	 * content pane of the editpart is horizontally oriented.
	 * @return boolean true if the <code>ToolbarLayout</code> of the 
	 * content pane is horizontally oriented, false otherwise.
	 * @see org.eclipse.gef.editpolicies.FlowLayoutEditPolicy#isHorizontal()
	 */
	protected boolean isHorizontal() {
		return ((ToolbarLayout)getContentPane().getLayoutManager()).isHorizontal();
	}
	private Rectangle getAbsoluteBounds(GraphicalEditPart editPart) {
		Rectangle bounds = editPart.getFigure().getBounds().getCopy();
		editPart.getFigure().translateToAbsolute(bounds);
		return bounds;
	}
	private Point getLocationFromRequest(Request request) {
		return ((DropRequest)request).getLocation();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.FlowLayoutEditPolicy#showLayoutTargetFeedback(org.eclipse.gef.Request)
	 */
	protected void showLayoutTargetFeedback(Request request) {
		if (getHost().getChildren().size() == 0)
			return;
		Polyline fb = getLineFeedback();
		Transposer transposer = new Transposer();
		transposer.setEnabled(!isHorizontal());
	
		boolean before = true;
		int epIndex = getFeedbackIndexFor(request);
		Rectangle r = null;
		if (epIndex == -1) {
			before = false;
			epIndex = getHost().getChildren().size() - 1;
			EditPart editPart = (EditPart) getHost().getChildren().get(epIndex);
			r = transposer.t(getAbsoluteBounds((GraphicalEditPart)editPart));
		} else {
			EditPart editPart = (EditPart) getHost().getChildren().get(epIndex);
			r = transposer.t(getAbsoluteBounds((GraphicalEditPart)editPart));
			Point p = transposer.t(getLocationFromRequest(request));
			if (p.x <= r.x + (r.width / 2))
				before = true;
			else {
				/*
				 * We are not to the left of this Figure, so the emphasis line needs to be to
				 * the right of the previous Figure, which must be on the previous row.
				 */
				before = false;
				epIndex--;
				editPart = (EditPart) getHost().getChildren().get(epIndex);
				r = transposer.t(getAbsoluteBounds((GraphicalEditPart)editPart));
			}
		}
		int x = Integer.MIN_VALUE;
		if (before) {
			/* 
			 * Want the line to be halfway between the end of the previous and the beginning
			 * of this one. If at the begining of a line, then start halfway between the left
			 * edge of the parent and the beginning of the box, but no more than 5 pixels (it
			 * would be too far and be confusing otherwise).
			 */
			if (epIndex > 0) {
				// Need to determine if a line break.
				Rectangle boxPrev = transposer.t(
					getAbsoluteBounds(
						(GraphicalEditPart) getHost().getChildren().get(epIndex - 1)));
				int prevRight = boxPrev.right();
				if (prevRight < r.x) {
					// Not a line break
					x = prevRight + (r.x - prevRight) / 2;
				} else if (prevRight == r.x) {
					x = prevRight + 1;
				}
			}
			if (x == Integer.MIN_VALUE) {
				// It is a line break.
				Rectangle parentBox = transposer.t(
					getAbsoluteBounds((GraphicalEditPart)getHost()));
				x = r.x - 5;
				if (x < parentBox.x)
					x = parentBox.x + (r.x - parentBox.x) / 2;
			}
		} else {
			/* 
			 * We only have before==false if we are at the end of a line, so go halfway
			 * between the right edge and the right edge of the parent, but no more than 5
			 * pixels.
			 */
			Rectangle parentBox = transposer.t(
				getAbsoluteBounds((GraphicalEditPart)getHost()));
			int rRight = r.x + r.width;
			int pRight = parentBox.x + parentBox.width;
			x = rRight + 5;
			if (x > pRight)
				x = rRight + (pRight - rRight) / 2;
		}
		Point p1 = new Point(x, r.y - 4);
		//fb.translateToRelative(p1);
		p1 = transposer.t(p1);
		Point p2 = new Point(x, r.y + r.height + 4);
		//fb.translateToRelative(p2);
		p2 = transposer.t(p2);
		
		Figure feedbackLayer = (Figure)getLayer(LayerConstants.FEEDBACK_LAYER);
		feedbackLayer.translateToRelative(p1);
		feedbackLayer.translateToRelative(p2);
		p1.x -= 2;
		p2.x += 2;
		
		fb.removeAllPoints();
		fb.addPoint(new Point(p1.x, p1.y - CURSOR_SIZE));
		fb.addPoint(new Point(p1.x, p1.y + CURSOR_SIZE));
		fb.addPoint(p1);
		fb.addPoint(p2);
		fb.addPoint(new Point(p2.x, p2.y - CURSOR_SIZE));
		fb.addPoint(new Point(p2.x, p2.y + CURSOR_SIZE));
		fb.setForegroundColor(ColorConstants.white);
		fb.setLineWidth(2);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}
	
	//abstract protected Command createCreateCommand
}
