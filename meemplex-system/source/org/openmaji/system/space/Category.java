/*
 * @(#)Category.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.space;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.spi.MajiSPI;


/**
 * Categories provide the equivalent to a directory structure in Maji.
 * Meems that also represent categories should implement the Category
 * facet to as the interface to be used by others to add and remove entries.
 * 
 * @author mg
 */

public interface Category extends Facet {
	
/**
 * Add a meem to the category with the given entry name.
 * 
 * @param entryName the entry name for the meem.
 * @param meem the meem to be added.
 */

	void addEntry(String entryName, Meem meem);
	
/**
 * Remove the category entry with the passed in name.
 * 
 * @param entryName the name of the entry to be removed.
 */

	void removeEntry(String entryName);
	
/**
 * Rename the category entry with name oldEntryName to newEntryName
 * 
 * @param oldEntryName the current name for the catgory entry.
 * @param newEntryName the new name for the category entry.
 */

	void renameEntry(String oldEntryName, String newEntryName);

  /**
   * Nested class for service provider.
   */
  public static class spi
  {
    public static synchronized Category get()
    {
      return (Category) MajiSPI.provider().create(Category.class);
    }
  }
}