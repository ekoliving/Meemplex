/*
 * @(#)DiagramEditPart.java
 * Created on 29/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementContainerEditPart;
import org.openmaji.implementation.tool.eclipse.editor.features.FeatureEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.SimpleSelectionHandlesEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;


/**
 * <code>DiagramEditPart</code> provides a base implemntation of controller for
 * <code>Diagram</code> derived classes.
 * <p>
 * @author Kin Wong
 */
abstract public class DiagramEditPart extends ElementContainerEditPart {
	static protected int MARGIN = 10;
	
	/**
	 * Constructs an instance of <code>DiagramEditPart</code>.
	 * <p>
	 * @param diagram The diagram to be associated with this editpart.
	 */
	public DiagramEditPart(Diagram diagram) {
		setModel(diagram);
	}
	
	/**
	 * Gets the diagram model associates with this Diagram edit part.
	 * <p>
	 * @return Diagram The diagram model associates with this Diagram edit part.
	 */
	public Diagram getDiagramModel() {
		return (Diagram)getModel();
	}
	
	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		boolean modifiable = getDiagramModel().getProxy().getVariableMapProxy().isModifiable();
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SimpleSelectionHandlesEditPolicy());
		if(modifiable) {
			installEditPolicy(FeatureEditPolicy.DROP_ROLE, new DiagramMeemDropCloneEditPolicy());
			installEditPolicy(EditPolicy.LAYOUT_ROLE, new DiagramXYLayoutEditPolicy());
			installEditPolicy(MajiEditPolicy.CATEGORY_ENTRY_CONTAINER_ROLE, new DiagramContainerEditPolicy());
			installEditPolicy(MajiEditPolicy.VARIABLE_SOURCE_CONTAINER_ROLE, new DiagramVariableMapContainerPolicy());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	public DragTracker getDragTracker(Request request) {
		return new MarqueeDragTracker();
	}
}
