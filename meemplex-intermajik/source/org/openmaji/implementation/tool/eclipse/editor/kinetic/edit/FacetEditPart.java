/*
 * @(#)FacetEditPart.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;


import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleClientStub;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.BoundsObjectEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ReverseHighlightEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.HorizontalAnchor;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.SimpleSelectionHandlesEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.infotips.InfoTip;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.FacetFigure;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips.FacetInfoTip;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.FacetInbound;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FacetInboundPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.FacetOutboundPropertySource;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * <code>FacetEditPart</code>.<p>
 * @author Kin Wong
 */
public class FacetEditPart extends BoundsObjectEditPart implements NodeEditPart {
	private interface DependencyState {
		int NOT_USED = 0;
		int USED = 1;
		int USED_INVISIBLE = 2;
	}
	
	private AbstractConnectionAnchor anchor;
	private LifeCycleClient lifeCycleClient = new LifeCycleClientStub() {
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.client.LifeCycleClientStub#lifeCycleStateChanged(org.openmaji.meem.wedge.lifecycle.LifeCycleTransition)
		 */
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			refreshVisuals();
		}
	};
	
	/**
	 * Constructs an instance of <code>FacetEditPart</code>.<p>
	 * @param facet The facet to be associates with this edit part.
	 */
	public FacetEditPart(Facet facet) {
		setModel(facet);
	}
	
	/**
	 * Gets the facet model of this edit part.<p>
	 * @return The facet model of this edit part.
	 */
	public Facet getFacetModel() {
		return (Facet) getModel();
	}
	
	/**
	 * Gets the facet figure of this edit part.<p>
	 * @return The facet figure of this edit part.
	 */
	protected FacetFigure getFacetFigure() {
		return (FacetFigure) getFigure();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		super.activate();
		getFacetModel().getProxy().getLifeCycle().addClient(lifeCycleClient);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		if(getFacetFigure() != null) getFacetFigure().dispose();
		getFacetModel().getProxy().getLifeCycle().removeClient(lifeCycleClient);
		super.deactivate();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if(key.equals(IPropertySource.class)) {
			if(getFacetModel() instanceof FacetInbound) {
				return new FacetInboundPropertySource(getFacetModel());
			}
			else {
				return new FacetOutboundPropertySource(getFacetModel());
			}
		}
		else
		return super.getAdapter(key);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(KINeticEditPolicy.LIFE_CYCLE_STATE_ROLE, 
			new LifeCycleStateEditPolicy(getFacetModel().getProxy()));
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new FacetMetaMeemEditPolicy());
		installEditPolicy(FeatureEditPolicy.HIGHLIGHT_ROLE, new ReverseHighlightEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SimpleSelectionHandlesEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new FacetNodeEditPolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new FacetDragEditPolicy());
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
	
	protected FigureScheme getScheme() {
		IFigureSchemeProvider schemeProvider = (IFigureSchemeProvider)
			EditPartHelper.findAncestor(this, IFigureSchemeProvider.class);
		return schemeProvider.getScheme(this);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		return getFacetModel().getSourceConnections();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		return getFacetModel().getTargetConnections();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		
		if(Facet.ID_ATTRIBUTE.equals(prop)) {
			refreshVisuals();
			refreshPropertySheet();

			List sources = getSourceConnections();
			for (Iterator iter = sources.iterator(); iter.hasNext();) {
				ConnectionEditPart editPart = (ConnectionEditPart) iter.next();
				editPart.refresh();
			}
		}
		else
		if(Facet.ID_INPUT_DEPENDECY.equals(prop)) {
			 refreshTargetConnections(); 
		} 
		else 
		if(Facet.ID_OUTPUT_DEPENDECY.equals(prop)) {
			refreshSourceConnections();
			refreshVisuals();
		} 
		else
		if(Facet.ID_DEPENDENCY_KEY.equals(prop)) {
			refreshDependency();
		}
		else
		super.propertyChange(evt);
	}
	
	protected void refreshVisuals() {
		super.refreshVisuals();
		
		Facet facet = getFacetModel();
		FacetFigure figure = getFacetFigure();
		figure.getLabel().setText(facet.getName());
		figure.setBounds(facet.isInbound());
		figure.setToolTip(new FacetInfoTip(getFacetModel()));
		
		refreshDependency();
	}
	
	protected void refreshDependency() {
		int dependencyState = getDependencyState();

		//boolean hasDependency = (getFacetModel().getDependencyKey() != null);
		//boolean hasDependencyConnection = (!getModelSourceConnections().isEmpty());
		
		String tip = "";
		Color color = null;
		switch(dependencyState) {
			case DependencyState.USED:
			tip = "Dependency exists.\n";
			color = ColorConstants.gray;
			break;

			case DependencyState.USED_INVISIBLE:
			tip = "Dependency exists but not visible.\n";
			color = ColorConstants.red;
			break;

			case DependencyState.NOT_USED:
			default:
			tip = "No dependency.\n";
			color = ColorConstants.black;
			break;
		}
		
		//LifeCycleState state = getFacetModel().getProxy().getLifeCycle().getState();
		switch(dependencyState) {
			case DependencyState.USED:
			tip += "Use Context Menu to remove.";
			break;

      case DependencyState.USED_INVISIBLE:
			tip += "Use Context Menu for more options.";
			break;

			case DependencyState.NOT_USED:
			default:
			tip += "Shift-Click to create.";
			break;
		}
		
		InfoTip infoTip = new InfoTip(tip);
		FacetFigure figure = getFacetFigure();
		figure.getLeftTriangle().setToolTip(infoTip);		
		figure.getRightTriangle().setToolTip(infoTip);
		figure.getLeftTriangle().setBackgroundColor(color);
		figure.getRightTriangle().setBackgroundColor(color);
	}
	
	private int getDependencyState() {
		boolean hasDependency = (getFacetModel().getDependencyKey() != null);
		boolean hasDependencyConnection = (!getModelSourceConnections().isEmpty());
		if(hasDependency) {
			if(!hasDependencyConnection) 
			return DependencyState.USED_INVISIBLE;
			else
			return DependencyState.USED;
		}
		return DependencyState.NOT_USED;
	}
	
	public void refreshScheme() {
		getFacetFigure().apply(getScheme());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
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
