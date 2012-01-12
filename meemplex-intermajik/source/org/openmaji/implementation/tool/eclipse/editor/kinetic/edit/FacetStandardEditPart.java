/*
 * Created on 12/03/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.HorizontalAnchor;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.FacetFigure;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;


/**
 * <code>FacetStandardEditPart</code> extends <code>FacetEditPart</code> with
 * dependency connectivity.
 * @author Kin Wong
 */
public class FacetStandardEditPart 
	extends FacetEditPart 
	implements NodeEditPart {
	private AbstractConnectionAnchor anchor;
	/**
	 * Constructs an instance of <code>FacetStandardEditPart</code>.
	 * <p>
	 * @param facet The facet model asociates with this edit part.
	 */
	public FacetStandardEditPart(Facet facet) {
		super(facet);
	}
	public Facet getConnectableFacetModel() {
		return (Facet)getModel();
	}

	 protected void createEditPolicies() {
		super.createEditPolicies();
		//installEditPolicy(FeatureEditPolicy.CONNECTABLE_ROLE, new ConnectableComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new FacetNodeEditPolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new FacetDragEditPolicy());
		installEditPolicy(FeatureEditPolicy.VIEW_MODE_ROLE, new ViewModeEditPolicy(getConnectableFacetModel()));
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		FacetFigure figure = new FacetFigure();
		figure.apply(getScheme());
		anchor = new HorizontalAnchor(figure);
		return figure;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		return getConnectableFacetModel().getSourceConnections();
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		return getConnectableFacetModel().getTargetConnections();
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.FacetEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		
		if(Facet.ID_INPUT_DEPENDECY.equals(prop)) {
			 refreshTargetConnections(); 
		} 
		else 
		if(Facet.ID_OUTPUT_DEPENDECY.equals(prop)) {
			refreshSourceConnections();
			refreshVisuals();
		} 
		else
		if(Facet.ID_VIEW_MODE.equals(prop)) {
			ElementContainer container = (ElementContainer)getParent().getModel();
			container.refreshChild(getConnectableFacetModel());
		}
		else
		super.propertyChange(evt);
	}
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connectionEditPart) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if(!(request instanceof CreateConnectionRequest)) return null;
		if(!getFacetModel().getSourceConnections().isEmpty()) return null;
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connectionEditPart) {
		//System.out.println("getTargetConnectionAnchor(" + connectionEditPart + ")");
		//if(getFacetModel().isOutbound()) return null;
//		System.out.println("getTargetConnectionAnchor(" + connectionEditPart.getSource() + ")");
		//connectionEditPart.getSource()
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if(!(request instanceof CreateConnectionRequest)) return null;
		CreateConnectionRequest createRequest = (CreateConnectionRequest)request;
		Object source = createRequest.getSourceEditPart().getModel();
		if(!(source instanceof Facet)) return null;
		
		Facet facet = (Facet)source;
		if(facet.isInbound() == getFacetModel().isInbound()) return null;
		return anchor;
	}
}
