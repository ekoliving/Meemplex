/*
 * @(#)SortEditPolicy.java
 * Created on 16/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.sorting;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

/**
 * <code>SortEditPolicy</code> is an abstract EditPolicy that knows how to 
 * generate 
 * {@link org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortCommand}.
 * <p>
 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.ISortableItemContainer
 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortAction
 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortRequest
 * @see org.openmaji.implementation.tool.eclipse.editor.features.sorting.SortCommand
 * @author Kin Wong
 */
public abstract class SortEditPolicy extends AbstractEditPolicy {
	/**
	 * Gets the <code>ISortableItemContainer</code> from the host editpart.
	 * <p>
	 * It search for the <code>ISortableItemContainer</code> in the following
	 * order:
	 * <p>
	 * (1) The model; <p>
	 * (2) The host editpart; <p>
	 * (3) As an Adapter of the editpart. <p>
	 * @return ISortableItemContainer A <code>ISortableItemContainer</code> to
	 * be used in the <code>SortCommand</code>, or null if none can be found.
	 * <p>
	 */
	protected ISortableItemContainer getContainer() {
		EditPart editPart = getHost();
		if(editPart.getModel() instanceof ISortableItemContainer) 
			return (ISortableItemContainer)editPart.getModel();

		if(editPart instanceof ISortableItemContainer) 
			return (ISortableItemContainer)editPart;
		
		return (ISortableItemContainer)
			editPart.getAdapter(ISortableItemContainer.class);
	}
	/**
	 * Overridden to intrepret <code>SortRequest</code>.
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		ISortableItemContainer container = getContainer();
		if(container == null) return null;

		if(request instanceof SortRequest) {
			SortRequest  sortRequest = (SortRequest)request;
			if(isValidSortRequest(sortRequest)) {
				if(container != null)
				return getSortCommand(sortRequest, container);
			}
		}
		return null;
	}
	
	abstract protected boolean isValidSortRequest(SortRequest request);
	
	protected Command getSortCommand(
		SortRequest request, 
		ISortableItemContainer container) {
		SortCommand command = new SortCommand(container, request.getComparator());
		command.setLabel("Sorting");// + request.getFieldName());
		return command;
	}
}
