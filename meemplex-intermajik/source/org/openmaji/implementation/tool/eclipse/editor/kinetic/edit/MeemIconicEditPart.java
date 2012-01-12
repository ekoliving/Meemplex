/*
 * @(#)MeemIconicEditPart.java
 * Created on 10/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openmaji.implementation.intermajik.model.ViewMode;
import org.openmaji.implementation.tool.eclipse.client.LifeCycleClientStub;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.VariableMapProxy;
import org.openmaji.implementation.tool.eclipse.client.presentation.IconExtractor;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.BoundsObjectEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ReverseHighlightEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.common.shapes.Hexagon;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.animation.AnimationEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.connectable.ConnectionCollector;
import org.openmaji.implementation.tool.eclipse.editor.features.containment.ContainerConnectionCollector;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.IShapeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.SimpleSelectionHandlesEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.editpolicies.BringToFrontEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.FigureLabel;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.FigureLocationSynchronizer;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.LayerConstants;
import org.openmaji.implementation.tool.eclipse.editor.features.multiview.ViewModeEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemFigure;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips.MeemInfoTip;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.MeemPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.MeemVariableSource;
import org.openmaji.implementation.tool.eclipse.images.Images;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * @author Kin Wong
 */
public class MeemIconicEditPart extends BoundsObjectEditPart 
	implements NodeEditPart {
	
	static private Image DEFAULT_IMAGE = Images.getIcon("meem48.gif");
	static private Image TEMPLATE_IMAGE = Images.getIcon("meem_template48.gif");
	static public Dimension ICON_SIZE = new Dimension(48, 48);
	static protected Dimension LIFE_CYCLE_STATE_SIZE = new Dimension(55,55);
	static protected final int LIFE_CYCLE_STATE_WIDTH = 2;

	//private VariableMapProxy variableMapProxy;
	private FigureLabel label;
	private Figure lifeCycleState;

	
	protected ConnectionAnchor anchor;
	
	private LifeCycleClient lifeCycleClient = new LifeCycleClientStub() {
		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			refreshVisuals();
			refreshPropertySheet();
		}
	};
	
	private LifeCycleLimit lifeCycleLimitClient = new LifeCycleLimit() {
		public void limitLifeCycleState(LifeCycleState state) {
			refreshVisuals();
			refreshPropertySheet();
		}
	};
	
	private ConfigurationClient configurationClient = new ConfigurationClient() {
		public void specificationChanged(ConfigurationSpecification[] oldSpecifications, ConfigurationSpecification[] newSpecifications) {
			refreshPropertySheet();
		}
		public void valueAccepted(ConfigurationIdentifier id, Serializable value) {
			refreshPropertySheet();
		}
		public void valueRejected(ConfigurationIdentifier id, Serializable value, Serializable reason) {
			refreshPropertySheet();
		}
	};
	/**
	 * Constructs an instance of <code>MeemIconicEditPart</code>.
	 * <p>
	 */
	public MeemIconicEditPart(Meem meem, VariableMapProxy variableMapProxy) {
		setModel(meem);
		//this.variableMapProxy = variableMapProxy;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		super.activate();
		getMeemModel().getProxy().getLifeCycle().addClient(lifeCycleClient);
		getMeemModel().getProxy().getLifeCycleLimit().addClient(lifeCycleLimitClient);
		getMeemModel().getProxy().getConfigurationHandler().addClient(configurationClient);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		removeDecorators();
		getMeemModel().getProxy().getConfigurationHandler().removeClient(configurationClient);
		getMeemModel().getProxy().getLifeCycleLimit().removeClient(lifeCycleLimitClient);
		getMeemModel().getProxy().getLifeCycle().removeClient(lifeCycleClient);
		super.deactivate();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		
		MeemFigure figure = new MeemFigure();
		Image customImage = 
			IconExtractor.extractCustomLarge(getMeemModel().getProxy());
		if(customImage == null) {
			figure.setMeemIcon(getDefaultImage());
		}
		else {
			figure.setMeemIcon(customImage);
			figure.setTemplate(getTemplateImage());
		}
		createDecorators(figure);
		anchor = new EllipseAnchor(figure);
		return figure;
	}
	
	protected void createDecorators(Figure figure) {
		IFigure layer = getLayer(LayerConstants.LABEL_LAYER);

		lifeCycleState = createLifeCycleStateFigure();
		layer.add(lifeCycleState);
		
		new FigureLocationSynchronizer(figure, lifeCycleState);

		label = new FigureLabel(figure);
		label.setForegroundColor(ColorConstants.white);
		layer.add(label);
	}
	
	protected Figure createLifeCycleStateFigure() {
		Hexagon meem = new Hexagon() {
			public boolean containsPoint(int x, int y) {return false;}
		};
		meem.setSize(LIFE_CYCLE_STATE_SIZE);
		meem.setFill(false);
		meem.setLineWidth(LIFE_CYCLE_STATE_WIDTH);
		return meem;
	}
	
	protected void removeDecorators()	{
		IFigure layer = getLayer(LayerConstants.LABEL_LAYER);
		layer.remove(label);
		layer.remove(lifeCycleState);
	}

	protected Image getDefaultImage() {
		return DEFAULT_IMAGE;
	}
	
	protected Image getTemplateImage() {
		return TEMPLATE_IMAGE;
	}

	protected MeemFigure getMeemStateFigure() {
		return (MeemFigure)getFigure();
	}

	public FigureLabel getLabel() {
		return label;
	}

	/**
	 * Gets the figure scheme provider.
	 * @return IFigureSchemeProvider The scheme provider.
	 */
	protected IFigureSchemeProvider getSchemeProvider() {
		return MeemFigureSchemeProvider.getInstance();
	}
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(KINeticEditPolicy.LIFE_CYCLE_STATE_ROLE, 
			new LifeCycleStateEditPolicy(getMeemModel().getProxy()));
		
		AnimationEditPolicy animtionEditPolicy = new AnimationEditPolicy() {
			public void updateAnimation(long millisecond) {
				updateLifeCycleStateAnimation(millisecond);
			}
		};
		
		installEditPolicy(FeatureEditPolicy.ANIMATION_ROLE,animtionEditPolicy);
		installEditPolicy(FeatureEditPolicy.SELECTION_FEEDBACK_ROLE, new SimpleSelectionHandlesEditPolicy());
		installEditPolicy(FeatureEditPolicy.HIGHLIGHT_ROLE, new ReverseHighlightEditPolicy());
		installEditPolicy(FeatureEditPolicy.BRING_TO_FRONT_ROLE, new BringToFrontEditPolicy());
		//installEditPolicy(FeatureEditPolicy.CONNECTABLE_ROLE, new ConnectableComponentEditPolicy());
		installEditPolicy(FeatureEditPolicy.VIEW_MODE_ROLE, new ViewModeEditPolicy(getMeemModel()));
		installEditPolicy(MajiEditPolicy.CATEGORY_ENTRY_ROLE, new MeemEditPolicy());
		installEditPolicy(MajiEditPolicy.VARIABLE_SOURCE_ROLE, 
			new PropertyChangeToVariableMapElementEditPolicy(new MeemVariableSource(getMeemModel())));
		installEditPolicy(FeatureEditPolicy.GRAPHICAL_NODE_ROLE, new MeemNodeEditPolicy());
	}
	
	private void updateLifeCycleStateAnimation(long millisecond) {
		LifeCycleState state = getMeemModel().getLCS();
		if(!state.equals(LifeCycleState.PENDING)) return;
		
		millisecond /= 60;
		if((millisecond % 4) == 0) {
			lifeCycleState.setForegroundColor(ColorConstants.black);
		}
		else {
			lifeCycleState.setForegroundColor(LifeCycleColors.getColor(state));
		}
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if(key.equals(MeemClientProxy.class)) {
			return getMeemModel().getProxy();
		}
		else
		if(key.equals(IPropertySource.class)) 
			return new MeemPropertySource(getMeemModel());
		else
		if((key.equals(IShapeProvider.class)) && (getFigure() instanceof IShapeProvider))
		return (IShapeProvider)getFigure();
		else
		return super.getAdapter(key);
	}
	
	/**
	 * Gets the model as a meem.
	 * @return Meem The model as a meem.
	 */
	public Meem getMeemModel() {
		return (Meem)getModel();
	}
	
	/**
	 * Overridden to handle change of view mode.
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
	  if(Meem.ID_ENTRY_TO_CATEGORY.equals(prop)) {
			// The meem has been added or removed from a Category.
		  refreshSourceConnections();
		}
	  else
	  if(Meem.ID_INNER_SOURCE_CONNECTIONS.equals(prop)) {
			refreshSourceConnections();
	  }
	  else
	  if(Meem.ID_INNER_TARGET_CONNECTIONS.equals(prop)) {
			refreshTargetConnections();
		}
		else
		if(Meem.ID_VIEW_MODE.equals(prop)) {
			Object model = getModel();
			EditPartViewer viewer = getViewer();
			ElementContainer container = (ElementContainer)getParent().getModel();

			int selection = getSelected();
		  container.refreshChild(getModel());
		  if(selection != SELECTED_NONE) {
				EditPart editPart = (EditPart)viewer.getEditPartRegistry().get(model);
				viewer.appendSelection(editPart);
			}
		}
	  else
	  super.propertyChange(evt);
	}
	
    /* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.BoundsObjectEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		MeemFigure figure = getMeemStateFigure();
		figure.setToolTip(new MeemInfoTip(getMeemModel()));
		refreshLabel();
		refreshLCS();
	}
	
	protected void refreshLCS() {
		lifeCycleState.setForegroundColor(LifeCycleColors.getColor(getMeemModel().getLCS()));
	}
	protected void refreshLabel() {
		getLabel().setText(getMeemModel().getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	protected List getModelSourceConnections() {
		ConnectionCollector collector = new ContainerConnectionCollector();
		List connections = new ArrayList(getMeemModel().getEntriesToCategory());
		collector.collectSourceConnections(getModel(), connections);
		return connections;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	protected List getModelTargetConnections() {
		List connections = new ArrayList();
		ConnectionCollector collector = new ContainerConnectionCollector();
		collector.collectTargetConnections(getModel(), connections);
		return connections;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider#getScheme(org.eclipse.draw2d.Figure)
	 */
	public FigureScheme getScheme(IFigure figure) {
		return getSchemeProvider().getScheme(this);
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#getViewMode()
	 */
	public Object getViewMode() {
		return getMeemModel().getViewMode();
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.features.multiview.IViewModeProvider#setViewMode(java.lang.Object)
	 */
	public void setViewMode(ViewMode viewMode) {
		getMeemModel().setViewMode(viewMode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return anchor;
	}
}
