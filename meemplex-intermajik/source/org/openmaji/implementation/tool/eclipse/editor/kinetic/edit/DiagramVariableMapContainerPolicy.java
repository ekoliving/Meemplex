/*
 * @(#)DiagramVariableMapContainerPolicy.java
 * Created on 6/10/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.openmaji.implementation.intermajik.model.ElementPath;
import org.openmaji.implementation.tool.eclipse.client.variables.IVariableSource;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.VariableMapCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.variables.VariableSourceFactory;


/**
 * <code>DiagramVariableMapContainerPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class DiagramVariableMapContainerPolicy extends ContainerEditPolicy {
	/**
	 * Gets the diagram model from the host edit part.
	 * @return Diagram The diagram model associates with the host edit part.
	 */
	protected Diagram getDiagramModel() {
		return (Diagram)getHost().getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		Diagram diagram = getDiagramModel();
		Meem meem = (Meem)request.getNewObject();
		IVariableSource vs = VariableSourceFactory.getInstance().createVariableSource(meem);
		ElementPath path = getDiagramModel().getPath().append(meem.getPath());
		
		//System.out.println("Creating Meem at: " + path);
		return new VariableMapCreateCommand(diagram.getProxy().getVariableMapProxy(), path, vs.extractAll());
	}
}
