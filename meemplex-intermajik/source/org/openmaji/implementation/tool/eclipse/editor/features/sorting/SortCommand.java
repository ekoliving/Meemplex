/*
 * @(#)SortCommand.java
 * Created on 16/05/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.sorting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.commands.Command;

/**
 * <code>SortCommand</code> works in conjuction with 
 * <code>ISortableItemContainer</code> and <code>Comparator</code> to sort a set 
 * of items in a container.
 * <p>
 * @author Kin Wong
 */
public class SortCommand extends Command {
	private Comparator comparator;
	private ISortableItemContainer container;
	private ArrayList originalOrder;

	/**
	 * Constructs an instance of <code>SortCommand</code>.
	 * <p>
	 * @param container The container in which the items are to be sorted.
	 * @param comparator The <code>Comparator</code> that defines the sort 
	 * order.
	 */	
	public SortCommand(ISortableItemContainer container, Comparator comparator) {
		this.container = container;
		this.comparator = comparator;
	}
	
	/**
	 * Returns the sortable item container.
	 * @return ISortableItemContainer The container of the items to be sorted.
	 */	
	public ISortableItemContainer getContainer() {
		return container;
	}
	
	/**
	 * Returns the comparator that defines the sort order.
	 * @return Comparator The comparator that defines the sort order.
	 */
	public Comparator getComparator() {
		return comparator;
	}

	/**
	 * Overridden to sort the items in the order defined by the 
	 * <code>Comparator</code>.
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		List sortableItems = container.getSortableItems();
		originalOrder = new ArrayList(sortableItems.size());
		originalOrder.addAll(sortableItems);
		performExecute();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		container.setSortableItems(originalOrder);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#redo()
	 */
	public void redo() {
		performExecute();
	}
	
	private void performExecute() {
		List sortableItems = container.getSortableItems();
		Object[] sortedItems = sortableItems.toArray();
		Arrays.sort(sortedItems, comparator);
		container.setSortableItems(Arrays.asList(sortedItems));
	}
}
