/*
 * @(#)FacetDragEditPolicy.java
 * Created on 6/04/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;

/**
 * <code>FacetDragEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class FacetDragEditPolicy extends GraphicalEditPolicy {
/* (non-Javadoc)
 * @see org.eclipse.gef.editpolicies.AbstractEditPolicy#getCommand(org.eclipse.gef.Request)
 */
public Command getCommand(Request request) {
	return super.getCommand(request);
}

}
