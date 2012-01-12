/*
 * @(#)ScalableFreeformLabelRootEditPart.java
 * Created on 21/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.labels;

import org.eclipse.draw2d.LayeredPane;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;

/**
 * <code>ScalableFreeformLabelRootEditPart</code> provides an additional layer 
 * with label support.
 * <p>
 * @author Kin Wong
 */
public class ScalableFreeformLabelRootEditPart
	extends ScalableFreeformRootEditPart implements LayerConstants {

	/**
	 * Overridden to add an label layer on top all layers.
	 */
	protected LayeredPane createPrintableLayers() {
		LayeredPane layeredPane = super.createPrintableLayers();
		layeredPane.add(new LabelLayer(), LABEL_LAYER);
		return layeredPane;
	}
}
