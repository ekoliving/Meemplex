/*
 * @(#)ConfigurationXYLayoutEditPolicy
 * Created on 14/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementXYLayoutEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.features.dynamicshapes.NonResizableShapeEditPolicy;


/**
 * @author Kin Wong
 */
public class DiagramXYLayoutEditPolicy extends ElementXYLayoutEditPolicy {
	/**
	 * Overridden to create child layout policies that support dynamic shape 
	 * rendering.
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if(child instanceof MeemPlexDetailedEditPart)
			return new ResizableEditPolicy();
		else
			return new NonResizableShapeEditPolicy();
	}
}
