/*
 * @(#)WedgeConnectableEditPart.java
 * Created on 16/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.HorizontalAnchor;
import org.openmaji.implementation.tool.eclipse.editor.features.collapsible.CollapsibleConnectionCollector;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.ConnectionCollector;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;

/**
 * <code>WedgeConnectableEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class WedgeConnectableEditPart extends WedgeEditPart 
implements NodeEditPart {
		private HorizontalAnchor anchor;
		
	protected Wedge getConnectableWedgeModel() {
		return (Wedge)getModel();
	}
	/**
	 * Constructs an instance of <code>WedgeConnectableEditPart</code>.
	 * <p>
	 * @param wedge The wedge as the model.
	 */
	public WedgeConnectableEditPart(Wedge wedge) {
		super(wedge);
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.WedgeEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		IFigure figure = super.createFigure();
		anchor = new HorizontalAnchor(figure);
		return figure;
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new WedgeLayoutEditPolicy());
//		if(getWedgeModel().isSystemWedge()) 
//		removeEditPolicy(FeatureEditPolicy.CONNECTABLE_ROLE);
//		else 
//		installEditPolicy(FeatureEditPolicy.CONNECTABLE_ROLE, new ConnectableComponentEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#propertyChange(java.beans.PropertyChangeEvent)
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
		super.propertyChange(evt);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		if(isExpanded()) return Collections.EMPTY_LIST;
		
		List connections = new ArrayList();
		ConnectionCollector collector = new CollapsibleConnectionCollector();
		collector.collectSourceConnections(getModel(), connections);
		return connections;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		if(isExpanded()) return Collections.EMPTY_LIST;

		List connections = new ArrayList();
		ConnectionCollector collector = new CollapsibleConnectionCollector();
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
		return null;
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
		return null;
	}
}
