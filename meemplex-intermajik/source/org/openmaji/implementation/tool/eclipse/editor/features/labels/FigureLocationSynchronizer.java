/*
 * @(#)FigureLocationSynchronizer.java
 * Created on 8/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.labels;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openmaji.implementation.tool.eclipse.editor.features.util.FigureHelper;


/**
 * <code>FigureLocationSynchronizer</code> automatically locates the target 
 * Figure based on the position of the owner figure. The figures can be in 
 * different layers.<p>
 * @author Kin Wong
 * @see org.eclipse.draw2d.Figure
 * @see org.eclipse.draw2d.PositionConstants
 */
public class FigureLocationSynchronizer {
	private Figure owner;
	private Figure target;
	private int alignment =  PositionConstants.CENTER|PositionConstants.MIDDLE;
	private int padding = 2;
	
	private AncestorListener ancestorListener = new AncestorListener() {
		public void ancestorAdded(IFigure ancestor) {}
		public void ancestorMoved(IFigure ancestor) {update();}
		public void ancestorRemoved(IFigure ancestor) {}
	};
	
	/**
	 * Constructs an instance of <code>FigureLocationSynchronizer</code>.<p>
	 */
	public FigureLocationSynchronizer() {
	}
	
	/**
	 * Constructs an instance of <code>FigureLocationSynchronizer</code>.<p>
	 * @param owner
	 * @param target
	 */
	public FigureLocationSynchronizer(Figure owner, Figure target) {
		setOwner(owner);
		setTarget(target);
	}
	
	/**
	 * Sets the owner figure of this synchronizer.<p>
	 * The target figure will be positioned according to the position of this 
	 * owner.<p>
	 * @param owner The owner figure of this synchronizer. 
	 */
	public void setOwner(Figure owner) {
		if(this.owner == owner) return;
		
		if(this.owner != null) {
			this.owner.removeAncestorListener(ancestorListener);
			this.owner = null;
		}
		this.owner = owner;
		owner.addAncestorListener(ancestorListener);
		update();
	}
	
	/**	
	 * Sets the target figure of this synchronizer.<p>
	 * The target figure will be positioned according to the position of this 
	 * owner.<p>
	 * @param target The target figure of this synchronizer.
	 */
	public void setTarget(Figure target) {
		this.target = target;
		update();
	}
	
	/**
	 * Sets the alignment of the positioning.<p>
	 * @param alignment The alignment of the positioning.
	 * @see PositionConstants
	 */
	public void setAlignment(int alignment) {
		this.alignment = alignment;
		update();
	}
	
	/*
	private boolean getOwnerVisibility() {
		return FigureHelper.isVisibleInRootFigure(owner);
	}
	 */
	
	/**
	 * Updates the position of the target figure according to the source figure 
	 * and alignment.<p>
	 */
	private void update() {
		if(	(owner == null) || 
				(target == null) || 
				(!FigureHelper.isVisibleInRootFigure(owner))) return;
		Rectangle ownerBounds = owner.getBounds().getCopy();
		owner.translateToAbsolute(ownerBounds);
		
		Dimension targetSize = target.getBounds().getSize();
		target.translateToAbsolute(targetSize);
		
		// Horizontal position
		int x, y;
		switch (alignment & PositionConstants.LEFT_CENTER_RIGHT) {
			case PositionConstants.LEFT:
			x = ownerBounds.x - targetSize.width - padding;
			break;
				
			case PositionConstants.RIGHT:
			x = ownerBounds.x + ownerBounds.width + padding;
			break;
				
			default:
			x = (ownerBounds.width - targetSize.width) / 2 + ownerBounds.x;
			break;
		}
		
		switch (alignment & PositionConstants.TOP_MIDDLE_BOTTOM) {
			case PositionConstants.TOP:
			y = ownerBounds.y - targetSize.height - padding;
			break;
			
			case PositionConstants.BOTTOM:
			y = ownerBounds.bottom() + padding;
			break;
			
			default:
			y = (ownerBounds.height - targetSize.height) / 2 + ownerBounds.y;
			break;
		}
		Point targetLocation = new Point(x,y);
		target.translateToRelative(targetLocation);
		target.setLocation(targetLocation);
	}
}
