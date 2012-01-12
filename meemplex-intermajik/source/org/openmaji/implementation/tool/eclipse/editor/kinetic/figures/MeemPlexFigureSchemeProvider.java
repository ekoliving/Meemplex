/*
 * @(#)MeemPlexFigureSchemeProvider.java
 * Created on 26/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemPlexDetailedEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemPlex;


/**
 * <code>MeemPlexFigureSchemeProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPlexFigureSchemeProvider extends MeemFigureSchemeProvider {
	private static final long serialVersionUID = 6424227717462161145L;

	static private MeemPlexFigureSchemeProvider instance;
	
	/**
	 * @return IFigureSchemeProvider
	 */
	public static IFigureSchemeProvider getInstance() {
		if(instance == null) instance = new MeemPlexFigureSchemeProvider();
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.MeemFigureSchemeProvider#createDefaultUsefulScheme()
	 */
	protected MeemScheme createDefaultLoadedScheme() {
		MeemScheme category = super.createDefaultLoadedScheme();
		
		FigureScheme scheme = category.getMeemScheme();
//		Color textDescription = new Color(Display.getDefault(), ColorConstants.yellow.getRGB());
//		Color darkerCaption = new Color(Display.getDefault(), 0, 175, 50);

		// MeemPlex
		scheme = category.getMeemScheme();
		Color background = new Color(Display.getDefault(), 24, 182, 71);
		scheme.setColors(ColorConstants.yellow, background);
		
		// Wedge in MeemPlex
		scheme = category.getWedgeScheme();
		scheme.setBorderColor(background);
		background = new Color(Display.getDefault(), 101, 235, 141);
		scheme.setColors(ColorConstants.black, background);
		
		// Facet in MeemPlex
		scheme = category.getFacetScheme();
		scheme.setBorderColor(background);
		background = new Color(Display.getDefault(), 167, 243, 190);
		Color foreground = new Color(Display.getDefault(), ColorConstants.white.getRGB());
		scheme.setColors(foreground, background);
		return category;
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider#getScheme(org.eclipse.draw2d.IFigure)
	 */
	public FigureScheme getScheme(EditPart editPart) {
		if(editPart instanceof MeemPlexDetailedEditPart) {
			MeemPlex meemplex = (MeemPlex)editPart.getModel();
			return getMeemScheme(meemplex.getProxy());
		}
		else
		return super.getScheme(editPart);
	}

}
