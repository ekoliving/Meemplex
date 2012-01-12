/*
 * @(#)DiagramContainerEditPolicy.java
 * Created on 1/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateRequest;
import org.openmaji.implementation.tool.eclipse.client.CategoryEntryNameFactory;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.editor.common.edit.ElementContainerEditPolicy;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Diagram;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.system.space.CategoryEntry;


/**
 * <code>DiagramContainerEditPolicy</code> maintains the cateogry associates 
 * with the diagram by updating the content of the category based on user 
 * requests.
 * <p>
 * @author Kin Wong
 */
public class DiagramContainerEditPolicy extends ElementContainerEditPolicy {
	/**
	 * Gets the diagram model from the host edit part.
	 * @return Diagram The diagram model associates with the host edit part.
	 */
	protected Diagram getDiagramModel() {
		return (Diagram)getHost().getModel();
	}
	
	/**
	 * Overridden to create a command that creates the category entry of the newly
	 * created Meem in the category associates with diagram.
	 * <p>
	 * @see org.eclipse.gef.editpolicies.ContainerEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		Diagram diagram = getDiagramModel();
		Meem meem = (Meem)request.getNewObject();
		CategoryProxy category = diagram.getCategory();
		String name = 
			CategoryEntryNameFactory.createUniqueEntryName(category, meem.getProxy()); 
		meem.setName(name);
		meem.getProxy().getConfigurationHandler().
			valueChanged(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER, name);
		
		CategoryEntry entry = new CategoryEntry(name, meem.getProxy());
		return new CategoryEntryCreateCommand(category, name, 
			entry.getMeem().getMeemPath());
	}
}
