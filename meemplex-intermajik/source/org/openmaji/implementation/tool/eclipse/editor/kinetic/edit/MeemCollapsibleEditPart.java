/*
 * @(#)MeemCollapsibleEditPart.java
 * Created on 7/03/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.openmaji.implementation.tool.eclipse.client.VariableMapProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.ConnectionCollector;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.ContainerConnectionCollector;
import org.openmaji.implementation.tool.eclipse.editor.features.editpolicies.BringToFrontEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;


/**
 * @author Kin Wong
 */
public class MeemCollapsibleEditPart extends MeemEditPart 
implements 	NodeEditPart {
	protected ConnectionAnchor anchor;
	
	/**
	 * Constructs an instance of meem edit part for configuration.
	 * @param meem The model that associates with this edit part.
	 * @param variableMapProxy The variable map.
	 */
	public MeemCollapsibleEditPart(Meem meem, VariableMapProxy variableMapProxy) {
		super(meem, variableMapProxy);
	}	
	
	protected Meem getConnectableMeemModel() {
		return (Meem)getModel();
	}
	
	/**
	 * Overridden to support multiple-view mode.<p>
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new MeemCollapsibleLayoutEditPolicy());
		//installEditPolicy(FeatureEditPolicy.CONNECTABLE_ROLE, new ConnectableComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new MeemNodeEditPolicy());
		installEditPolicy(FeatureEditPolicy.BRING_TO_FRONT_ROLE, new BringToFrontEditPolicy());
		installEditPolicy(FeatureEditPolicy.VIEW_MODE_ROLE, new ViewModeEditPolicy(getConnectableMeemModel()));
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(Meem.ID_INNER_SOURCE_CONNECTIONS.equals(prop)) {
			if(isCollapsed()) refreshSourceConnections();
		}
		else
		if(Meem.ID_INNER_TARGET_CONNECTIONS.equals(prop)) {
			if(isCollapsed()) refreshTargetConnections();
		}
		else
		if(Meem.ID_VIEW_MODE.equals(prop)) {
			Object model = getModel();
			EditPartViewer viewer = getViewer();
			ElementContainer container = (ElementContainer)getParent().getModel();
			int selection = getSelected();
			container.refreshChild(getModel());
			if(selection != SELECTED_NONE) {
				  EditPart editPart = (EditPart)viewer.getEditPartRegistry().get(model);
				  viewer.appendSelection(editPart);
			  }
		}
		else
		if(Meem.ID_ENTRY_TO_CATEGORY.equals(prop)) {
			// The meem has been added or removed from a Category.
			refreshSourceConnections();
		}
		else
		super.propertyChange(evt);
	}
	/**
	 * Overridden to create configuration specific meem figure.
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure figure = super.createFigure();
		anchor = new ChopboxAnchor(figure);
		return figure;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		List connections = new ArrayList(getConnectableMeemModel().getEntriesToCategory());
		if(isCollapsed()) {
			ConnectionCollector collector = new ContainerConnectionCollector();
			collector.collectSourceConnections(getModel(), connections);
		}
		return connections;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		if(isExpanded()) return Collections.EMPTY_LIST;
		List connections = new ArrayList();
		ConnectionCollector collector = new ContainerConnectionCollector();
		collector.collectTargetConnections(getModel(), connections);
		return connections;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return anchor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if(!(request instanceof CreateConnectionRequest)) return null;
		CreateConnectionRequest createRequest = (CreateConnectionRequest)request;
		
		Command command = createRequest.getStartCommand();
		if(command instanceof CategoryEntryCreateCommand) return anchor;
		
		return null;		
	}
}
