/*
 * @(#)CategoryClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.ui.view.category;

import org.openmaji.system.space.CategoryEntry;

/**
    * <p>
    * ...
    * </p>
    * @author  mg
    * @version 1.0
    */
public class CategoryClient implements org.openmaji.system.space.CategoryClient {

	CategoryContentProvider provider = null;

	public void setProvider(CategoryContentProvider provider) {
		this.provider = provider;
	}

	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesAdded(CategoryEntry[] newEntries) {
		for (int i = 0; i < newEntries.length; i++) {
			provider.addEntry(newEntries[i]);
		}
	}

	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesRemoved(CategoryEntry[] removedEntries) {
		for (int i = 0; i < removedEntries.length; i++) {
			provider.removeEntry(removedEntries[i]);
		}	
	}
	
	/**
	 * @see org.openmaji.system.space.CategoryClient#entryRenamed(org.openmaji.system.space.CategoryEntry, org.openmaji.system.space.CategoryEntry)
	 */
	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry)  {
		provider.renameEntry(oldEntry, newEntry);
	}
}
