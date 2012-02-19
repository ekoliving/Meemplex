/*
 * @(#)SortAction.java
 * Created on 16/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.sorting;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.IWorkbenchPart;

/**
 * <code>SortAction</code>.
 * <p>
 * @author Kin Wong
 */
public class SortAction extends SelectionAction {
	private SortRequest request;
	
	/**
	 * Constructs an instance of <code>SortAction</code>.
	 * <p>
	 * @param part The part that this action is associated with.
	 * @param comparator The comparator that defines the sort order.
	 */
	public SortAction(IWorkbenchPart part, Comparator comparator) {
		super(part);
		request = new SortRequest(comparator);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#setId(java.lang.String)
	 */
	public void setId(String id) {
		super.setId(id);
		request.setType(id);
	}

	/**
	 * Returns the <code>Comparator</code> that defines the sort order.
	 * @return Comparator The <code>Comparator</code> that defines the sort 
	 * order.
	 */
	public Comparator getComparator() {
		return request.getComparator();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.actions.EditorPartAction#calculateEnabled()
	 */
	protected boolean calculateEnabled() {
		List parts = getSelectedObjects();
		if(parts.isEmpty()) return false;
		
		Iterator it = parts.iterator();
		while(it.hasNext()) {
			Object selected = it.next();
			if(selected instanceof EditPart) {
				EditPart part = (EditPart)selected;
				Command command = part.getCommand(request);
				if(command == null) return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates a command that sorts all sortable item containers in the list.
	 * @param list A list contains sortable item containers.
	 * @return Command A command that sorts all items in all sortable item 
	 * containers in the list.
	 */
	public Command createSortCommand(List list) {
		CompoundCommand compoundCommand = new CompoundCommand();
		Iterator it = list.iterator();
		while(it.hasNext()) {
			EditPart part = (EditPart)it.next();
			compoundCommand.add(part.getCommand(request));
		}
		if(compoundCommand.isEmpty()) return null;
		return compoundCommand.unwrap();
	}
	
	/**
	 * Gets a command that sorts all selected edit parts.
	 * @return Command A command that sorts all selected edit parts.
	 */
	protected Command getCommand() {
		List parts = getSelectedObjects();
		if(parts.isEmpty()) return null;
		return createSortCommand(parts);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		execute(getCommand());
	}
}
