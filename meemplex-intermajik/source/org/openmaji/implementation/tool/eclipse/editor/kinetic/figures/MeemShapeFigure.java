/*
 * @(#)MeemFigure.java
 * Created on 21/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>MeemFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemShapeFigure extends MeemFigure {
	//static private int OUTLINE_WIDTH = 3;

	static private Image defaultImage = Images.getIcon("meem48.gif");
	static private Image templateImage = Images.getIcon("meem_template48.gif");

//  /* (non-Javadoc)
//   * @see org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider#createFeedbackShape()
//   */
//  public Shape createFeedbackShape() {
//	  Hexagon feedback = new Hexagon();
//	  feedback.setSize(getIcon().getSize());
//	  feedback.setLineWidth(OUTLINE_WIDTH);
//	  feedback.setFill(true);
//	  feedback.setForegroundColor(ColorConstants.white);
//	  return feedback;
//  }

//	/* (non-Javadoc)
//	 * @see org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider#createSelectionShape(org.eclipse.draw2d.IFigure)
//	 */
//	public Shape createSelectionShape(IFigure figure) {
//		Shape selection = createFeedbackShape();
//		selection.setFill(false);
//		Dimension size = selection.getSize().getCopy();
//		figure.translateToAbsolute(size);
//		selection.setSize(size);
//		return selection;
//	}

	protected Image getDefaultImage() {
		return defaultImage;
	}
	
	protected Image getTemplateImage() {
		return templateImage;
	}
}
