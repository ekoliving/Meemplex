/*
 * @(#)ConnectionFigureSchemeProvider.java
 * Created on 17/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.DependencyEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.EntryOfEditPart;


/**
 * <code>ConnectionFigureSchemeProvider</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionFigureSchemeProvider implements IFigureSchemeProvider {
	static protected final String PRIMARY_FONT_NAME = "Tahoma";
	static protected final String SECONDARY_FONT_NAME = "Tahoma";

	private FigureScheme dependencyScheme = new FigureScheme();
	private FigureScheme entryOfScheme = new FigureScheme();
	
	public ConnectionFigureSchemeProvider() {
		setDefault();
	}
	public void setDefault() {
		// Dependency 
		Color background = ColorConstants.black;
		FigureScheme scheme = dependencyScheme;
		scheme.setBorder(ColorConstants.black, 2);
		scheme.setColors(ColorConstants.yellow, background);
		scheme.setCaptionFont(SECONDARY_FONT_NAME, 10, SWT.BOLD, ColorConstants.yellow, background);
		scheme.setSelectionColor(ColorConstants.white);
		
		scheme = entryOfScheme;
		scheme.setBorder(ColorConstants.black, 2);
		scheme.setColors(new Color(Display.getDefault(), 255, 176, 98), background);
		scheme.setCaptionFont(SECONDARY_FONT_NAME, 10, SWT.BOLD, ColorConstants.yellow, background);
		scheme.setSelectionColor(ColorConstants.white);
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider#getScheme(org.eclipse.gef.EditPart)
	 */
	public FigureScheme getScheme(EditPart editPart) {
		if(editPart instanceof EntryOfEditPart) {
			return entryOfScheme;
		}
		else
		if(editPart instanceof DependencyEditPart) {
			return dependencyScheme;
		}
		//System.out.println("warning: unidentified edit part: " + editPart);
		return dependencyScheme;
	}
}
