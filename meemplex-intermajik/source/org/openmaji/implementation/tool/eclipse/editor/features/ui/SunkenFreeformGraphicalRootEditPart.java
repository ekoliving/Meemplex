/*
 * @(#)SunkenFreeformGraphicalRootEditPart.java
 * Created on 12/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.ui;

import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SimpleLoweredBorder;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;

/**
 * <code>SunkenFreeformGraphicalRootEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class SunkenFreeformGraphicalRootEditPart
	extends FreeformGraphicalRootEditPart
	 {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.FreeformGraphicalRootEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		FreeformViewport viewport = (FreeformViewport)super.createFigure();
		viewport.setBorder(new SimpleLoweredBorder());
		return viewport;
	}

}
