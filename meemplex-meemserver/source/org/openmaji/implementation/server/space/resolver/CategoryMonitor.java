/*
 * @(#)CategoryMonitor.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


import org.openmaji.implementation.server.Common;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CategoryMonitor implements CategoryClient {

	static private final Logger logger = LogFactory.getLogger();

	private Meem categoryMeem;
	private MeemPath categoryPath;
	private MeemResolverWedge meemResolver;

	private Map watchingFor = Collections.synchronizedMap(new HashMap());
	private Map entries = Collections.synchronizedMap(new HashMap());

	private Reference reference = null;

	public CategoryMonitor(MeemResolverWedge meemResolver, Meem categoryMeem, MeemPath categoryPath) {

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "CategoryMonitor(): " + categoryMeem.getMeemPath() +" path: " + categoryPath);
		}

		this.meemResolver = meemResolver;
		this.categoryMeem = categoryMeem;
		this.categoryPath = categoryPath;
	}

	public void setReference(Reference reference)
	{
        if (reference == null)
        {
            throw new IllegalArgumentException("attempt to add null reference to a CategoryMonitor.");
        }
		// start listening to the category
                this.reference = reference;
         
		 categoryMeem.addOutboundReference(reference, false);
	}

	public void stopWatching() {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "stopWatching(): " + this +" path: " + categoryPath);
		}
        
        //
        // if it's null we never really started so there is no reference attached.\
        //
        if (reference != null)
        {
            categoryMeem.removeOutboundReference(reference);
        }

		// go thru entries and notify all interested parties that this is no longer valid
		synchronized (entries) {
			Iterator i = entries.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				checkRemovedEntry((String) entry.getKey(), (MeemPath) entry.getValue());
			}
		}
	}

	public void addEntryToWatchFor(String entryName, MeemPath fullMeemPath) {

//		System.err.println("CategoryMonitor.addEntryToWatchFor(): " + entryName + ", " + fullMeemPath);

		synchronized (watchingFor) {
			Vector meemPaths = (Vector) watchingFor.get(entryName);

			if (meemPaths == null) {
				meemPaths = new Vector();
				watchingFor.put(entryName, meemPaths);
			}
			meemPaths.add(fullMeemPath);
		}

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "watchingFor: " + entryName + " fullMeemPath: " + fullMeemPath + " : " + this);
		}

		MeemPath meemPath = (MeemPath) entries.get(entryName);
		if (meemPath != null) {
			// this entry is already in the category

			MeemPath newEntryPath = MeemPath.spi.create(categoryPath.getSpace(), categoryPath.getLocation() + "/" + entryName);

			Vector vector = new Vector();
			vector.add(fullMeemPath);

			meemResolver.handleEntryResolved(newEntryPath, meemPath, vector);
		}
	}

	public void removeWatchedMeemPath(String entryName, MeemPath fullMeemPath) {
		synchronized (watchingFor) {
			Vector meemPaths = (Vector) watchingFor.get(entryName);

			if (meemPaths != null) {
				meemPaths.remove(fullMeemPath);
				
				if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
					LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "stopped watching : " + entryName + " fullMeemPath: " + fullMeemPath + " : " + this);
				}
				
				if (meemPaths.size() == 0) {
					watchingFor.remove(entryName);
				}
			}
		}
	}

	private void checkNewEntry(CategoryEntry newEntry) {
		Vector meemPaths = (Vector) watchingFor.get(newEntry.getName());
		if (meemPaths != null) {
			// lets tell someone
			MeemPath newEntryPath = MeemPath.spi.create(categoryPath.getSpace(), categoryPath.getLocation() + "/" + newEntry.getName());
			meemResolver.handleEntryResolved(newEntryPath, newEntry.getMeem().getMeemPath(), meemPaths);
		}
	}

	private void checkRemovedEntry(String removedEntryName, MeemPath removedEntryMeemPath) {
		Vector meemPaths = (Vector) watchingFor.get(removedEntryName);
		if (meemPaths != null) {
			// someone cares
			MeemPath removedEntryPath =
            MeemPath.spi.create(categoryPath.getSpace(), categoryPath.getLocation() + "/" + removedEntryName);
			meemResolver.handleEntryRemoved(removedEntryPath, meemPaths);
		}
	}

	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesAdded(CategoryEntry[] newEntries) {
		for (int i = 0; i < newEntries.length; i++) {
			entries.put(newEntries[i].getName(), newEntries[i].getMeem().getMeemPath());
			checkNewEntry(newEntries[i]);
		}	
	}

	/**
	 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
	 */
	public void entriesRemoved(CategoryEntry[] removedEntries) {
		for (int i = 0; i < removedEntries.length; i++) {
			entries.remove(removedEntries[i].getName());
			checkRemovedEntry(removedEntries[i].getName(), removedEntries[i].getMeem().getMeemPath());
		}
	}

	/**
	 */
	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		synchronized (entries) {
			entriesRemoved(new CategoryEntry[] {oldEntry});
			
			entries.put(newEntry.getName(), newEntry.getMeem().getMeemPath());
		}
		checkNewEntry(newEntry);
	}
}
