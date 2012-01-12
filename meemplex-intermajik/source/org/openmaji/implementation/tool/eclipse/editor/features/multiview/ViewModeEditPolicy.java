/*
 * Created on 9/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.multiview;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.RequestConstants;


/**
 * @author Kin Wong
 * The ViewModeEditPolicy supports dynamic change of the view mode (editpart 
 * and its associated figure).
 */
public class ViewModeEditPolicy extends GraphicalEditPolicy 
implements RequestConstants {
	private IViewModeProvider viewModeProvider;
	/**
	 * Constructs an instance of <code>ViewModeEditPolicy</code>.
	 * @param viewModeProvider The new instance of 
	 * <code>ViewModeEditPolicy</code>.
	 */
	public ViewModeEditPolicy(IViewModeProvider viewModeProvider) {
		this.viewModeProvider = viewModeProvider;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	public boolean understandsRequest(Request req) {
		if(!(req instanceof ViewModeRequest)) 
			return super.understandsRequest(req);
		
		ViewModeRequest viewModeRequest = (ViewModeRequest)req;
		return viewModeProvider.supportViewMode(viewModeRequest.getViewMode());
	}
	
	/**
	 * Factors the incoming Request into view mode change command.
	 * @see org.eclipse.gef.EditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		if (REQ_CHANGE_VIEW_MODE.equals(request.getType()))
			return getChangeViewModeCommand((ViewModeRequest)request);
		return null;
	}
	/**
	 * Gets a command that changes the view mode of the view mode provider.
	 * @param request The new view mode request.
	 * @return Command The command that changes the view mode.
	 */
	protected Command getChangeViewModeCommand(ViewModeRequest request) {
		if(request.getViewMode().equals(viewModeProvider.getViewMode())) 
		return null;	// The view mode is the same.
		
		ViewModeCommand command = new ViewModeCommand(viewModeProvider);
		command.setViewMode(request.getViewMode());
		return command;
	}
}
