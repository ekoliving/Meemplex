/*
 * @(#)CategoryTraverser.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.hyperspace.utility;

import java.util.LinkedHashMap;
import java.util.Map;


import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.MeemHelper;
import org.openmaji.server.helper.MeemPathResolverHelper;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Given a starting MeemPath, return a Map containing all child entries. 
 * The key of the resulting Map is a String representing the path to the meem relative to the starting MeemPath.
 * The value of the Map is (currently) the MeemPath of the meem. 
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CategoryTraverser implements CategoryClient, ContentClient {

  private static final Logger logger = Logger.getAnonymousLogger();

	private Reference reference = Reference.spi.create("categoryClient", this, true, null);
	private Meem meem;

	private String currentPath;

	private LinkedHashMap categoriesToScan = new LinkedHashMap();

	public LinkedHashMap meems = new LinkedHashMap();

	int iUpto;

	public synchronized Map traverse(MeemPath meemPath) {

		iUpto = 0;
		
		meems.clear();
		categoriesToScan.clear();
		currentPath = "";

		Meem resolvedMeem = MeemPathResolverHelper.getInstance().resolveMeemPath(meemPath);

		if (resolvedMeem == null) 
			return meems;

		categoriesToScan.put("", resolvedMeem);

		scanNextCategory();

		return meems;
	}

	private void scanNextCategory() {

		// grab the meempath using an array. If I use an iterator and grab the first one 
		// and remove it, then i might scan the same category more than once.

		if (iUpto < categoriesToScan.size()) {

			currentPath = (String) categoriesToScan.keySet().toArray(new String[0])[iUpto];

			meem = (Meem) categoriesToScan.get(currentPath);

			meem.addOutboundReference(reference, false);
		}
	}
	
	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesAdded(CategoryEntry[] newEntries) {
		for (int i = 0; i < newEntries.length; i++) {
			CategoryEntry newEntry = newEntries[i];
			
			Meem meem = newEntry.getMeem();
	    if ( meem == null )
	    {
	      logger.log(Level.WARNING, "entryAdded() - null returned by locateMeem()");
	      return;
	    }
	
			if (MeemHelper.isA(meem, Category.class)) {
				categoriesToScan.put(currentPath + "/" + newEntry.getName(), newEntry.getMeem());
			} 
			meems.put(currentPath + "/" + newEntry.getName(), newEntry.getMeem().getMeemPath());
		}

	}

	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesRemoved(CategoryEntry[] removedEntries) {
		// don't care - ignore
	}


	/**
	 */
	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		// don't care - ignore
	}

	/**
	 */
	public void contentSent() {
		// remove last reference
		meem.removeOutboundReference(reference);

		iUpto++;

		scanNextCategory();
	}

	/**
	 */
	public void contentFailed(String reason) {
		// remove last reference
		meem.removeOutboundReference(reference);

		// TODO[peter] What to do on failure?
	}
}
