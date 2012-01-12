/*
 * @(#)IShapeProvider.java
 * Created on 11/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Shape;

/**
 * <code>IShapeProvider</code> defines the contract that allows the client of 
 * the implementation to retreive feedback and selection shape from the 
 * implementation.
 * <p>
 * @author Kin Wong
 */
public interface IShapeProvider {
	/**
	 * Creates a shape that represents the drag feedback image.
	 * @return Shape The Shape that represents the drag feedback image.
	 */
	Shape createFeedbackShape();
	/**
	 * Creates a shape that represents the selection image.
	 * @return Shape The shape that represents the selection image.
	 */
	Shape createSelectionShape(IFigure figure);
}
