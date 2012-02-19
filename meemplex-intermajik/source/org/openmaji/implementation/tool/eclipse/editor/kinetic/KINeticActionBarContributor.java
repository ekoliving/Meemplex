/*
 * @(#)KINeticActionBarContributor.java
 * Created on 15/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.ActionBarContributor;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.LabelRetargetAction;
import org.openmaji.implementation.tool.eclipse.editor.common.actions.RetargetAction;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapseAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LayoutAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LifeCycleAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.MeemShowSystemWedgeAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.RouterAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.ViewModeAction;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>KINeticActionBarContributor</code>.
 * <p>
 * @author Kin Wong
 */
public class KINeticActionBarContributor extends ActionBarContributor {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
	 */
	protected void buildActions() {
		RetargetAction action;
		// Create zoom in action
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		// Alignment Actions
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
		addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));

		addRetargetAction(new DeleteRetargetAction());
		addRetargetAction(new UndoRetargetAction());
		addRetargetAction(new RedoRetargetAction());
		
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		// Router Actions
		action = new RetargetAction(RouterAction.ID_MANHATTAN, Messages.RouterAction_Standard_Label, IAction.AS_RADIO_BUTTON);
		action.setToolTipText(Messages.RouterAction_Manhattan_ToolTip);
		action.setImageDescriptor(Images.ICON_ROUTER_MANHATTAN);
		addRetargetAction(action);

		// Collapsible Actions
		action = new RetargetAction(CollapseAction.ID_COLLAPSE, 
			org.openmaji.implementation.tool.eclipse.editor.features.Messages.CollapseModeCollapse_Label, IAction.AS_RADIO_BUTTON);
		action.setToolTipText(org.openmaji.implementation.tool.eclipse.editor.features.Messages.CollapseModeCollapse_Tooltip);
		action.setImageDescriptor(Images.ICON_COLLAPSE);
		addRetargetAction(action);
		
		// View Mode Actions
		action = new LabelRetargetAction(ViewModeAction.ID_VIEW_MODE, IAction.AS_DROP_DOWN_MENU);
		addRetargetAction(action);

		// Layout Actions		
		action = new RetargetAction(LayoutAction.ID_LAYOUT_STANDARD, Messages.LayoutAction_LayoutStandard_Label);
		action.setToolTipText(Messages.LayoutAction_LayoutStandard_Label);
		action.setImageDescriptor(Images.ICON_LAYOUT);
		addRetargetAction(action);
		
		// Show System Wedge Action		
		action = new RetargetAction(MeemShowSystemWedgeAction.ID_SHOW_SYSTEM_WEDGES, Messages.MeemAction_ShowSystemWedges_Label, IAction.AS_RADIO_BUTTON);
		action.setToolTipText(Messages.MeemAction_ShowSystemWedges_Label);
		action.setImageDescriptor(Images.ICON_SHOW_SYSTEM_WEDGES);
		addRetargetAction(action);

		addRetargetAction(
			new LabelRetargetAction(LifeCycleAction.ID_LIFE_CYCLE_STATE, 
			IAction.AS_DROP_DOWN_MENU));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
	 */
	protected void declareGlobalActionKeys() {
		//=== Global Actions ===
//		// Zoom Actions
//		addGlobalActionKey(GEFActionConstants.ZOOM_IN);
//		addGlobalActionKey(GEFActionConstants.ZOOM_OUT);
		// Collapse/Expand
		addGlobalActionKey(CollapseAction.ID_COLLAPSE);
		// Alignment Actions
		addGlobalActionKey(GEFActionConstants.ALIGN_LEFT);
		addGlobalActionKey(GEFActionConstants.ALIGN_CENTER);
		addGlobalActionKey(GEFActionConstants.ALIGN_RIGHT);
		addGlobalActionKey(GEFActionConstants.ALIGN_TOP);
		addGlobalActionKey(GEFActionConstants.ALIGN_MIDDLE);
		addGlobalActionKey(GEFActionConstants.ALIGN_BOTTOM);
		
		addGlobalActionKey(LayoutAction.ID_LAYOUT_STANDARD);
//		addGlobalActionKey(LayoutAction.ID_LAYOUT_DEATHMAN);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	public void contributeToToolBar(IToolBarManager tbm) {
		// Zoom Manager		
		tbm.add(getAction(GEFActionConstants.ZOOM_IN));
		tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
		tbm.add(new ZoomComboContributionItem(getPage()));
		tbm.add(new Separator());

		// LifeCycle 
		tbm.add(getAction(LifeCycleAction.ID_LIFE_CYCLE_STATE));
//		tbm.add(getAction(LifeCycleAllAction.ID_LIFE_CYCLE_READY));
//		tbm.add(getAction(LifeCycleAllAction.ID_LIFE_CYCLE_LOADED));
			
		tbm.add(new Separator());
		tbm.add(getAction(LayoutAction.ID_LAYOUT_STANDARD));
		tbm.add(getAction(RouterAction.ID_MANHATTAN));
		tbm.add(new Separator());

		// View related actions
		tbm.add(getAction(ViewModeAction.ID_VIEW_MODE));
		tbm.add(getAction(CollapseAction.ID_COLLAPSE));
		tbm.add(getAction(MeemShowSystemWedgeAction.ID_SHOW_SYSTEM_WEDGES));
		tbm.add(new Separator());

		// Horizontal Alignment Actions
		/*
		tbm.add(getAction(GEFActionConstants.ALIGN_LEFT));
		tbm.add(getAction(GEFActionConstants.ALIGN_CENTER));
		tbm.add(getAction(GEFActionConstants.ALIGN_RIGHT));
		tbm.add(new Separator());
*/
		// Vertical Alignment Actions
		/*
		tbm.add(getAction(GEFActionConstants.ALIGN_TOP));
		tbm.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
		tbm.add(getAction(GEFActionConstants.ALIGN_BOTTOM));
		tbm.add(new Separator());
		*/
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToStatusLine(org.eclipse.jface.action.IStatusLineManager)
	 */
	public void contributeToStatusLine(IStatusLineManager statusLineManager) {
	}
}
