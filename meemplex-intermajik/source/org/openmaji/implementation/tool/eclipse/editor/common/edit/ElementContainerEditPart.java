package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.common.model.ElementContainer;
import org.openmaji.implementation.tool.eclipse.editor.features.util.EditPartHelper;


/**
 * @author Kin Wong
 * <P> ElementContainerEditPart represents the controller part of the element
 * container which an element that contains other elements as children. It works
 * in conjuction with ElementContainer model.
 * </P>
 */
abstract public class ElementContainerEditPart extends BoundsObjectEditPart {
	/**
	 * Installs the desired EditPolicies for this.
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new ElementContainerEditPolicy());
	}
	/**
	 * Returns the model of this as an ElementContainer.
	 * @return  ElementContainer corresponds to this edit part.
	 */
	protected ElementContainer getContainerModel() {
		return (ElementContainer)getModel();
	}
	/**
	 * Overridden to handle changes in child list.
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(	ElementContainer.ID_CHILDREN.equals(prop) || 
				ElementContainer.ID_CHILD_ORDERS.equals(prop)) {
			refreshChildren(); 
			EditPartHelper.forceRepaint(this);
		} 
		else
		if(ElementContainer.ID_REFRESH_CHILD.equals(prop)) {
			Element element = (Element)evt.getNewValue();

			EditPart childEditPart = (EditPart)getViewer().getEditPartRegistry().get(element);
			int selected = childEditPart.getSelected();
			
			removeChild(childEditPart);	// Remove the previous edit part
			refreshChildren();
			
			if(selected != SELECTED_NONE) {
				getViewer().appendSelection(
				(EditPart)getViewer().getEditPartRegistry().get(element));
			}
		}
		else
		super.propertyChange(evt);
	}
	/**
	 * Returns the children of this through the model.
	 * @return  Children of this as a List.
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren() {
		return getContainerModel().getChildren();
	}
}
