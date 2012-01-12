/*
 * Created on 10/04/2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.common.util.ColorTransformer;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.CategoryIconicEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;


/**
 * @author Kin Wong
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CategoryFigureSchemeProvider extends MeemFigureSchemeProvider {
	private static final long serialVersionUID = 6424227717462161145L;

	static private CategoryFigureSchemeProvider instance;
	
	static public IFigureSchemeProvider getInstance() {
		if(instance == null) instance = new CategoryFigureSchemeProvider();
		return instance;
	}

	protected MeemScheme createDefaultReadyScheme() {
		MeemScheme category = super.createDefaultReadyScheme();
		
		FigureScheme scheme = category.getMeemScheme();
//		Color textDescription = new Color(Display.getDefault(), ColorConstants.yellow.getRGB());
//		Color darkerCaption = new Color(Display.getDefault(), 51, 0, 51);

		// Category
		scheme = category.getMeemScheme();
		Color background = new Color(Display.getDefault(), 155, 92, 209);
		scheme.setColors(ColorConstants.white, background);
		
		// Wedge in Category
		scheme = category.getWedgeScheme();
		scheme.setBorderColor(background);
		background = ColorTransformer.brighten(background, 0.4f);
		scheme.setColors(ColorConstants.black, background);
		
		// Facet in Category
		scheme = category.getFacetScheme();
		scheme.setBorderColor(background);
		background = ColorTransformer.brighten(background, 0.4f);
		//new Color(Display.getDefault(), 193, 165, 210);
		Color foreground = new Color(Display.getDefault(), ColorConstants.white.getRGB());
		scheme.setColors(foreground, background);
		return category;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider#getScheme(org.eclipse.draw2d.IFigure)
	 */
	public FigureScheme getScheme(EditPart editPart) {
		
		if(editPart instanceof CategoryIconicEditPart) {
			Category category = (Category)editPart.getModel();
			return getMeemScheme(category.getProxy());
		}
		else
		return super.getScheme(editPart);
	}
}
