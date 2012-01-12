/*
 * @(#)MeemPlexDetailedFigure.java
 * Created on 23/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.util.ColorTransformer;
import org.openmaji.implementation.tool.eclipse.editor.features.flat.FlatScrollPane;


/**
 * <code>MeemPlexDetailedFigure</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPlexDetailedFigure extends Figure {
	static private int MARGIN = 2;
	
	private Label caption;
	private Figure meemHost;
	private Figure worksheetHost;
//	private Label meemHostStatus;
	
	/**
	 * Constructs an instance of <code>MeemPlexDetailedFigure</code>.
	 * <p>
	 */
	public MeemPlexDetailedFigure() {
		setOpaque(true);
		BorderLayout layout = new BorderLayout();

		setLayoutManager(layout);
		
		// Create caption
		caption = new Label();
		caption.setLabelAlignment(PositionConstants.LEFT);
		caption.setBorder(new MarginBorder(0, MARGIN << 1, 0, MARGIN << 1));
		
		// Create Meem host
		meemHost = new Figure();
		meemHost.setBorder(new MarginBorder(MARGIN));
		meemHost.setLayoutManager(new ToolbarLayout(false));
		//meemHost.setMinimumSize(new Dimension(50,-1));
		//meemHost.setPreferredSize(new Dimension(50,100));
		
		// Create Worksheet host
		FlatScrollPane scrollpane = new FlatScrollPane();
		worksheetHost = new FreeformLayer();
		worksheetHost.setLayoutManager(new FreeformLayout());
		Border border = new CompoundBorder(
							new MarginBorder(MARGIN),
							new LineBorder(ColorConstants.black, 1));
		scrollpane.setBorder(border);
		scrollpane.setViewport(new FreeformViewport());
		scrollpane.setContents(worksheetHost);

	
		// Add all figures		
		add(caption, BorderLayout.TOP);
		add(meemHost, BorderLayout.LEFT);
		add(scrollpane, BorderLayout.CENTER);
	}
	
	/**
	 * Applies the figure scheme to this figure.
	 * @param scheme The figure scheme to be applied to this figure.
	 */
	public void apply(FigureScheme scheme) {
		scheme.applyColors(this);
		setForegroundColor(ColorTransformer.darken(scheme.getBackground(), 0.5f));
		setBorder(new LineBorder(scheme.getBorderColor(), scheme.getBorderWidth()));
		scheme.applyCaptionColors(caption);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return false;
	}
	
	/**
	 * Gets the caption of the MeemPlex figure.
	 * @return Label The caption of the MeemPlex figure.
	 */
	public Label getCaption() {
		return caption;
	}
	
	/**
	 * Gets the figure that hosts the worksheet.
	 * @return Figure The figure that hosts the worksheet.
	 */
	public Figure getWorksheetPane() {
		return worksheetHost;
	}
	
	/**
	 * Gets the figures that hosts the wedges.
	 * @return Figure The figure that hosts the wedges.
	 */
	public Figure getMeemPane() {
		return meemHost;
	}
}
