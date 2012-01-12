package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import java.util.Iterator;


import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.views.properties.IPropertySource;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.CollapsibleFigure;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.FigureScheme;
import org.openmaji.implementation.tool.eclipse.editor.common.figures.IFigureSchemeProvider;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortRequest;
import org.openmaji.implementation.tool.eclipse.editor.features.util.ClassHelper;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.infotips.WedgeInfoTip;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.WedgeLoadedPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.properties.WedgeUsefulPropertySource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.WedgeVariableSource;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleTransition;


/**
 * <code>WedgeEditPart</code> represents the controller of the wedge.
 * @author Kin Wong
 */
public class WedgeEditPart extends CollapsibleEditPart {
	static public String ID_SORT_FACET_ASCENDING = "WedgeEditPart.sort.facet.ascending";
	static public String ID_SORT_FACET_DESCENDING = "WedgeEditPart.sort.facet.descending";
	
	class WedgeSortPolicy extends SortEditPolicy {
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortEditPolicy#isValidSortRequest(org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortRequest)
		 */
		protected boolean isValidSortRequest(SortRequest request) {
			if(	ID_SORT_FACET_ASCENDING.equals(request.getType()) ||
				ID_SORT_FACET_DESCENDING.equals(request.getType()))
			return true;
			else
			return false;
		}
	}
	
	LifeCycleClient lifeCycleClient = new LifeCycleClient() {
		public void lifeCycleStateChanging(LifeCycleTransition transition) {
			//don't care
		}
		public void lifeCycleStateChanged(LifeCycleTransition transistion) {
			refreshPropertySheet();
		}
	};

	/**
	 * Constructs an instance of WedgeEditPart.
	 * @param wedge The new instance of WedgeEditPart.
	 */
	public WedgeEditPart(Wedge wedge) {
		setModel(wedge);
	}
	
	protected IPropertySource createPropertySource() {
		if(getWedgeModel().getMeem().isLCSLoaded())
		return new WedgeLoadedPropertySource(getWedgeModel());
		else
		return new WedgeUsefulPropertySource(getWedgeModel());
	}
	
	protected MeemClientProxy getMeemProxy() {
		if(getWedgeModel().getMeem() == null) return null;
		return getWedgeModel().getMeem().getProxy();
	}
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class key) {
		if(key.equals(IPropertySource.class)) {
			return createPropertySource();
		}
		else
		return super.getAdapter(key);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#activate()
	 */
	public void activate() {
		super.activate();
		getWedgeModel().getMeem().getProxy().getLifeCycle().addClient(lifeCycleClient);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#deactivate()
	 */
	public void deactivate() {
		if(getWedgeModel().getMeem() != null) {
			getWedgeModel().getMeem().getProxy().getLifeCycle().removeClient(lifeCycleClient);
		}
		super.deactivate();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		if(	(getWedgeModel().getAttribute() != null) && 
				(getWedgeModel().getAttribute().isSystemWedge())) {
			removeEditPolicy(EditPolicy.COMPONENT_ROLE);
		}
		else {
			installEditPolicy(EditPolicy.COMPONENT_ROLE, new WedgeMetaMeemEditPolicy());
		}
		installEditPolicy(KINeticEditPolicy.LIFE_CYCLE_STATE_ROLE, 
			new LifeCycleStateEditPolicy(getMeemProxy()));
			
		installEditPolicy(FeatureEditPolicy.SORT_ROLE, new WedgeSortPolicy());
		installEditPolicy(MajiEditPolicy.VARIABLE_SOURCE_ROLE, 
		new PropertyChangeToVariableMapElementEditPolicy(new WedgeVariableSource(getWedgeModel())));
	}

	protected Wedge getWedgeModel() {
		return (Wedge) getModel();
	}
	
	protected FigureScheme getScheme() {
		IFigureSchemeProvider schemeProvider = (IFigureSchemeProvider)
			EditPartHelper.findAncestor(this, IFigureSchemeProvider.class);
		return schemeProvider.getScheme(this);
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		CollapsibleFigure figure = new CollapsibleFigure();
		figure.apply(getScheme());
		return figure;
	}
	
	public void refreshScheme() {
		getCollapsibleFigure().apply(getScheme());
		Iterator it = getChildren().iterator();
		while(it.hasNext()) {
			FacetEditPart editPart = (FacetEditPart)it.next();
			editPart.refreshScheme();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.common.edit.CollapsibleEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		getFigure().setToolTip(new WedgeInfoTip(getWedgeModel()));
	}

	protected void refreshCollapsibleFigure() {
		Wedge wedge = getWedgeModel();
		CollapsibleFigure figure = getCollapsibleFigure();
		((ToolbarLayout)figure.getContentPane().getLayoutManager()).setSpacing(0);
		figure.setCaptionText(ClassHelper.getClassNameFromFullName(wedge.getName()));

		ignoredChange = true;
		figure.setCollapse(getCollapsibleModel().isCollapsed());
		figure.getCollapseButton().setSelected(wedge.isCollapsed());
		ignoredChange = false;
	}
}
