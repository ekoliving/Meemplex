/*
 * @(#)CategoryNodeEditPolicy.java
 * Created on 22/04/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;


import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.openmaji.implementation.tool.eclipse.client.CategoryEntryNameFactory;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.DependencyCreateCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.meem.definition.DependencyType;


/**
 * <code>CategoryNodeEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryNodeEditPolicy extends MeemNodeEditPolicy {
	private Category getCategory() {
		return (Category)getHost().getModel();
	}
	
	/**
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		Command command = request.getStartCommand();
		if(command == null) return null;
		
		if(command instanceof CategoryEntryCreateCommand) {
			return getCategoryEntryCompleteCommand(request);
		}
		else
		if(command instanceof DependencyCreateCommand) {
			return getDependencyCompleteCommand(request);
		}
		
		return null;
	}
	
	private Command getCategoryEntryCompleteCommand(CreateConnectionRequest request) {
		Object source = request.getSourceEditPart().getModel();
		if(!(source instanceof Meem)) return null;
		
		Object target = request.getTargetEditPart().getModel();
		if(!(target instanceof Category)) return null;
		
		if(source == target) return null;
		
		Meem meem = (Meem)source;
		Category category = (Category)target;

		CategoryEntryCreateCommand command = (CategoryEntryCreateCommand)request.getStartCommand();
		command.setCategory(category.getProxy().getCategoryProxy());
		
		String entryName = CategoryEntryNameFactory.
			createUniqueEntryName(category.getProxy().getCategoryProxy(), meem.getName());

		command.setEntryName(entryName);
		request.setTargetEditPart(getHost());
		return command;
	}
	
	private Command getDependencyCompleteCommand(CreateConnectionRequest request) {
		Category category = getCategory();
		if(category == null) return null;
		
		request.setTargetEditPart(getHost());
		DependencyCreateCommand command = (DependencyCreateCommand)request.getStartCommand();
		command.getAttribute().setFacetIdentifier("");
		command.getAttribute().setMeemPath(category.getMeemPath());
		command.getAttribute().setDependencyType(DependencyType.STRONG_MANY);
		return command;
	}
}
