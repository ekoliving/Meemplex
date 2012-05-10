/*
 * @(#)WorksheetEditPart.java
 * Created on 26/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Image;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.BackdropFreeformLayer;
import org.openmaji.implementation.tool.eclipse.editor.features.ui.BackdropHightLightEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Worksheet;
import org.openmaji.implementation.tool.eclipse.images.Images;


/**
 * <code>WorksheetEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class WorksheetEditPart extends DiagramEditPart {
	static private Image NORMAL_BACKDROP;
	static private Image HIGHLIGHT_BACKDROP;
	
	/**
	 * Constructs an instance of <code>WorksheetEditPart</code>.
	 * <p>
	 * @param worksheet The worksheet associates with this editpart.
	 */
	public WorksheetEditPart(Worksheet worksheet) {
		super(worksheet);
		prepareImages();
	}
	
	private void prepareImages() {
		if(NORMAL_BACKDROP == null)
			NORMAL_BACKDROP = Images.IMAGE_WORKSHEET.createImage();
			
		if(HIGHLIGHT_BACKDROP == null)
			HIGHLIGHT_BACKDROP = Images.IMAGE_WORKSHEET_HL.createImage();
	}
	
	/**
	 * Gets the model as worksheet.
	 * @return Worksheet The model as worksheet.
	 */
	protected Worksheet getWorksheetModel() {
		return (Worksheet)getModel();
	}
	
	/**
	 * Gets the figure as <code>BackdropFreeformLayer</code>.
	 * @return BackdropFreeformLayer the figure as 
	 * <code>BackdropFreeformLayer</code>.
	 */
	protected BackdropFreeformLayer getWorksheetFigure() {
		return (BackdropFreeformLayer)getFigure();
	}

	protected FigureScheme getScheme() {
		IFigureSchemeProvider schemeProvider = (IFigureSchemeProvider)
		EditPartHelper.findAncestor(getParent(), IFigureSchemeProvider.class);
		if(schemeProvider != null) 
		return schemeProvider.getScheme(this);
		else
		return null;
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		Figure figure = new BackdropFreeformLayer(NORMAL_BACKDROP);
		figure.setOpaque(true);
		figure.setLayoutManager(new FreeformLayout());
		return figure;
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.DiagramEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
//		installEditPolicy( FeatureEditPolicy.CONNECTABLE_ROLE, 
//							new ConnectableComponentEditPolicy());
		installEditPolicy(	EditPolicy.SELECTION_FEEDBACK_ROLE, 
							new BackdropHightLightEditPolicy(
								NORMAL_BACKDROP, 
								HIGHLIGHT_BACKDROP));
	}

	/**
	 * Overridden to handle Meem.ID_MEEM_STATE to change the backdrop image.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementContainerEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
//		String prop = evt.getPropertyName();
		
		/*
		if(Meem.ID_MEEM_STATE.equals(prop)) {
			refreshScheme();
		}
		else
		*/
		super.propertyChange(evt);
	}
	
	protected void refreshScheme() {
		//FigureScheme scheme = getScheme();
		//BackdropFreeformLayer figure = getWorksheetFigure();
		//figure.setImage(Icon.class, "worksheet.bmp");
	}
}
