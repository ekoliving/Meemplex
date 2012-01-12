package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.swt.graphics.Color;

/**
 * <code>ReverseHighlightEditPolicy</code> is an selection feeback edit policy 
 * (SELECTION_FEEDBACK_ROLE) that visually presents highlighted figure by 
 * swapping the foreground and background colors.
 * <p>
 * @author Kin Wong
 */
public class ReverseHighlightEditPolicy extends GraphicalEditPolicy {
	static public String[] DEFAULT_REQUESTS = {
		RequestConstants.REQ_MOVE,
		RequestConstants.REQ_ADD,
		RequestConstants.REQ_CONNECTION_START,
		RequestConstants.REQ_CONNECTION_END,
		RequestConstants.REQ_CREATE
	};
	
	//private String[] requests;
	private boolean reverted = false;	
	
	public ReverseHighlightEditPolicy() {
		this(DEFAULT_REQUESTS);
	}
	
	public ReverseHighlightEditPolicy(String[] requests) {
		//this.requests = requests;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.EditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
	 */
	public void eraseTargetFeedback(Request request) {
		if (reverted) {
			revert();
			if (request.getType().equals(RequestConstants.REQ_MOVE)
				|| request.getType().equals(RequestConstants.REQ_ADD)
				|| request.getType().equals(RequestConstants.REQ_CONNECTION_START)
				|| request.getType().equals(RequestConstants.REQ_CONNECTION_END)
				|| request.getType().equals(RequestConstants.REQ_CREATE))
			reverted = false;
		}
	}
	protected void showHighlight() {
		if (!reverted) {
			revert();
			reverted = true;
		}
	}
	private void revert() {
		IFigure figure = getHostFigure();
		Color fore = figure.getForegroundColor();
		Color back = figure.getBackgroundColor();
		figure.setForegroundColor(back);
		figure.setBackgroundColor(fore);
	}
	public void showTargetFeedback(Request request) {
		if (request.getType().equals(RequestConstants.REQ_MOVE)
			|| request.getType().equals(RequestConstants.REQ_ADD)
			|| request.getType().equals(RequestConstants.REQ_CONNECTION_START)
			|| request.getType().equals(RequestConstants.REQ_CONNECTION_END)
			|| request.getType().equals(RequestConstants.REQ_CREATE))
			showHighlight();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
	 */
	public EditPart getTargetEditPart(Request request) {
		if(request.getType().equals(RequestConstants.REQ_SELECTION_HOVER)) {
			System.out.println("Hover!");
			return getHost();
		}
		else
		return null;
	}

}
