/*
 * @(#)EntryOfCategoryConnectionEditPolicy.java
 * Created on 23/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.edit;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.commands.CategoryEntryDeleteCommand;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;


/**
 * <code>EntryOfCategoryConnectionEditPolicy</code>.
 * <p>
 * @author Kin Wong
 */
public class EntryOfCategoryConnectionEditPolicy extends ConnectionEditPolicy {
	
	private EntryOf getEntryOfModel() {
		return (EntryOf)getHost().getModel();
	}
	
	private Category getCategory() {
		EntryOf entryOf = getEntryOfModel();
		if(entryOf == null) return null;
		return entryOf.getCategory();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConnectionEditPolicy#getDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	protected Command getDeleteCommand(GroupRequest request) {
		EntryOf entryOf = getEntryOfModel();
		Category category = getCategory();
		if((entryOf == null) || (category == null)) return null;
		return new CategoryEntryDeleteCommand(category.getProxy().getCategoryProxy(), 
																					entryOf.getCategoryEntry());
	}
}
