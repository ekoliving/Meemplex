/*
 * @(#)CategoryUtility.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.utility;

import java.util.Map;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.spi.MajiSPI;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryEntry;


/**
 * Basic utility class for dealing with categories from outside the meem world.
 * 
 * TODO turn these methods into asynchronous methods with AsyncCallback
 */
public interface CategoryUtility
{
    /**
     * Returns the Category facet on the given meem if it has one.
     * 
     * @param categoryMeem the meem that holds the category.
     * @return The Category facet or null if the categoryMeem does not have one.
     */
    public Category getCategory(Meem categoryMeem);
    
    /**
     * Returns the Category facet on the last meem in the hyperspace meem path.
     * 
     * The passed in MeemPath is interpreted as relative to the passed
     * in parent category as the root. If any entries in the path don't exist
     * they will be created.
     * 
     * @param rootMeem the equivalent of "/" for the path
     * @param categoryPath the path to the category
     * @return the category representing the end of the path, null if the path exists but is not a category.
     */
    public Category getCategory(Meem rootMeem, MeemPath categoryPath);
    
	/**
	 * Returns a hashtable representing the entries stored in the category meem.
	 * 
	 * @param categoryMeem the meem containing the category of interest.
	 * @return a hashtable of entries.
	 */
	public Map<String, CategoryEntry> getCategoryEntries(Meem categoryMeem);

    /**
     * Returns the Category entry for the passed in entryName.
     * 
     * @param categoryMeem the category of interest.
     * @param entryName the name of the entry we are looking for.
     * @return categoryEntry
     */
	public CategoryEntry getCategoryEntry(Meem categoryMeem, String entryName);
	
	/**
	 * Access point to service provider.
	 */
	public static class spi
	{
		/**
		 * Return an object implementing CategoryUtility.
		 * 
		 * @return a CategoryUtility object.
		 */
		public static synchronized CategoryUtility get() {
		  return  (CategoryUtility) MajiSPI.provider().create(CategoryUtility.class);
		}
	}
}
