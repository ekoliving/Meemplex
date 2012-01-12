package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import java.beans.PropertyChangeEvent;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionBendpointEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.ConnectionLabel;
import org.openmaji.implementation.tool.eclipse.editor.features.labels.LayerConstants;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.figures.DependencyConnection;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips.DependencyInfoTip;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.DependencyPropertySource;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.DependencyType;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * @author Kin Wong
 */
public class DependencyEditPart extends SchemeConnectionEditPart {
	private ConnectionLabel label;
  private DependencyConnection dependencyConnection;
	private LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
		}

		public void lifeCycleStateChanged(LifeCycleTransition transition) {
			refreshActions();
			refreshPropertySheet();
		}
	};
	
	/**
	 * Constructs an instance of <code>DependencyEditPart</code>.
	 * <p>
	 * @param dependency The dependency model assocciate with this edit part.
	 */
	public DependencyEditPart(Dependency dependency) {
		super(dependency);
	}
	
	protected MeemClientProxy getSourceMeemProxy() {
		return getSourceMeem().getProxy();
	}
	
	private boolean isSourceModifiable() {
		MeemClientProxy proxy = getSourceMeemProxy();
		
		return proxy.getMetaMeem().isModifiable();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if(adapter.equals(IPropertySource.class))
		return new DependencyPropertySource(getDependencyModel(), !isSourceModifiable());
		else
		return super.getAdapter(adapter);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		dependencyConnection = new DependencyConnection();
		FigureScheme scheme = getScheme(this);
		scheme.applyColors(dependencyConnection);
		
		return dependencyConnection;
	}
	
	private void createLabel(PolylineConnection connection) {
		label = new ConnectionLabel(connection, ConnectionLabel.SOURCE);
		label.setForegroundColor(ColorConstants.white);
		getLayer(LayerConstants.LABEL_LAYER).add(label);
		refreshLabel();
	}

	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#activate()
	 */
	public void activate() {
		super.activate();
		getSourceMeemProxy().getLifeCycle().addClient(lifeCycleClient);
    createLabel(dependencyConnection);
	}
	
		
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#deactivate()
	 */
	public void deactivate() {
    getSourceMeemProxy().getLifeCycle().removeClient(lifeCycleClient);
		if(label != null) {
			getLayer(LayerConstants.LABEL_LAYER).remove(label);
			label.setOwner(null);
      label = null;
		}
		super.deactivate();
	}

	protected void refreshLabel() {
    if ( label != null ) {
      label.setText(getDependencyModel().getShortName());
    }
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.ConnectionElementEditPart#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(Dependency.ID_ATTRIBUTE.equals(prop)) {
			refreshVisuals();
			refreshPropertySheet();	
		}
		else
		super.propertyChange(evt);
	}

	/**
	 * Gets the dependency associates with this editpart as model.
	 * @return Dependency The dependency associates with this editpart as model.
	 */
	public Dependency getDependencyModel() {
		return (Dependency)getModel();
	}
	
	Meem getSourceMeem() {
		if(getDependencyModel() == null) return null;
		if(getDependencyModel().getSourceFacet() == null) return null;
		return getDependencyModel().getSourceFacet().getMeem();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		MeemClientProxy proxy = getSourceMeemProxy();
		boolean modifiable = isSourceModifiable();
		installEditPolicy(	EditPolicy.CONNECTION_ROLE, 
												new DependencyConnectionEditPolicy());

		if(modifiable) {
			installEditPolicy(KINeticEditPolicy.LIFE_CYCLE_STATE_ROLE, 
				new LifeCycleStateEditPolicy(proxy));
		}
		else {
			removeEditPolicy(MajiEditPolicy.VARIABLE_SOURCE_ROLE);
		}
	}

	protected void refreshBendpointEditPolicy(){
		MeemClientProxy proxy = getSourceMeemProxy();
		
		boolean modifiable = proxy.getMetaMeem().isModifiable();
		
		if(modifiable)	modifiable = (!(getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter));
		
		if (modifiable)
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new ConnectionBendpointEditPolicy());
		else 
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
	}

	/**
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#refresh()
	 */
	public void refreshVisuals() {
		super.refreshVisuals();
		
//		FigureScheme scheme =
		getScheme(this);
		DependencyConnection dependencyConnection = (DependencyConnection)getFigure();
		Dependency dependency = getDependencyModel();
		DependencyAttribute attribute = dependency.getAttribute();
		DependencyType type = attribute.getDependencyType();
		boolean single = (type.equals(DependencyType.STRONG) || 
										type.equals(DependencyType.WEAK));
										
		boolean strong = (type.equals(DependencyType.STRONG) || 
											type.equals(DependencyType.STRONG_MANY));

		refreshLabel();

		Facet sourceFacet = dependency.getSourceFacet();
		if(sourceFacet == null) return;
		dependencyConnection.update(strong, single, sourceFacet.isOutbound());
		dependencyConnection.setToolTip(new DependencyInfoTip(dependency));
	}
	
	private void refreshActions() {
		if (getParent() == null) return;
		IEditorPart editorPart = EditPartHelper.getEditorPart(this);
		ActionRegistry actionRegistry = 
			(ActionRegistry)editorPart.getAdapter(ActionRegistry.class);
		if(actionRegistry == null) return;
	
			DeleteAction action = (DeleteAction)
			actionRegistry.getAction(ActionFactory.DELETE.getId());
		if(action == null) return;
		action.update();
	}
}
