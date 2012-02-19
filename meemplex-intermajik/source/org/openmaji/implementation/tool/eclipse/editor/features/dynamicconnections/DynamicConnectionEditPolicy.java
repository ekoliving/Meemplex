/*
 * @(#)DynamicConnectionEditPolicy.java
 * Created on 12/06/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.dynamicconnections;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.util.FigureHelper;


/**
 * <code>DynamicConnectionEditPolicy</code> shows and hides the connection 
 * figure according to the visibility of its owners. The owners of the 
 * connection figure are defined as both the source and target anchor.
 * <p>
 * @author Kin Wong
 */
public class DynamicConnectionEditPolicy extends AbstractEditPolicy
	implements AncestorListener {

	protected PolylineConnection getConnection() {
		return (PolylineConnection)((GraphicalEditPart)getHost()).getFigure();
	}

	/**
	 * Overridden to add itself as a ancestor listener of the figure.
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	public void activate() {
		getConnection().addAncestorListener(this);
		super.activate();
	}
	
	/** 
	 * Overridden to remove itself from the ancestor listener list of the 
	 * figure.
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		getConnection().removeAncestorListener(this);
		super.deactivate();
	}
	
	/**
	 * Overridden to update the visibility of the connection.
	 * @see org.eclipse.draw2d.AncestorListener#ancestorMoved(org.eclipse.draw2d.IFigure)
	 */
	public void ancestorMoved(IFigure ancestor) {updateVisibility(); }
	public void ancestorAdded(IFigure ancestor) {}
	public void ancestorRemoved(IFigure ancestor) {}
	
	/**
	 * Updates the visibility of the connection figure.
	 */
	protected void updateVisibility() {
		((Figure)getConnection()).setVisible(getVisibility());
	}
	
	/**
	 * Gets the desired visibility of the connection figure.
	 * @return boolean true is the connection figure should be visible, false
	 * otherwise.
	 */	
	protected boolean getVisibility() {
		return (getHost().getSelected() != 0) || isOwnersVisible();
	}
	
	protected ConnectionAnchor getTargetAnchor() {
		return getConnection().getTargetAnchor();
	}
	
	protected ConnectionAnchor getSourceAnchor() {
		return getConnection().getSourceAnchor();
	}
	
	protected boolean isOwnersVisible() {
		if((getTargetAnchor() == null) || (getSourceAnchor() == null)) return true;
		return 	isAnchorVisible(getTargetAnchor(), getSourceAnchor().getReferencePoint()) && 
				isAnchorVisible(getSourceAnchor(), getTargetAnchor().getReferencePoint());
	}
	
	static private boolean isAnchorVisible(ConnectionAnchor anchor, Point referencePoint) {
		return FigureHelper.isVisibleInRootFigure(anchor.getOwner(), anchor.getLocation(referencePoint));
	}
}
