/*
 * @(#)KINeticContextMenuProvider.java
 * Created on 10/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapseAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.DependencyAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.FacetRemoveDependencyAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LifeCycleAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.MeemAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.RouterAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.ViewModeAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.WedgeEditPart;


/**
 * <code>KINeticContextMenuProvider</code> creates and manage context menu of KINetic 
 * editor.
 * <p>
 * @author Kin Wong
 */
public class KINeticContextMenuProvider extends ContextMenuProvider {
	private ActionRegistry registry;
	/**
	 * Constructs an instance of <code>KINeticContextMenuProvider</code>.
	 * @param viewer The associated viewer.
	 */
	public KINeticContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		this.registry = registry;
	}
	
	private ActionRegistry getRegistry() {
		return registry;
	}
	
	/**
	 * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
	 */	
	public void buildContextMenu(IMenuManager menu) {
		IAction action;
		
		menu.add(new Separator(ActionConstants.GROUP_MAIN));
		menu.add(new Separator(ActionConstants.GROUP_LIFECYCLESTATE));
		menu.add(new Separator(ActionConstants.GROUP_VIEW_MODE));
		menu.add(new Separator(ActionConstants.GROUP_COLLAPSE));
		menu.add(new Separator(ActionConstants.GROUP_SORT));
		menu.add(new Separator(GEFActionConstants.GROUP_REST));
		menu.add(new Separator(GEFActionConstants.GROUP_SAVE));
		menu.add(new Separator(GEFActionConstants.GROUP_UNDO));
		menu.add(new Separator(GEFActionConstants.GROUP_EDIT));

		IMenuManager subMenu = createViewMenu();
		if(subMenu != null) menu.appendToGroup(ActionConstants.GROUP_VIEW_MODE, subMenu);
		
		// Dependency
		action = getRegistry().getAction(DependencyAction.ID_STRENGTH);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(ActionConstants.GROUP_MAIN, action);
		
		buildLifeCycleItems(menu);
		
		// Router
		action = getRegistry().getAction(RouterAction.ID_MANHATTAN);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(GEFActionConstants.GROUP_REST, action);
		
		// Sorting
		action = getRegistry().getAction(MeemEditPart.ID_SORT_WEDGE_ASCENDING);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(ActionConstants.GROUP_SORT, action);

		action = getRegistry().getAction(MeemEditPart.ID_SORT_WEDGE_DESCENDING);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(ActionConstants.GROUP_SORT, action);

		action = getRegistry().getAction(WedgeEditPart.ID_SORT_FACET_ASCENDING);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(ActionConstants.GROUP_SORT, action);

		action = getRegistry().getAction(WedgeEditPart.ID_SORT_FACET_DESCENDING);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(ActionConstants.GROUP_SORT, action);
		
		// Undo/Redo
		/*
		action = getRegistry().getAction(GEFActionConstants.UNDO);
//		if((action != null) && action.isEnabled())
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

		action = getRegistry().getAction(GEFActionConstants.REDO);
//		if((action != null) && action.isEnabled())
		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
		*/
				

		action = getRegistry().getAction(FacetRemoveDependencyAction.ID_REMOVE_DEPENDENCY);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

		action = getRegistry().getAction(ActionFactory.DELETE.getId());
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
			
		action = getRegistry().getAction(MeemAction.ID_REMOVE);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	
		action = getRegistry().getAction(MeemAction.ID_DESTROY);
		if (action.isEnabled())
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	}
	
	private IMenuManager createViewMenu() {
		MenuManager menu = new MenuManager("View");
		final int FIXED_ITEM_COUNT = 2;
		menu.add(new Separator(ActionConstants.GROUP_VIEW_MODE));
		menu.add(new Separator(ActionConstants.GROUP_COLLAPSE));

		// View Mode
		IAction action = getRegistry().getAction(ViewModeAction.ID_VIEW_MODE);
		if(action != null) {
			ViewModeAction viewModeAction = (ViewModeAction)action;
			viewModeAction.appendToGroup(menu, ActionConstants.GROUP_VIEW_MODE);
		}
		//menu.appendToGroup(ActionConstants.GROUP_VIEW_MODE, action);
		
		/*
		IAction action = getRegistry().getAction(ViewModeConstants.DETAILED);
		if(action != null) 
		menu.appendToGroup(ActionConstants.GROUP_VIEW_MODE, action);
		
		action = getRegistry().getAction(ViewModeConstants.ICONIC);
		if(action != null) 
		menu.appendToGroup(ActionConstants.GROUP_VIEW_MODE, action);

		action = getRegistry().getAction(ViewModeConstants.DEVICE);
		if(action != null) 
		menu.appendToGroup(ActionConstants.GROUP_VIEW_MODE, action);
		*/
		
		// Collapse/Expand
		action = getRegistry().getAction(CollapseAction.ID_COLLAPSE);
		if((action != null) && action.isEnabled())
		menu.appendToGroup(ActionConstants.GROUP_COLLAPSE, action);

		if(menu.getItems().length <= FIXED_ITEM_COUNT) return null;
		return menu;
	}

	protected void buildLifeCycleItems(IMenuManager menu) {
		IAction actionLifeCycleState = getRegistry().getAction(LifeCycleAction.ID_LIFE_CYCLE_STATE);
		Action action = new Action("Life Cycle State") {};
		action.setMenuCreator(actionLifeCycleState.getMenuCreator());
		
		menu.appendToGroup(ActionConstants.GROUP_LIFECYCLESTATE, 
			new ActionContributionItem(action));
	}
}
