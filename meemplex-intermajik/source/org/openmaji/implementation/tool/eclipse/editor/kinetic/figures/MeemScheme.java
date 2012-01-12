/*
 * @(#)MeemScheme.java
 * Created on 28/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.util.ColorTransformer;

/**
 * <code>MeemScheme</code> represents figure schemes related for painting meem 
 * figure, particularly the detailed view.
 * <p>
 * @author Kin Wong
 */
public class MeemScheme implements Cloneable {
	private FigureScheme meemScheme = new FigureScheme();
	private FigureScheme wedgeScheme = new FigureScheme();
	private FigureScheme facetScheme = new FigureScheme();
	
	/**
	 * Gets the figure scheme for painting meem figure.
	 * @return FigureScheme the figure scheme for painting meem figure.
	 */
	public FigureScheme getMeemScheme() {
		return meemScheme;
	}
	/**
	 * Gets the figure scheme for painting wedge figure.
	 * @return FigureScheme the figure scheme for painting wedge figure.
	 */
	public FigureScheme getWedgeScheme() {
		return wedgeScheme;
	}
	/**
	 * Gets the figure scheme for painting facet figure.
	 * @return FigureScheme the figure scheme for painting facet figure.
	 */
	public FigureScheme getFacetScheme() {
		return facetScheme;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch(CloneNotSupportedException e) {
			return null;
		}
	}
	
	MeemScheme createBrighten() {
		float change = 0.1f;
		MeemScheme brighten = (MeemScheme)clone();
		brighten.meemScheme.setBackground(ColorTransformer.brighten(meemScheme.getBackground(), change));
		brighten.meemScheme.setCaptionBackground(ColorTransformer.brighten(meemScheme.getBackground(), change));

		brighten.wedgeScheme.setBackground(ColorTransformer.brighten(wedgeScheme.getBackground(), change));
		brighten.wedgeScheme.setCaptionBackground(ColorTransformer.brighten(wedgeScheme.getBackground(), change));

		brighten.facetScheme.setBackground(ColorTransformer.brighten(facetScheme.getBackground(), change));
		brighten.facetScheme.setCaptionBackground(ColorTransformer.brighten(facetScheme.getBackground(), change));
		
		return brighten;
	}
}
