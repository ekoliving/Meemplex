package org.openmaji.implementation.tool.eclipse.editor.kinetic;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.openmaji.implementation.intermajik.model.ViewModeConstants;
import org.openmaji.implementation.tool.eclipse.client.VariableMapProxy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.CategoryCollapsibleEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.CategoryIconicEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.ConfigurationEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.DependencyEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.EntryOfEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.FacetEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemCollapsibleEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemIconicEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.MeemPlexDetailedEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.WedgeConnectableEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.edit.WorksheetEditPart;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Configuration;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Facet;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.MeemPlex;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Wedge;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Worksheet;

/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class KINeticEditPartFactory implements EditPartFactory {
	private VariableMapProxy variableMap;
	
	public void setVariableMap(VariableMapProxy variableMap) {
		this.variableMap = variableMap;
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		
		if(model instanceof Configuration) {
			return new ConfigurationEditPart((Configuration)model);
		}
		else
		if(model instanceof Worksheet) {
			return new WorksheetEditPart((Worksheet)model);
		}
		else
		if(model instanceof MeemPlex) {
			return createMeeplexEditPart(context, (MeemPlex)model);			
		}
		else
		if(model instanceof Category) {
			return createCategoryEditPart(context, (Category)model);
		}
		else
		if(model instanceof Meem) {
			return createMeemEditPart(context, (Meem)model);
		}
		else
		if(model instanceof Wedge) {
			 return new WedgeConnectableEditPart((Wedge)model);
		}
		else
		if(model instanceof Facet) {
			return createFacetEditPart(context, (Facet)model);
		}
		else
		if(model instanceof Dependency) {
			return new DependencyEditPart((Dependency)model);
		}
		else
		if(model instanceof EntryOf) {
			return new EntryOfEditPart((EntryOf)model);
		}
		
		//System.out.println("Undefined model: " + model.toString());
		return null;
	}
	
	/**
	 * Creates editpart for meem model.
	 * @param context The parent of the new editpart.
	 * @param meem The meem model that associates with the new editpart.
	 * @return EditPart The editpart for the meem model.
	 */
	protected EditPart createMeemEditPart(
		EditPart context, 
		Meem meem) {

		Object viewMode = meem.getViewMode();
		if(ViewModeConstants.VIEW_MODE_DETAILED.equals(viewMode)) {
			return new MeemCollapsibleEditPart(meem, variableMap);
		}
		else
		if(ViewModeConstants.VIEW_MODE_ICONIC.equals(viewMode)) {
			return new MeemIconicEditPart(meem, variableMap);
		}
		else {
			//System.out.println("Undefined view mode: " + viewMode);
			return new MeemCollapsibleEditPart(meem, variableMap);
		}
	}
	
	/**
	 * Creates editpart for facet model.
	 * @param context The parent of this new editpart.
	 * @param facet The facet model that associates with the new editpart.
	 * @return EditPart The editpart for this facet model.
	 */
	protected EditPart createFacetEditPart(
		EditPart context,
		Facet facet) {
		return new FacetEditPart(facet);
	}
	
	/**
	 * Creates editpart for category model.
	 * @param context The parent of this new editpart.
	 * @param category The category model that associates with the new editpart.
	 * @return EditPart The editpart for the category model.
	 */
	protected EditPart createCategoryEditPart(
		EditPart context, 
		Category category) {

		Object viewMode = category.getViewMode();
		if(ViewModeConstants.VIEW_MODE_DETAILED.equals(viewMode)) {
			return new CategoryCollapsibleEditPart(category, variableMap);
		}
		else
		if(ViewModeConstants.VIEW_MODE_ICONIC.equals(viewMode)) {
			return new CategoryIconicEditPart(category, variableMap);
		}
		else {
			//System.out.println("Undefined Category view mode: " + viewMode);
			return new CategoryCollapsibleEditPart(category, variableMap);
		}
	}
	/**
	 * Creates editpart for meemplex model.
	 * @param context The parent of this new editpart.
	 * @param meemplex
	 * @return EditPart
	 */
	protected EditPart createMeeplexEditPart(
		EditPart context, 
		MeemPlex meemplex) {
		Object viewMode = meemplex.getViewMode();
		
		if(ViewModeConstants.VIEW_MODE_DETAILED.equals(viewMode)) {
			return new MeemPlexDetailedEditPart(meemplex);
		}
		else
		if(ViewModeConstants.VIEW_MODE_ICONIC.equals(viewMode)) {
			return new MeemIconicEditPart(meemplex, variableMap);
		}
		else {
			//System.out.println("Undefined Meemplex view mode: " + viewMode);
			return new MeemPlexDetailedEditPart(meemplex);
		}
	}
}
