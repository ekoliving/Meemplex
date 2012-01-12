/*
 * @(#)MeemCollapsibleFigure.java
 * Created on 14/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.BorderLayout;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.CollapsibleFigure;


/**
 * <code>MeemCollapsibleFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemCollapsibleFigure extends CollapsibleFigure {
	protected MeemStateButton stateButton;
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.CollapsibleFigure#createLabelLayer()
	 */
	protected void createLabelLayer() {
		super.createLabelLayer();
		stateButton = new MeemStateButton();
		labelLayer.add(stateButton);
		labelLayer.setConstraint(stateButton, BorderLayout.RIGHT);
	}

	public MeemStateButton getStateButton() {
		return stateButton;
	}
}
