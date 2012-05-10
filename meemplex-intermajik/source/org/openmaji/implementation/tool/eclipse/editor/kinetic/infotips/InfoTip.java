/*
 * @(#)InfoTip.java
 * Created on 26/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FontManager;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.FontDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.util.PlatformHelper;


/**
 * <code>InfoTip</code>.
 * <p>
 * @author Kin Wong
 */
public class InfoTip extends Figure {
	static public final int BORDER_MARGIN = 2;
	static public final int CONTENT_MARGIN = 16;
	
	static private final String PRIMARY_FONT_NAME = "Tahoma";
	static private final FontDescriptor headerDescriptor = new FontDescriptor(PRIMARY_FONT_NAME, PlatformHelper.getMinFontHeight(), SWT.BOLD);
	static private final FontDescriptor contentDescriptor = new FontDescriptor(PRIMARY_FONT_NAME, PlatformHelper.getMinFontHeight(), SWT.NORMAL);
		
	protected Label labelLayer;
	protected Figure contentLayer;
	private Font headerFont;
	private Font contentFont;
	
	/**
	 * Constructs an instance of <code>InfoTip</code>.
	 * <p>
	 * 
	 */
	public InfoTip() {
		setLayoutManager(new BorderLayout());
		setBorder(new MarginBorder(BORDER_MARGIN));

		labelLayer = createLabelLayer();
		add(labelLayer, BorderLayout.TOP);
		
		contentLayer = createContentLayer();
		add(contentLayer, BorderLayout.CENTER);
	}
	
	public void dispose() {
		if(headerFont != null) {
			FontManager.getInstance().checkIn(headerDescriptor);
		}
		
		if(contentFont != null) {
			FontManager.getInstance().checkIn(contentDescriptor);
		}
	}
	
	public Figure getContentPane() {
		return contentLayer;
	}

	protected void setIcon(Image image) {
		labelLayer.setIcon(image);
	}
	
	protected void setCaption(String caption) {
		labelLayer.setText(caption);
	}
	
	private Label createLabelLayer() {
		Label label = new Label();
		headerFont = FontManager.getInstance().checkOut(headerDescriptor);
		
		label.setFont(headerFont);
		label.setTextAlignment(PositionConstants.TOP);
		return label;
	}
	
	private Figure createContentLayer() {
		Figure content = new Figure();
		contentFont = FontManager.getInstance().checkOut(contentDescriptor);
		content.setFont(contentFont);
		content.setBorder(new MarginBorder(BORDER_MARGIN, BORDER_MARGIN + CONTENT_MARGIN, BORDER_MARGIN, BORDER_MARGIN));
		content.setLayoutManager(new ToolbarLayout(false));
		return content;
	}
}
