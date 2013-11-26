/*
 * @(#)CategoryWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 */
package org.openmaji.implementation.server.space;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mg
 * @author stormboy
 */

public class CategoryWedge implements Category, MeemDefinitionProvider, Wedge {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static final Level LOG_LEVEL = Common.getLogLevelVerbose();

	/* -------------------- outbound facets ------------------------- */

	public CategoryClient categoryClient;
	
	public final ContentProvider<CategoryClient> categoryClientProvider = new ContentProvider<CategoryClient>() {
		public void sendContent(CategoryClient client, Filter filter) throws ContentException {
			synchronized (entries) {
				if (filter == null) {
					// send entries in the order they were added
					client.entriesAdded(entries.toArray(arraySpec));
				}
				else if (filter instanceof ExactMatchFilter) {
					ExactMatchFilter<?> exactMatchFilter = (ExactMatchFilter<?>) filter;
					Object template = exactMatchFilter.getTemplate();

					if (template instanceof String) {
						String stringTemplate = (String) template;
						CategoryEntry entry = entriesMap.get(stringTemplate);

						if (entry != null) {
							client.entriesAdded(new CategoryEntry[] { entry });
						}
					}
					else {
						throw new ContentException("Unsupported template type: " + template.getClass());
					}
				}
				else {
					throw new ContentException("Unsupported filter type: " + filter.getClass());
				}
			}
		}
	};

	/* -------------------- conduits ---------------------- */

	public Category categoryConduit = new CategoryConduitImpl(this);

	public CategoryClient categoryClientConduit = null;

	public ManagedPersistenceHandler managedPersistenceHandlerConduit;

	/* ------------------- persisted properties ---------------- */

	/** a map of entryentriesMap names to entries */
	public Map<String, CategoryEntry> entriesMap = new HashMap<String, CategoryEntry>();

	/** an ordered list of entries */
	public List<CategoryEntry> entries = new ArrayList<CategoryEntry>();

	/* ------------------ Category interface -------------------- */

	/**
	 * @see org.openmaji.system.space.Category#addEntry(java.lang.String,
	 *      org.openmaji.meem.Meem)
	 */
	public void addEntry(String entryName, Meem meem) {
		if (entryName == null) {
			throw new RuntimeException("Attempt to add entry to category with null name");
		}

		synchronized (entries) {
			if (Common.TRACE_ENABLED && Common.TRACE_CATEGORY && entriesMap.containsKey(entryName)) {
				logger.log(LOG_LEVEL, "Overwriting entry " + entriesMap.get(entryName));
			}

			// remove existing entry if it exists
			removeEntry(entryName);

			if (meem != null) {

				CategoryEntry categoryEntry = new CategoryEntry(entryName, meem);

				entriesMap.put(entryName, categoryEntry);
				entries.add(categoryEntry);

				if (Common.TRACE_ENABLED && Common.TRACE_CATEGORY) {
					logger.log(LOG_LEVEL, this.toString() + " added entry: " + entryName + " meemPath: " + meem.getMeemPath());
				}

				// persist content
				managedPersistenceHandlerConduit.persist();

				categoryClient.entriesAdded(new CategoryEntry[] { categoryEntry });
				categoryClientConduit.entriesAdded(new CategoryEntry[] { categoryEntry });
			}
		}
	}

	/**
	 * @see org.openmaji.system.space.Category#removeEntry(java.lang.String)
	 */
	public void removeEntry(String entryName) {
		synchronized (entries) {
			CategoryEntry categoryEntry = entriesMap.remove(entryName);
			entries.remove(categoryEntry);

			if (categoryEntry != null) {

				if (Common.TRACE_ENABLED && Common.TRACE_CATEGORY)
					logger.log(LOG_LEVEL, "removed entry: " + entryName + " meemPath: " + categoryEntry.getMeem().getMeemPath());

				// persist content
				managedPersistenceHandlerConduit.persist();

				categoryClient.entriesRemoved(new CategoryEntry[] { categoryEntry });
				categoryClientConduit.entriesRemoved(new CategoryEntry[] { categoryEntry });
			}
		}
	}

	/**
	 * @see org.openmaji.system.space.Category#renameEntry(java.lang.String,
	 *      java.lang.String)
	 */
	public void renameEntry(String oldEntryName, String newEntryName) {

		synchronized (entries) {
			CategoryEntry oldCategoryEntry = entriesMap.remove(oldEntryName);
			int i = entries.indexOf(oldCategoryEntry);
			if (oldCategoryEntry != null) {

				CategoryEntry newCategoryEntry = oldCategoryEntry.rename(newEntryName);

				entriesMap.put(newEntryName, newCategoryEntry);
				if (i >= 0) {
					entries.set(i, newCategoryEntry);
				}

				if (Common.TRACE_ENABLED && Common.TRACE_CATEGORY)
					logger.log(LOG_LEVEL, "renamed entry: " + oldEntryName + " to " + newEntryName);

				// persist content
				managedPersistenceHandlerConduit.persist();

				categoryClient.entryRenamed(oldCategoryEntry, newCategoryEntry);
				categoryClientConduit.entryRenamed(oldCategoryEntry, newCategoryEntry);
			}
		}
	}

	/*------------------- Category conduit -----------------*/

	private final class CategoryConduitImpl implements Category {

		private CategoryWedge categoryWedge = null;

		public CategoryConduitImpl(CategoryWedge categoryWedge) {
			this.categoryWedge = categoryWedge;
		}

		/**
		 * @see org.openmaji.system.space.Category#addEntry(java.lang.String,
		 *      org.openmaji.meem.Meem)
		 */
		public void addEntry(String entryName, Meem meem) {
			categoryWedge.addEntry(entryName, meem);
		}

		/**
		 * @see org.openmaji.system.space.Category#removeEntry(java.lang.String)
		 */
		public void removeEntry(String entryName) {
			categoryWedge.removeEntry(entryName);
		}

		/**
		 * @see org.openmaji.system.space.Category#renameEntry(java.lang.String,
		 *      java.lang.String)
		 */
		public void renameEntry(String oldEntryName, String newEntryName) {
			categoryWedge.renameEntry(oldEntryName, newEntryName);
		}

	}

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return (meemDefinition);
	}

	private static final CategoryEntry[] arraySpec = new CategoryEntry[0];
}
