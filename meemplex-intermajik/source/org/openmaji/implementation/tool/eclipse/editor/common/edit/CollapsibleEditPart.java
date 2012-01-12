package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToggleModel;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.ui.IEditorPart;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.CollapsibleFigure;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Collapsible;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapseAction;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapsibleEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.ICollapsible;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.SimpleSelectionHandlesEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;


/**
 * @author Kin Wong
 *
 * Default implementation of a collapsible container object.
 */
abstract public class CollapsibleEditPart 
	extends ElementContainerEditPart {
	protected boolean ignoredChange = false;
	private ChangeListener changeListener = new ChangeListener() {
		/**
		 * Implemented here to handle collapse button click.
		 * @see org.eclipse.draw2d.ChangeListener#handleStateChanged(ChangeEvent)
		 */
		public void handleStateChanged(ChangeEvent event) {
			if(ignoredChange) return; 
			String prop = event.getPropertyName();
			if(ToggleModel.SELECTED_PROPERTY.equals(prop)) toggle();
		}
	};
	
	/**
	 * Gets the attached model as a Collapsible.
	 * @return Collapsible The CollapsibleElement object which this EditPart
	 * connects to.
	 */	
	public Collapsible getCollapsibleModel() {
		return (Collapsible)getModel();
	}
	
	/**
	 * Gets the attached figure as a CollapsibleFigure.
	 * @return CollapsibleFigure the CollapsibleFigure which this EditPart
	 * connects to.
	 */
	public CollapsibleFigure getCollapsibleFigure() {
		return (CollapsibleFigure)getFigure();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if(	ICollapsible.class.equals(key) && 
			(getModel() instanceof ICollapsible))
		return getModel();
		else
		return super.getAdapter(key);
	}

	/**
	 * Overridden here to return the collapsible content pane of the
	 * CollapsibleFigure.
	 * @see org.eclipse.gef.GraphicalEditPart#getContentPane()
	 */
	public IFigure getContentPane() {
		return getCollapsibleFigure().getContentPane();
	}
	
	/**
	 * Installs the desired EditPolicies for this.
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SimpleSelectionHandlesEditPolicy());
		installEditPolicy(FeatureEditPolicy.HIGHLIGHT_ROLE, new ReverseHighlightEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new CollapsibleLayoutEditPolicy());
		installEditPolicy(FeatureEditPolicy.COLLAPSE_EXPAND_ROLE, new CollapsibleEditPolicy());
	}
	
	/**
	 * Overridden here to subscribe to the change events of the
	 * CollapsibleFigure.
	 * @see org.eclipse.gef.EditPart#activate()
	 */	
	public void activate() {
		super.activate();
		ToggleModel model = (ToggleModel)getCollapsibleFigure().getCollapseButton().getModel();
		model.addChangeListener(changeListener);
	}
	
	/**
	 * Overridden here to unsubscribe to the change events of the
	 * CollapsibleFigure.
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		if(getCollapsibleFigure() != null) getCollapsibleFigure().dispose();
		ToggleModel model = (ToggleModel)getCollapsibleFigure().getCollapseButton().getModel();
		model.removeChangeListener(changeListener);
		super.deactivate();
	}
	
	/**
	 * Overridden here to return an empty list if the collapsible is currently 
	 * collapsed.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementContainerEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		if(getCollapsibleModel().isCollapsed()) return Collections.EMPTY_LIST;
		return super.getModelChildren();
	}
	
	/**
	 * Overridden here to provide handling of collapse property change
	 * (Collapsible.ID_COLLAPSED).
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(Collapsible.ID_COLLAPSED.equals(prop)) {
			boolean collapsed = getCollapsibleModel().isCollapsed();
			// The collapse state of the model has been changed
			removeConnections();
			if(collapsed) {
				Collapsed();
			} 
			else {
				Expanded();
			} 
			EditPartHelper.forceRepaint(this);
		} 
		else
		super.propertyChange(evt);
	}
	
	/**
	 * Invoked when the collapsible object has been collapsed. The default
	 * implementation invokes refresh() of the edit part. Derived class can 
	 * perform additional task.
	 */
	protected void Collapsed() {
		refresh();
	}
	
	/**
	 * Invoked when the collapsible object has been expanded. The default 
	 * implementation invokes refresh() of the edit part. Derived class can
	 * perform additional tasks.
	 */
	protected void Expanded() {
		refresh();
	}

	protected void removeConnections() {
		removeSourceConnections();
		removeTargetConnections();
	}
	
	protected void removeSourceConnections() {
		if(sourceConnections == null) return;
		List connections = new ArrayList(sourceConnections);
		Iterator it = connections.iterator();
		while(it.hasNext())	{
			EditPart editPart = (EditPart)it.next();
			if(editPart instanceof ConnectionEditPart) {
				ConnectionEditPart connectionEditPart = (ConnectionEditPart)editPart;
				removeSourceConnection(connectionEditPart);
			}
		}
	}

	protected void removeTargetConnections() {
		if(targetConnections == null) return;
		List connections = new ArrayList(targetConnections);
		Iterator it = connections.iterator();
		while(it.hasNext())	{
			EditPart editPart = (EditPart)it.next();
			if(editPart instanceof ConnectionEditPart) {
				ConnectionEditPart connectionEditPart = (ConnectionEditPart)editPart;
				removeTargetConnection(connectionEditPart);
			}
		}
	}

	/**
	 * Overridden to support expanding and collapse by double-click.
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	public DragTracker getDragTracker(Request request) {
		return new CollapsibleDragEditPartsTracker(this);
	}
	
	/**
	 * Overridden to refresh this collapsible figure according to the 
	 * collapsible model. 
	 * Derived class can override to perform additional refresh.
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		refreshCollapsibleFigure();
	}
	
	/**
	 * Refreshes the collapsible figure.
	 * 
	 */
	protected void refreshCollapsibleFigure() {
		Collapsible collapsible = getCollapsibleModel();
		CollapsibleFigure figure = getCollapsibleFigure();

		figure.setCaptionText(collapsible.getName());
		ignoredChange = true;
		figure.setCollapse(collapsible.isCollapsed());
		figure.getCollapseButton().setSelected(collapsible.isCollapsed());
		ignoredChange = false;
	}
	
	/**
	 * Gets whether the collapsible model is currently expanded.
	 * @return boolean true if the collapsible model is currently expanded, 
	 * false otherwise.
	 */
	public boolean isExpanded() {
		return !isCollapsed();
	}
	
	public boolean isCollapsed() {
		return getCollapsibleModel().isCollapsed();
	}
	
	public void toggle() {
		IEditorPart editorPart = EditPartHelper.getEditorPart(this);			
		ActionRegistry actionRegistry = 
			(ActionRegistry)editorPart.getAdapter(ActionRegistry.class);
		if(actionRegistry == null) return;
		
		CollapseAction action  = 
			(CollapseAction)actionRegistry.getAction(CollapseAction.ID_COLLAPSE);
		ignoredChange = true;
		action.run(this, !isCollapsed());
		ignoredChange = false;
	}
	
	/**
	 * Private class to handle double-clicking to toggle collapse state in
	 * CollapsibleEditPart.
	 * <p>
	 * @author Kin Wong
	 */
	class CollapsibleDragEditPartsTracker extends DragEditPartsTracker {
		public CollapsibleDragEditPartsTracker(EditPart sourceEditPart) {
			super(sourceEditPart);
		}
		/**
		 * Overridden to toggle the collapse state when the eidtpart is 
		 * double-clicked.
		 * @see org.eclipse.gef.tools.AbstractTool#handleDoubleClick(int)
		 */
		protected boolean handleDoubleClick(int button) {
			CollapsibleEditPart editpart = (CollapsibleEditPart)getSourceEditPart();
			editpart.toggle();
			return super.handleDoubleClick(button);
		}
	}
}
