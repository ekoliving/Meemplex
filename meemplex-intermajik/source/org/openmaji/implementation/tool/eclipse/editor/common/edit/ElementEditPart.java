package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.Figure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;
import org.openmaji.implementation.tool.eclipse.properties.PropertySheetPage;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class ElementEditPart
	extends AbstractGraphicalEditPart
	implements PropertyChangeListener {
	Figure selection;
	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		if (isActive()) return;
		super.activate();
		getElementModel().addPropertyChangeListener(this);
	}
	/**
	 * @see org.eclipse.gef.EditPart#deactivate()
	 */
	public void deactivate() {
		if (!isActive()) return;
		getElementModel().removePropertyChangeListener(this);
		super.deactivate();
	}
	/**
	 * Gets the model as an element.
	 * @return Element The model as an element.
	 */
	protected Element getElementModel() {
		return (Element)getModel();
	}
	/**
	 * Creates and installs edit policies for this element. The element
	 * base class only install
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new ElementEditPolicy());
	}
	/*
	 *  (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(Element.ID_REFRESH_VISUALS.equals(prop)) {
			refresh();
		}
		else
		if(Element.ID_NAME.equals(prop)) {
			// Name has changed
			refreshVisuals();
			refreshPropertySheet();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#refresh()
	 */
	public void refresh() {
		super.refresh();
		refreshPropertySheet();
	}

	/**
	 * Refreshes the propert sheet associates with this element edit part.
	 */
	protected void refreshPropertySheet() {
		if(getSelected() == SELECTED_NONE) return;
		
		IPropertySheetPage page = (IPropertySheetPage)
			EditPartHelper.getEditorPart(this).getAdapter(IPropertySheetPage.class);
		if(page == null) return;
		if(!(page instanceof PropertySheetPage)) return;
		((PropertySheetPage)page).refresh();
	}
}
