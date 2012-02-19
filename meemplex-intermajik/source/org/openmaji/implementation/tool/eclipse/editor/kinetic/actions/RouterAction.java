/*
 * @(#)RouterAction.java
 * Created on 12/06/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.actions;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.EditorPartAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.Messages;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>RouterAction</code>.
 * <p>
 * @author Kin Wong
 */
public class RouterAction extends EditorPartAction {
	static public String ID_MANHATTAN = RouterAction.class + ".manhattan";
	public RouterAction(IWorkbenchPart part) {
		super(part, IAction.AS_RADIO_BUTTON);
		
		setId(ID_MANHATTAN);
		setText(Messages.RouterAction_Manhattan_Label);
		setToolTipText(Messages.RouterAction_Manhattan_ToolTip);
		setImageDescriptor(Images.ICON_ROUTER_MANHATTAN);
		setHoverImageDescriptor(Images.ICON_ROUTER_MANHATTAN);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.UpdateAction#update()
	 */
	public void update() {
		Class routerClass = getConnectionLayer().getConnectionRouter().getClass();
		setChecked(routerClass.equals(ManhattanConnectionRouter.class));
	}


	public void run() {
		ConnectionLayer layer = getConnectionLayer();
		Class routerClass = layer.getConnectionRouter().getClass();
		if(routerClass.equals(ManhattanConnectionRouter.class)) {
			AutomaticRouter router = new FanRouter();
			router.setNextRouter(new BendpointConnectionRouter());
			layer.setConnectionRouter(router);
		}
		else {
			ManhattanConnectionRouter router = new ManhattanConnectionRouter();
			layer.setConnectionRouter(router);
		}
	}
	
	private ConnectionLayer getConnectionLayer() {
		GraphicalViewer viewer = (GraphicalViewer)getEditorPart().getAdapter(GraphicalViewer.class);
		LayerManager manager = (LayerManager)viewer.getEditPartRegistry().get(LayerManager.ID);
		return (ConnectionLayer)manager.getLayer(LayerConstants.CONNECTION_LAYER);
	}
}
