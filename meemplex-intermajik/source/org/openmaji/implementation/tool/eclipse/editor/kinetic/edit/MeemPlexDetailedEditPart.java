/*
 * @(#)MeemplexCollapsibleEditPart.java
 * Created on 23/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;


import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleLayoutEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementContainerEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ReverseHighlightEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.SimpleSelectionHandlesEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.editpolicies.BringToFrontEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemPlexDetailedFigure;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemPlexFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemPlex;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;

//import org.openmaji.implementation.tool.eclipse.editor.features.ui.BackdropFreeformLayer;

/**
 * <code>MeemplexCollapsibleEditPart</code>.
 * <p>
 * @author Kin Wong
 */
public class MeemPlexDetailedEditPart extends ElementContainerEditPart {
	/**
	 * Constructs an instance of <code>MeemplexCollapsibleEditPart</code>.
	 * <p>
	 * @param meemplex The meemplex model associates with this editpart.
	 */
	public MeemPlexDetailedEditPart(MeemPlex meemplex) {
		setModel(meemplex);
	}
	
	/**
	 * Gets the model as meemplex.
	 * @return MeemPlex The model as meemplex.
	 */
	public MeemPlex getMeemPlexModel() {
		return (MeemPlex)getModel();
	}
	
	private FigureScheme getScheme() {
		return MeemPlexFigureSchemeProvider.getInstance().getScheme(this);
	}
	/**
	 * Overridden to returns <code>IFigureSchemeProvider</code>.
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if(key.equals(IFigureSchemeProvider.class))
		return MeemPlexFigureSchemeProvider.getInstance();
		else
		return super.getAdapter(key);
	}
	
	/**
	 * Gets the figure as <code>MeemPlexDetailedFigure</code>.
	 * @return MeemPlexDetailedFigure The figure as 
	 * <code>MeemPlexDetailedFigure</code>.
	 */
	public MeemPlexDetailedFigure getMeemplexFigure() {
		return (MeemPlexDetailedFigure)getFigure();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		//installEditPolicy(EditPolicy.LAYOUT_ROLE, null);
		//installEditPolicy(FeatureEditPolicy.GRAPHICAL_NODE_ROLE, new MeemNodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new CollapsibleLayoutEditPolicy());
		//installEditPolicy(FeatureEditPolicy.CONNECTABLE_ROLE, new ConnectableComponentEditPolicy());
		installEditPolicy(FeatureEditPolicy.HIGHLIGHT_ROLE, new ReverseHighlightEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SimpleSelectionHandlesEditPolicy());
		installEditPolicy(FeatureEditPolicy.BRING_TO_FRONT_ROLE, new BringToFrontEditPolicy());
		installEditPolicy(FeatureEditPolicy.VIEW_MODE_ROLE, new ViewModeEditPolicy(getMeemPlexModel()));
	}

	/**
	 * Overridden to create <code>MeemplexFigure</code>.
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemCollapsibleEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		MeemPlexDetailedFigure figure = new MeemPlexDetailedFigure();
		figure.apply(getScheme());
		return figure;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getContentPane()
	 */
	public IFigure getContentPane() {
		return getMeemplexFigure().getMeemPane();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshChildren()
	 */
	protected void refreshChildren() {
		super.refreshChildren();
		EditPart editPart = (EditPart)
		getViewer().getEditPartRegistry().get(getMeemPlexModel().getWorksheet());
		if(editPart == null) {
			editPart = createChild(getMeemPlexModel().getWorksheet());
			addChild(editPart, getChildren().size());
		}
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.BoundsObjectEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		
		MeemPlexDetailedFigure figure = getMeemplexFigure();
		((ToolbarLayout)figure.getMeemPane().getLayoutManager()).setSpacing(2);
		MeemPlex meemplex = getMeemPlexModel();
		
		figure.getCaption().setText(meemplex.getName());
		//figure.getCaption().setToolTip(new ToolTip(meemplex.getProxy().getMeemPath().toString()));
	}

	/**
	 * Overridden to create worksheet figure in the worksheet pane.
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#addChildVisual(org.eclipse.gef.EditPart, int)
	 */
	protected void addChildVisual(EditPart childEditPart, int index) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		if(childEditPart instanceof WorksheetEditPart) {
			getMeemplexFigure().getWorksheetPane().add(child);
		}
		else
		super.addChildVisual(childEditPart, index);
	}
	
	/**
	 * Overridden to remove worksheet figure from the worksheet pane.
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#removeChildVisual(org.eclipse.gef.EditPart)
	 */
	protected void removeChildVisual(EditPart childEditPart) {
		IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
		if(childEditPart instanceof WorksheetEditPart) {
			getMeemplexFigure().getWorksheetPane().remove(child);
		}
		else
		super.removeChildVisual(childEditPart);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		/*
		if(Meem.ID_MEEM_STATE.equals(prop)) {
			refreshScheme();
			refreshWorksheet();
		}
		else
		*/
		if(Meem.ID_VIEW_MODE.equals(prop)) {
			ElementContainer container = (ElementContainer)getParent().getModel();
			container.refreshChild(getModel());
		}
		else
		super.propertyChange(evt);
	}

	protected void refreshScheme() {
		getMeemplexFigure().apply(getScheme());
		Iterator it = super.getChildren().iterator();
		
		while(it.hasNext()) {
			Object child = it.next();
			if(child instanceof WedgeEditPart) {
				WedgeEditPart editPart = (WedgeEditPart)child;
				editPart.refreshScheme();
			}
		}
	}
	protected void refreshWorksheet() {
		WorksheetEditPart editPart = (WorksheetEditPart)
		getViewer().getEditPartRegistry().get(getMeemPlexModel().getWorksheet());
		
		MeemClientProxy proxy = getMeemPlexModel().getProxy();
		LifeCycleState state = proxy.getLifeCycle().getState();
		
		if(LifeCycleState.READY.equals(state)) {
//			BackdropFreeformLayer figure = (BackdropFreeformLayer)editPart.getFigure();
			//figure.setImage(Icon.class, "worksheet.bmp");
			//figure.setBorder(null);
		}
		else {
			Color color = getScheme().getBackground();
			editPart.getFigure().setBackgroundColor(color);
			//editPart.getFigure().setBorder(new LineBorder(ColorTransformer.darken(color)));
		}
	}
	
}
