/*
 * @(#)KINeticActionBuilder
 * Created on 7/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.openmaji.implementation.tool.eclipse.editor.common.model.comparators.ElementNameComparator;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapseAction;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.ScalableFreeformLabelRootEditPart;
import org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.DependencyAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.FacetRemoveDependencyAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LayoutAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.LifeCycleAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.MeemAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.MeemShowSystemWedgeAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.RouterAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.actions.ViewModeAction;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.WedgeEditPart;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.implementation.tool.eclipse.util.ReverseComparator;


/**
 * <code>KINeticActionBuilder</code>.
 * <p>
 * @author Kin Wong
 */
public class KINeticActionBuilder {
	static public String SORT_ASCENDING_ID = "action.sort.ascending";
	static public String SORT_DESCENDING_ID = "action.sort.descending";
	
	private ActionRegistry registry;
	private IWorkbenchPart part;
	private ScrollingGraphicalViewer viewer;
	private List selectionActions;
	
	public KINeticActionBuilder(
		ActionRegistry registry,
		IWorkbenchPart part,
		List selectionActions) {
		this.registry = registry;
		this.part = part;
		this.selectionActions = selectionActions;
	}

	protected ZoomManager getZoomManager() {
		return ((ScalableFreeformLabelRootEditPart)viewer.getRootEditPart()).getZoomManager();
	}
	
	public void createActions() {
		createCollapseActions();
		createMeemActions();
		createWedgeActions();
		
		createAlignmentActions();
		createViewModeActions();
		createDependencyActions();
		createSortActions();
		createLifeCycleActions();
		
		// Layout		
		IAction action = new LayoutAction((IEditorPart)part, LayoutAction.ID_LAYOUT_STANDARD);
		registry.registerAction(action);
		
		action = new RouterAction(part);
		registry.registerAction(action);
	}
	
	protected void createViewerActions(ScrollingGraphicalViewer viewer) {
		this.viewer = viewer;
		createZoomActions();
	}
	
	/**
	 * Creates all meem specific actions.
	 */
	protected void createMeemActions() {
			IAction action = new MeemAction(part, MeemAction.ID_REMOVE);
			registry.registerAction(action);
			selectionActions.add(action.getId());

			action = new MeemAction(part, MeemAction.ID_DESTROY);
			registry.registerAction(action);
			selectionActions.add(action.getId());
			
			action = new MeemShowSystemWedgeAction(part);
			registry.registerAction(action);
			selectionActions.add(action.getId());
			
			action = new FacetRemoveDependencyAction(part);
			registry.registerAction(action);
			selectionActions.add(action.getId());
	}
	
	/**
	 * Creates all wedge specific actions.
	 */
	protected void createWedgeActions(){
	}
			
	/**
	 * Creates all collapse/expand actions.
	 */
	protected void createCollapseActions() {

		// Selection Sensitive
		IAction action = new CollapseAction(part);
		registry.registerAction(action);
		selectionActions.add(action.getId());
	}
	
	private void createZoomActions() {
		ZoomInAction zoomInAction = new ZoomInAction(getZoomManager());
		ZoomOutAction zoomOutAction = new ZoomOutAction(getZoomManager());
		registry.registerAction(zoomInAction);
		registry.registerAction(zoomOutAction);
	}
	
	/**
	 * Creates all alignment related actions.
	 * @param registry The action registry of this editor.
	 */
	private void createAlignmentActions() {
		IAction action = new AlignmentAction(part, PositionConstants.LEFT);
		registry.registerAction(action);
		selectionActions.add(action.getId());
		
		action = new AlignmentAction(part, PositionConstants.LEFT);
		registry.registerAction(action);
		selectionActions.add(action.getId());

		action = new AlignmentAction(part, PositionConstants.RIGHT);
		registry.registerAction(action);
		selectionActions.add(action.getId());

		action = new AlignmentAction(part, PositionConstants.TOP);
		registry.registerAction(action);
		selectionActions.add(action.getId());

		action = new AlignmentAction(part, PositionConstants.BOTTOM);
		registry.registerAction(action);
		selectionActions.add(action.getId());

		action = new AlignmentAction(part, PositionConstants.CENTER);
		registry.registerAction(action);
		selectionActions.add(action.getId());

		action = new AlignmentAction(part, PositionConstants.MIDDLE);
		registry.registerAction(action);
		selectionActions.add(action.getId());
	}
	
	/**
	 * Creates all view mode actions.
	 */
	private void createViewModeActions() {		

		//=== View Mode Actions ===	
			IAction action = new ViewModeAction(part);
			registry.registerAction(action);
			selectionActions.add(action.getId());
		
//		action = new ViewModeAction(part, ViewModeConstants.ICONIC);
//		registry.registerAction(action);
//		selectionActions.add(action.getId());

//		action = new ViewModeAction(part, ViewModeConstants.DEVICE);
//		registry.registerAction(action);
//		selectionActions.add(action.getId());
	}

	/**
	 * Creates all dependency actions.
	 * @param registry The action registry of editorPart editor.
	 */
	private void createDependencyActions() {
		//=== Strength (Strong/Weak) ===	
		IAction action = new DependencyAction(part);
		registry.registerAction(action);
		selectionActions.add(action.getId());
	}
	
	/**
	 * Creates all sort actions.
	 * @param registry The action registry of editorPart editor.
	 */
	private void createSortActions() {
			// Sort Wedge name ascending		
			IAction action = new SortAction(part, new ElementNameComparator(false));
			action.setId(MeemEditPart.ID_SORT_WEDGE_ASCENDING);
			action.setText(Messages.SortAction_Wedge_Ascending_Label);
			action.setToolTipText(Messages.SortAction_Wedge_Ascending_Tooltip);
			action.setImageDescriptor(Images.ICON_SORT_ASCENDING);
			registry.registerAction(action);
			selectionActions.add(action.getId());

			// Sort Wedge name descending		
			action = new SortAction(part, new ReverseComparator(new ElementNameComparator(false)));
			action.setId(MeemEditPart.ID_SORT_WEDGE_DESCENDING);
			action.setText(Messages.SortAction_Wedge_Descending_Label);
			action.setToolTipText(Messages.SortAction_Wedge_Descending_Tooltip);
			action.setImageDescriptor(Images.ICON_SORT_DESCENDING);
			registry.registerAction(action);
			selectionActions.add(action.getId());

			// Sort Facet name ascending		
			action = new SortAction(part, new ElementNameComparator(false));
			action.setId(WedgeEditPart.ID_SORT_FACET_ASCENDING);
			action.setText(Messages.SortAction_Facet_Ascending_Label);
			action.setToolTipText(Messages.SortAction_Facet_Ascending_Tooltip);
			action.setImageDescriptor(Images.ICON_SORT_ASCENDING);
			registry.registerAction(action);
			selectionActions.add(action.getId());

			// Sort Facet name descending		
			action = new SortAction(part, new ReverseComparator(new ElementNameComparator(false)));
			action.setId(WedgeEditPart.ID_SORT_FACET_DESCENDING);
			action.setText(Messages.SortAction_Facet_Descending_Label);
			action.setToolTipText(Messages.SortAction_Facet_Descending_Tooltip);
			action.setImageDescriptor(Images.ICON_SORT_DESCENDING);
			registry.registerAction(action);
			selectionActions.add(action.getId());
	}

	private void createLifeCycleActions(){
		IAction action = new LifeCycleAction(part);
		registry.registerAction(action);
		selectionActions.add(action.getId());

/*
			action = new LifeCycleAction(part, LifeCycleAction.ID_LIFE_CYCLE_LOADED);
			registry.registerAction(action);

			action = new LifeCycleAction(part, LifeCycleAction.ID_LIFE_CYCLE_DORMANT);
			registry.registerAction(action);
			selectionActions.add(action.getId());

			action = new LifeCycleAction(part, LifeCycleAction.ID_LIFE_CYCLE_LOADED);
			registry.registerAction(action);
			selectionActions.add(action.getId());
		
			action = new LifeCycleAction(part, LifeCycleAction.ID_LIFE_CYCLE_READY);
			registry.registerAction(action);
			selectionActions.add(action.getId());
			*/
			
	}
}
