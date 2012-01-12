/*
 * @(#)AbstractLabel.java
 * Created on 25/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.labels;

import org.eclipse.draw2d.AncestorListener;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FontManager;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.FontDescriptor;
import org.openmaji.implementation.tool.eclipse.editor.features.util.PlatformHelper;


/**
 * <code>AbstractLabel</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class AbstractLabel extends Figure implements AncestorListener {
	private static FontDescriptor fontDescriptor = new FontDescriptor("tahoma", PlatformHelper.getMinFontHeight(), SWT.NORMAL);
	
	protected RectangleFigure backdrop;
	protected Label label;
	protected Figure owner;	// The Owner of this label
	boolean paint = true;
	Font cachedFont;

	/**
	 * Constructs an instance of <code>AbstractLabel</code>.
	 * <p>
	 * @param owner The owner of this label.
	 */
	public AbstractLabel(Figure owner) {
		cachedFont = FontManager.getInstance().checkOut(fontDescriptor);
		setFont(cachedFont);
		backdrop = new RectangleFigure();
		FigureUtilities.makeGhostShape(backdrop);
		backdrop.setOutline(false);
		label = new Label();
		add(backdrop);
		add(label);
		setOwner(owner);
	}
	
	/**
	 * Releases any OS resources used by the figure.
	 */
	public void dispose() {
		if (cachedFont != null) {
			FontManager.getInstance().checkIn(fontDescriptor);
			cachedFont = null;
		}
	}

	public Figure getOwner() {
		return owner;
	}
	
	public void setOwner(Figure owner) {
		if(this.owner == owner) return;
		
		if(this.owner != null) {
			this.owner.removeAncestorListener(this);
		}
		this.owner = owner;
		if(this.owner != null) {
			this.owner.addAncestorListener(this);
		}
		update();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#addNotify()
	 */
	public void addNotify() {
		super.addNotify();
		update();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#validate()
	 */
	public void validate() {
		super.validate();
		update();
	}
	
	/**
	 * Sets the text of this label.
	 * @param text The text of this label.
	 */
	public void setText(String text) {
		label.setText(text);
		update();
	}
	
	protected boolean getVisibility() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#useLocalCoordinates()
	 */
	protected boolean useLocalCoordinates() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.AncestorListener#ancestorAdded(org.eclipse.draw2d.IFigure)
	 */
	public void ancestorAdded(IFigure ancestor) {}

	/**
	 * Overridden to update the label position.
	 * @see org.eclipse.draw2d.AncestorListener#ancestorMoved(org.eclipse.draw2d.IFigure)
	 */
	public void ancestorMoved(IFigure ancestor) {
		update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.AncestorListener#ancestorRemoved(org.eclipse.draw2d.IFigure)
	 */
	public void ancestorRemoved(IFigure ancestor) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#paintClientArea(org.eclipse.draw2d.Graphics)
	 */
	protected void paintClientArea(Graphics graphics) {
		if(paint) super.paintClientArea(graphics);
	}
	
	abstract protected void update();
}
