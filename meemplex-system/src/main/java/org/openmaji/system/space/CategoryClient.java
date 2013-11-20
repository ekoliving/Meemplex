/*
 * @(#)CategoryClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.space;

import org.openmaji.meem.Facet;

/**
 * A client facet for receiving changes to categories.
 * 
 * @author mg
 */
public interface CategoryClient extends Facet {

    /**
     * Signal that the passed in entries have been added.
     * 
     * @param newEntries an array containing the entries that have been added.
     */
	void entriesAdded(CategoryEntry[] newEntries);
	
	/**
	 * Signal that the passed in entries have been removed.
	 * 
	 * @param removedEntries an array containing the entries that have been removed.
	 */
	void entriesRemoved(CategoryEntry[] removedEntries);
	
	/**
	 * Signal that a category entry has had it's name changed.
	 * 
	 * @param oldEntry the entry with it's original name.
	 * @param newEntry the new category entry.
	 */
	void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry);
}
