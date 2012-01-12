/*
 * @(#)CategoryEntryNameFactory.java
 * Created on 2/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.implementation.intermajik.worksheet.Worksheet;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.CategoryClient;



/**
 * <code>CategoryEntryNameFactory</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryEntryNameFactory {
	static public String createUniqueEntryName(CategoryProxy category, String preferredName) {
		if(!category.contains(preferredName)) return preferredName;
		int nextEntry = 1;
		String entryName = null;
		do {
			entryName = preferredName + "(" + nextEntry + ")";
			nextEntry++;
		}
		while(category.contains(entryName));
		return entryName;
	}
	
	static public String createUniqueEntryName(CategoryProxy category, MeemPath meemPath) {
		return createUniqueEntryName(category, SecurityManager.getInstance().getGateway().getMeem(meemPath));
	}
	
	/**
	 * Creates a unique entry name for a meem in a category.
	 * @param category The category facet proxy for the entry.
	 * @param meem The meem the entry is for.
	 * @return String a unique entry name for a meem in a category.
	 */
	static public String createUniqueEntryName(CategoryProxy category, Meem meem) {
		int nextEntry = 1;
		String prefix = getPrefix(meem) + ' ';
		String entryName = null;
		do {
			 entryName = prefix + Integer.toString(nextEntry++);
		}
		while(category.contains(entryName));
		return entryName;
	}

	static private String getPrefix(Meem meem) {
		
		String prefix = "Meem";
		if(meem != null) {
			MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().create(meem);
			if(proxy.isA(Worksheet.class)) {
				prefix = "Worksheet";
			}
			else
			if(proxy.isA(CategoryClient.class)) {
				prefix = "Category";
			}
		}
		return prefix;
	}
}
