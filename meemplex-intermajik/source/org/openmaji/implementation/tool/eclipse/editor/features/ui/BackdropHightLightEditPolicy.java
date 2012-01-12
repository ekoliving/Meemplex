/*
 * @(#)BackdropHightLightEditPolicy.java
 * Created on 30/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.swt.graphics.Image;

/**
 * <code>BackdropHightLightEditPolicy</code> is an selection feeback edit policy 
 * (which can be installed with SELECTION_FEEDBACK_ROLE) that visualises 
 * highlight of <code>BackdropFreeformLayer</code> with an alternative image.
 * <p>
 * @author Kin Wong
 * @see org.openmaji.implementation.tool.eclipse.editor.features.ui.BackdropFreeformLayer
 */
public class BackdropHightLightEditPolicy extends GraphicalEditPolicy {
	private boolean reverted = false;	
	private Image normal;
	private Image highlight;

	/**
	 * Constructs an instance of <code>BackdropHightLightEditPolicy</code>.
	 * <p>
	 */
	public BackdropHightLightEditPolicy(Image normal, Image highlight) {
		super();
		this.normal = normal;
		this.highlight = highlight;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.EditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
	public void eraseTargetFeedback(Request request) {
		if (reverted) {
			reverted = false;
			updateImage();
		}
	}
	protected void showHighlight() {
		if (!reverted) {
			reverted = true;
			updateImage();
		}
	}
	
	private BackdropFreeformLayer getBackdropFigure() {
		return (BackdropFreeformLayer)getHostFigure();
	}
	private void updateImage() {
		BackdropFreeformLayer figure = getBackdropFigure();
		figure.setImage((reverted)? highlight : normal);
	}
	
	public void showTargetFeedback(Request request) {
		if (/*request.getType().equals(RequestConstants.REQ_MOVE)
			||*/ request.getType().equals(RequestConstants.REQ_ADD)
			|| request.getType().equals(RequestConstants.REQ_CONNECTION_START)
			|| request.getType().equals(RequestConstants.REQ_CONNECTION_END)
			|| request.getType().equals(RequestConstants.REQ_CREATE))
			showHighlight();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	public EditPart getTargetEditPart(Request request) {
		return request.getType().equals(RequestConstants.REQ_SELECTION_HOVER) ? getHost() : null;
	}

}
