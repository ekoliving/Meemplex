/*
 * @(#)FigureHelper.java
 * Created on 2/06/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.util;


import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * <code>FigureHelper</code> contains a set of generic methods to assist in 
 * Figure related implementation.
 * <p>
 * @author Kin Wong
 */
public class FigureHelper {
	/**
	 * Finds a class or interface implemented by the figure or any of its 
	 * ancestor, start from the youngest. 
	 * @param figure The figure to start the search.
	 * @param type The type looking for.
	 * @return Object The ancestor of this figure that implements the type, null
	 * otherwise.
	 */
	static public Object findAncestor(IFigure figure, Class type) {
		if(figure == null) return null;
		if(type.isInstance(figure)) return figure;
		return findAncestor(figure.getParent(), type);
	}

	/**
	 * Checks whether a figure is currently visible in the root figure by taking
	 * all the ancestor viewports into account. 
	 * @param figure The figure in which the visibility is in question.
	 * @return boolean true if the figure is visible in view, false otherwise.
	 */
	static public boolean isVisibleInRootFigure(IFigure figure) {
		return isVisibleInRootFigure(figure, figure.getBounds());
	}
	
	/**
	 * Checks whether the given rectangle in the figure is currently visible in 
	 * the root figure by taking viewports of all ancestors into account.
	 * @param figure
	 * @param rect
	 * @return boolean
	 */
	static public boolean isVisibleInRootFigure(IFigure figure, Rectangle rect) {
		boolean visible = true;
		Rectangle bounds = rect.getCopy();
		figure.translateToAbsolute(bounds);

		Rectangle viewBounds = new Rectangle();
		do {		
			ScrollPane scrollPane = (ScrollPane)
				FigureHelper.findAncestor(figure, ScrollPane.class);
			if(scrollPane == null) break;
			
			viewBounds.setBounds(scrollPane.getViewport().getBounds());
			scrollPane.translateToAbsolute(viewBounds);
			visible = viewBounds.touches(bounds);
			if(!visible) break;
			figure = scrollPane.getParent();
		}
		while(figure != null);
		return visible;
	}
	
	static public boolean isVisibleInRootFigure(IFigure figure, Point point) {
		if(figure == null) return true;
		boolean visible = true;
		Point absPoint = point.getCopy();
		figure.translateToAbsolute(absPoint);

		Rectangle viewBounds = new Rectangle();
		do {		
			ScrollPane scrollPane = (ScrollPane)
				FigureHelper.findAncestor(figure, ScrollPane.class);
			if(scrollPane == null) break;
			
			viewBounds.setBounds(scrollPane.getViewport().getBounds());
			scrollPane.translateToAbsolute(viewBounds);
			visible = viewBounds.contains(absPoint);
			
			if(!visible) break;
			figure = scrollPane.getParent();
		}
		while(figure != null);
		return visible;
	}
	
	static public boolean isAnchorVisibleInRootFigure(ConnectionAnchor anchor, Point referencePoint) {
		if(anchor.getOwner() == null) return false;
		return isVisibleInRootFigure(anchor.getOwner(), anchor.getLocation(referencePoint));
	}
	
	static public boolean isAnchorsVisibleInRootFigure(PolylineConnection connection) {
		ConnectionAnchor sourceAnchor = connection.getSourceAnchor();
		ConnectionAnchor targetAnchor = connection.getTargetAnchor();
		if((sourceAnchor == null)||(targetAnchor == null)) return true;

		return 	isAnchorVisibleInRootFigure(targetAnchor, sourceAnchor.getReferencePoint()) && 
						isAnchorVisibleInRootFigure(sourceAnchor, targetAnchor.getReferencePoint());
	}
}
