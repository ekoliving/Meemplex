/*
 * @(#)OverviewOutlinePage.java
 * Created on 8/04/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.common.pages;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * <code>OverviewOutlinePage</code>.
 * <p>
 * @author Kin Wong
 */
public class OverviewOutlinePage extends Page implements IContentOutlinePage {
	private Canvas overview;
	/** the root edit part */
	private ScalableFreeformRootEditPart rootEditPart;
	/** the thumbnail */
	private Thumbnail thumbnail;

	public OverviewOutlinePage(ScalableFreeformRootEditPart rootEditPart) {
		this.rootEditPart = rootEditPart;
	}
	
	/* (non-Javadoc)
	* @see ISelectionProvider#addSelectionChangedListener
	* (ISelectionChangedListener)
	*/
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{}
	
	/* (non-Javadoc)
	* @see ISelectionProvider#removeSelectionChangedListener
	* (ISelectionChangedListener)
	*/
	public void removeSelectionChangedListener(
	ISelectionChangedListener listener){}

	/* (non-Javadoc)
	* @see IPage#createControl(Composite)
	132 Eclipse Development using the Graphical Editing Framework and the Eclipse Modeling Framework
	*/
	public void createControl(Composite parent) {
	//	   create canvas and lws
		overview = new Canvas(parent, SWT.NONE);
		LightweightSystem lws = new LightweightSystem(overview);
	//	   create thumbnail
		thumbnail = new ScrollableThumbnail((Viewport) rootEditPart.getFigure());
		thumbnail.setBorder(new MarginBorder(3));
		thumbnail.setSource(
		rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
		lws.setContents(thumbnail);
	}
	
	/* (non-Javadoc)
	* @see org.eclipse.ui.part.IPage#dispose()
	*/
	public void dispose() {
		if (null != thumbnail) thumbnail.deactivate();
		super.dispose();
	}
	
	/* (non-Javadoc)
	* @see org.eclipse.ui.part.IPage#getControl()
	*/
	public Control getControl() {
		return overview;
	}
	
	/* (non-Javadoc)
	* @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	*/
	public ISelection getSelection() {
		return StructuredSelection.EMPTY;
	}
	
	/* (non-Javadoc)
	* @see org.eclipse.ui.part.IPage#setFocus()
	*/
	public void setFocus() {
	if (getControl() != null)
	getControl().setFocus();
	}
	
	/* (non-Javadoc)
	* @see ISelectionProvider#setSelection(ISelection)
	*/
	public void setSelection(ISelection selection)
	{}
}
