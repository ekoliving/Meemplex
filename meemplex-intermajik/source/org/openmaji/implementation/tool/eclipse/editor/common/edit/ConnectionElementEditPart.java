package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionBendpoint;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ConnectionElement;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicconnections.DynamicConnectionEditPolicy;


/**
 * @author Kin Wong
 */

public abstract class ConnectionElementEditPart 
	extends AbstractConnectionEditPart 
	implements PropertyChangeListener
{
	protected ConnectionElementEditPart(ConnectionElement connectionElement){
		setModel(connectionElement);
	}
	
	public ConnectionElement getConnectionElementModel() {
		return (ConnectionElement)getModel();
	}
	
	public PolylineConnection getPolyline() {
		return (PolylineConnection)getFigure();
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		super.activate();
		getConnectionElementModel().addPropertyChangeListener(this);
	}
	
	public void activateFigure(){
		super.activateFigure();
		getFigure().addPropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	public void deactivateFigure(){
		getFigure().removePropertyChangeListener(Connection.PROPERTY_CONNECTION_ROUTER, this);
		super.deactivateFigure();
	}

	/**
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		getConnectionElementModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ConnectionBendpointEditPolicy());
		installEditPolicy(FeatureEditPolicy.CONNECTION_VISIBILITY_ROLE, new DynamicConnectionEditPolicy());
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(ConnectionElement.ID_REFRESH_VISUALS.equals(prop)) {
			refresh();
		}
		else
		if(ConnectionElement.ID_TARGET.equals(prop)) {
			onTargetChanged();
		}
		else
		if(ConnectionElement.ID_SOURCE.equals(prop)) {
			onSourceChanged();
		}
		else
		if (Connection.PROPERTY_CONNECTION_ROUTER.equals(prop)) {
			refreshBendpoints();
		}
		else
		if (ConnectionElement.ID_BENDPOINT.equals(prop)) {
			refreshBendpoints();       
		}
	}
	
	protected void refreshBendpoints() {
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter) return;
		List modelConstraint = getConnectionElementModel().getBendpoints();
		
		List figureConstraint = new ArrayList();
		for (int i=0; i<modelConstraint.size(); i++) {
			ConnectionBendpoint bendpoint = (ConnectionBendpoint)modelConstraint.get(i);
			RelativeBendpoint relBendpoint = new RelativeBendpoint(getConnectionFigure());
			relBendpoint.setRelativeDimensions(
				bendpoint.getFirstRelativeDimension(),
				bendpoint.getSecondRelativeDimension());
			relBendpoint.setWeight((i+1) / ((float)modelConstraint.size()+1));
			figureConstraint.add(relBendpoint);
		}
		getConnectionFigure().setRoutingConstraint(figureConstraint);
	}
	
	protected void refreshVisuals() {
		refreshBendpoints();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#refresh()
	 */
	public void refresh() {
		super.refresh();
		refreshPropertySheet();
	}

	/**
	 * Refreshes the propert sheet associates with this element edit part.
	 */
	protected void refreshPropertySheet() {
		if(getSelected() == SELECTED_NONE) return;
		getViewer().deselect(this);
		getViewer().flush();
		getViewer().appendSelection(this);
	}
	
	protected void onTargetChanged() {}
	protected void onSourceChanged() {}
		
}
