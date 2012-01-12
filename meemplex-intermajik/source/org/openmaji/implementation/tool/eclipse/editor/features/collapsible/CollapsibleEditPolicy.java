/*
 * Created on 9/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

/**
 * <code>CollapsibleEditPolicy</code> works in conjuction with 
 * <code>ICollapsible</code> and <code>CollapseCommand</code> to provide 
 * reusable collapsibility. Install this edit policy in the edit part with 
 * <code>org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy.
 * COLLAPSE_EXPAND_ROLE</code>.
 * <p>
 * @author Kin Wong
 */
public class CollapsibleEditPolicy extends GraphicalEditPolicy {
	/**
	 * Gets the host collapsible object from the edit part.
	 * @return ICollapsible The host collapsible object.
	 */
	protected ICollapsible getCollapsible() {
		ICollapsible collapsible = null;
		if(getHost() instanceof ICollapsible)
		collapsible = (ICollapsible)getHost();
		else
		collapsible = (ICollapsible)getHost().getAdapter(ICollapsible.class);
		
		if(	(collapsible == null) && 
			(getHost().getModel() instanceof ICollapsible)) 
		collapsible = (ICollapsible)getHost().getModel();
		return collapsible;
	}
	/**
	 * Factors the incoming Request into command.
	 */
	public Command getCommand(Request request) {
		ICollapsible collapsible = getCollapsible();
		if(collapsible == null) return null;
		
		if(CollapseAction.REQ_COLLAPSE.equals(request.getType())) {
			if(collapsible.isCollapsed()) return null;
			return new CollapseCommand(collapsible, true);
		}
		else
		if(CollapseAction.REQ_EXPAND.equals(request.getType())) {
			if(collapsible.isCollapsed()) 
			return new CollapseCommand(collapsible, false);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#understandsRequest(org.eclipse.gef.Request)
	 */
	public boolean understandsRequest(Request req) {
		ICollapsible collapsible = getCollapsible();
		if(collapsible == null) return false;
		
		if(CollapseAction.REQ_COLLAPSE.equals(req.getType())) {
			return (!collapsible.isCollapsed());
		}
		else
		if(CollapseAction.REQ_EXPAND.equals(req.getType())) {
			return collapsible.isCollapsed();
		}
		else
		return super.understandsRequest(req);
	}
}
