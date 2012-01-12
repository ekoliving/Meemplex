package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToggleModel;
import org.eclipse.gef.EditPart;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.client.VariableMapProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.dnd.WedgeAddDropEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.infotips.InfoTip;
import org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemCollapsibleFigure;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.MeemFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips.MeemInfoTip;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.MeemPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.MeemVariableSource;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleLimit;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * <code>MeemEditPart</code> represents the controller of a meem in the MVC.
 * @author Kin Wong
 */
public class MeemEditPart extends CollapsibleEditPart {
	static public String ID_SORT_WEDGE_ASCENDING = "MeemEditPart.sort.wedge.ascending";
	static public String ID_SORT_WEDGE_DESCENDING = "MeemEditPart.sort.wedge.descending";
	
	private boolean ignoredStateButtonChange = false;
	private ChangeListener stateChangeListener = new ChangeListener() {
		/**
		 * Implemented here to handle collapse button click.
		 * @see org.eclipse.draw2d.ChangeListener#handleStateChanged(ChangeEvent)
		 */
		public void handleStateChanged(ChangeEvent event) {
			if(ignoredStateButtonChange) return; 
			String prop = event.getPropertyName();
			if(ToggleModel.SELECTED_PROPERTY.equals(prop)) stateButtonClicked();
		}
	};
	
	private LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			//don't care
		}
		public void lifeCycleStateChanged(LifeCycleTransition transistion) {
			refreshVisuals();
			refreshPropertySheet();
			refreshEditPolicy();
			refreshScheme();
		}
	};
	
	private LifeCycleLimit lifeCycleLimitClient = new LifeCycleLimit() {
		public void limitLifeCycleState(LifeCycleState state) {
			refreshVisuals();
			refreshPropertySheet();
			refreshEditPolicy();
			refreshScheme();
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
/*	
	private MetaMeem metaMeemClient = new MetaMeemStub() {
		public void addDependencyAttribute(Object facetKey, DependencyAttribute dependencyAttribute) {
		}
		
		public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {}
		public void removeDependencyAttribute(Object dependencyKey) {
			refresh();
		}
	};
*/	
	protected VariableMapProxy variableMapProxy;
	
	class MeemSortEditPolicy extends SortEditPolicy {
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortEditPolicy#isValidSortRequest(org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortRequest)
		 */
		protected boolean isValidSortRequest(SortRequest request) {
			if(	ID_SORT_WEDGE_ASCENDING.equals(request.getType()) ||
				ID_SORT_WEDGE_DESCENDING.equals(request.getType()))
			return true;
			else
			return false;
		}
	}
	
	/**
	 * Constructs an instance of ConnectableMeemEditPart.
	 * @param meem An instance of ConnectableMeemEditPart.
	 */
	public MeemEditPart(Meem meem, VariableMapProxy variableMapProxy) {
		setModel(meem);
		this.variableMapProxy = variableMapProxy;
	}

	/**
	 * Creates an <code>IPropertySource</code> to be used as the property sheet
	 * source for the meem.
	 * <p>
	 * @return IPropertySource The property source for the meem.
	 */
	protected IPropertySource createPropertySource() {
		return new MeemPropertySource(getMeemModel());
	}
	
	/**
	 * Gets the model as a meem.
	 * @return Meem The model as a meem.
	 */
	public Meem getMeemModel() {
		return (Meem)getModel();
	}
	
	protected MeemClientProxy getMeemProxy() {
		return getMeemModel().getProxy();
	}
	
	protected MeemCollapsibleFigure getMeemFigure() {
		return (MeemCollapsibleFigure) getFigure();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#activate()
	 */
	public void activate() {
		super.activate();
		ToggleModel model = (ToggleModel)getMeemFigure().getStateButton().getModel();
		model.addChangeListener(stateChangeListener);

		getMeemModel().getProxy().getLifeCycle().addClient(lifeCycleClient);
		getMeemModel().getProxy().getLifeCycleLimit().addClient(lifeCycleLimitClient);
		getMeemModel().getProxy().getConfigurationHandler().addClient(configurationClient);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#deactivate()
	 */
	public void deactivate() {
		getMeemModel().getProxy().getConfigurationHandler().removeClient(configurationClient);
		getMeemModel().getProxy().getLifeCycleLimit().removeClient(lifeCycleLimitClient);
		getMeemModel().getProxy().getLifeCycle().removeClient(lifeCycleClient);
		

		ToggleModel model = (ToggleModel)getMeemFigure().getStateButton().getModel();
		model.removeChangeListener(stateChangeListener);
		super.deactivate();
	}

	/**
	 * Overridden to Install sort edit policy.
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(KINeticEditPolicy.LIFE_CYCLE_STATE_ROLE, 
			new LifeCycleStateEditPolicy(getMeemProxy()));
			
		installEditPolicy(FeatureEditPolicy.SORT_ROLE, new MeemSortEditPolicy());
		installEditPolicy(MajiEditPolicy.CATEGORY_ENTRY_ROLE, new MeemEditPolicy());
		installEditPolicy(MajiEditPolicy.VARIABLE_SOURCE_ROLE, 
			new PropertyChangeToVariableMapElementEditPolicy(new MeemVariableSource(getMeemModel())));
		refreshEditPolicy();
	}
		
	protected void refreshEditPolicy() {
		if(getMeemModel().isLCSLoaded())
		installEditPolicy(FeatureEditPolicy.DROP_ROLE, new WedgeAddDropEditPolicy());
		else
		removeEditPolicy(FeatureEditPolicy.DROP_ROLE);
	}
	/**
	 * Overridden to return <code>IFigureSchemeProvider</code>.
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if(key.equals(MeemClientProxy.class)) {
			return getMeemModel().getProxy();
		}
		else
		if(key.equals(IPropertySource.class)) 
			return createPropertySource();
		else
		if(key.equals(IFigureSchemeProvider.class)) 
			return getSchemeProvider();
		else
		return super.getAdapter(key);
	}
		
	/**
	 * Gets the figure scheme provider.
	 * @return IFigureSchemeProvider The figure scheme provider.
	 */
	public IFigureSchemeProvider getSchemeProvider() {
		return MeemFigureSchemeProvider.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(Meem.ID_SHOWSYSTEMWEDGES.equals(prop)) {
			refresh();
		}
		else
		super.propertyChange(evt);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		List children = super.getModelChildren();
		
		if(!getMeemModel().isSystemWedgeShown()) {
			ArrayList wedges = new ArrayList();
			Iterator it = children.iterator();
			while(it.hasNext()) {
				Wedge wedge = (Wedge)it.next();
				if(!wedge.isSystemWedge()) wedges.add(wedge);
			}
			return wedges;
		}
		else
		return children;
	}

	/**
	 * Overridden to create configuration specific meem figure.
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		MeemCollapsibleFigure figure = new MeemCollapsibleFigure();
		figure.apply(getScheme(this));
		return figure;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#refreshCollapsibleFigure()
	 */
	protected void refreshCollapsibleFigure() {
		super.refreshCollapsibleFigure();
		MeemCollapsibleFigure figure = (MeemCollapsibleFigure)getCollapsibleFigure();
		LifeCycleState state = getMeemModel().getProxy().getLifeCycle().getState();
		String tooltipText = "State is " + state.getCurrentState().toUpperCase() + ".\n";

		if(state.equals(LifeCycleState.READY)) {
			tooltipText += "Click to make it LOADED.";				
		}
		else 
		if(state.equals(LifeCycleState.LOADED)) {
			tooltipText += "Click to make it READY";
		}
		else
		if(state.equals(LifeCycleState.PENDING)) {
			tooltipText += "Click to make it LOADED";
		}
		
		figure.getStateButton().setState(state);
		figure.getStateButton().setToolTip(new InfoTip(tooltipText));
		figure.setToolTip(new MeemInfoTip(getMeemModel()));
	}
	
	public FigureScheme getScheme(EditPart editPart) {
		return getSchemeProvider().getScheme(editPart);
	}
	protected void refreshScheme() {
		getCollapsibleFigure().apply(getScheme(this));
		Iterator it = getChildren().iterator();
		while(it.hasNext()) {
			WedgeEditPart editPart = (WedgeEditPart)it.next();
			editPart.refreshScheme();
		}
	}
	
	void stateButtonClicked() {
		LifeCycleState state = 	getMeemModel().getProxy().
														getLifeCycle().getState();
		
		if(state.equals(LifeCycleState.LOADED)) {
			getMeemModel().getProxy().getLifeCycle().changeLifeCycleState(LifeCycleState.READY);
		}
		else
		if(state.equals(LifeCycleState.READY)) {
			getMeemModel().getProxy().getLifeCycle().changeLifeCycleState(LifeCycleState.LOADED);
		}		
		else
		if(state.equals(LifeCycleState.PENDING)) {
			getMeemModel().getProxy().getLifeCycle().changeLifeCycleState(LifeCycleState.LOADED);
		}		
	}
}
