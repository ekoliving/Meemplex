/*
 * @(#)ISortableContainer.java
 * Created on 16/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.sorting;

import java.util.Collection;
import java.util.List;

/**
 * <code>ISortableContainer</code> defines the contract of a container with 
 * sortable items.
 * <p>
 * @author Kin Wong
 */
public interface ISortableItemContainer {
	/**
	 * Returns a clone of all the sortable items in the list in their original
	 * order.
	 * @return List A clone of all the sorable items in the list in their 
	 * original order.
	 */
	List getSortableItems();
	
	/**
	 * Replaces all the sortable items in the list in the order in the passed-in
	 * collection.
	 * @param items The sorted order in the passed-in collection.
	 */
	void setSortableItems(Collection items);
}
