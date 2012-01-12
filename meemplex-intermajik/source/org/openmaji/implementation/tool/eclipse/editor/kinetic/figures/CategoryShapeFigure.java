/*
 * Created on 10/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.images.Images;



/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CategoryShapeFigure extends MeemFigure {
	//static private int OUTLINE_WIDTH = 3;

	static private Image defaultImage = Images.getIcon("category48.gif");
	static private Image templateImage = Images.getIcon("category_template48.gif");

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider#createFeedbackShape()
	 */
//	public Shape createFeedbackShape() {
//		Ellipse feedback = new Ellipse();
//		feedback.setSize(getIcon().getSize());
//		feedback.setLineWidth(OUTLINE_WIDTH);
//		feedback.setFill(true);
//		feedback.setForegroundColor(ColorConstants.white);
//		return feedback;
//	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider#createSelectionShape(org.eclipse.draw2d.IFigure)
	 */
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
