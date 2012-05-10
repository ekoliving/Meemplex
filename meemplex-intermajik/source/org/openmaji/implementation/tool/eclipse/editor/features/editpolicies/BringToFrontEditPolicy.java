/*
 * @(#)BringToFrontEditPolicy.java
 * Created on 30/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.editpolicies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
//import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

/**
 * <code>BringToFrontEditPolicy</code> is a simple edit policy that provides the 
 * ability to bring the host figure to the front when it's clicked. The layout
 * manager of the parent figure must use rectangle as the constraint.
 * <p>
 * <code>FeatureEditPolicy.BRING_TO_FRONT_ROLE</code> is defined to avoid role 
 * name conflict with other edit policies. 
 * <p> 
 * @author Kin Wong
 */
public class BringToFrontEditPolicy 
	extends GraphicalEditPolicy 
	implements MouseListener {

	/**
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#activate()
	 */
	public void activate() {
		super.activate();
		IFigure figure = getHostFigure();
		figure.addMouseListener(this);
	}
	
	/**
	 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#deactivate()
	 */
	public void deactivate() {
		IFigure figure = getHostFigure();
		figure.removeMouseListener(this);
		super.deactivate();
	}

	/**
	 * @see org.eclipse.draw2d.MouseListener#mouseDoubleClicked(org.eclipse.draw2d.MouseEvent)
	 */
	public void mouseDoubleClicked(MouseEvent me) {
	}

	/**
	 * Overridden to bring the host figure to the front in its parent figure.
	 * @see org.eclipse.draw2d.MouseListener#mousePressed(org.eclipse.draw2d.MouseEvent)
	 */
	public void mousePressed(MouseEvent me) {
		IFigure figure = getHostFigure();
		IFigure parent = figure.getParent();
		if(parent == null) return;
		if(parent.getLayoutManager() == null) return;
		
//		Rectangle bounds = figure.getBounds().getCopy();
		//bounds.setSize(-1,-1);
		
		Object constraint = parent.getLayoutManager().getConstraint(figure);
		parent.remove(figure);
		parent.add(figure);
		parent.getLayoutManager().setConstraint(figure, constraint);
	}

	/**
	 * @see org.eclipse.draw2d.MouseListener#mouseReleased(org.eclipse.draw2d.MouseEvent)
	 */
	public void mouseReleased(MouseEvent me) {
	}

}
