package org.openmaji.implementation.tool.eclipse.editor.common.edit;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;
import org.openmaji.implementation.tool.eclipse.editor.common.model.BoundsObject;


/**
 * @author Kin Wong
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
abstract public class BoundsObjectEditPart extends ElementEditPart {
	/**
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected BoundsObject getBoundsObjectModel() {
		return (BoundsObject)getModel();
	}
	
	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if(BoundsObject.ID_SIZE.equals(prop) || BoundsObject.ID_LOCATION.equals(prop)) {
			refreshVisuals();
		}
		else
		if(BoundsObject.ID_VALIDATEFROMFIGURE.equals(prop)) {
			getFigure().validate();
			getBoundsObjectModel().setBounds(getFigure().getBounds());
		}
		else
		super.propertyChange(evt);
	}
	protected void refreshVisuals() {
		super.refreshVisuals();
		GraphicalEditPart graphicalEditPart = (GraphicalEditPart)getParent();
		if(graphicalEditPart == null) return;
		if(graphicalEditPart.getFigure().getLayoutManager() instanceof XYLayout) {
			Point location = getBoundsObjectModel().getLocation();
			Dimension size = getBoundsObjectModel().getSize();
			Rectangle bounds = new Rectangle(location ,size);
			graphicalEditPart.setLayoutConstraint(this, getFigure(), bounds);
		}
	}
}
