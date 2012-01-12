/*
 * @(#)LifeCycleManagerCategoryWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.lifecycle.category;

import java.util.*;

import org.openmaji.implementation.server.Common;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.ExactMatchFilter;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategory;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerCategoryClient;
import org.openmaji.system.meem.wedge.persistence.ManagedPersistenceHandler;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.CategoryEntry;

/**
 * <p>
 * This is the Category that will store all the meems that this LifeCycleManager
 * is resposible for.
 * 
 * A lot of this code comes from CategoryWedge. There is probably a better way of 
 * doing this.
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class LifeCycleManagerCategoryWedge implements LifeCycleManagerCategory, LifeCycleManagementClientCategory, Wedge {

	private static final Logger logger = LogFactory.getLogger();
	
	public MeemContext meemContext;

	public LifeCycleManagerCategoryClient lifeCycleManagerCategoryClient;
	public final ContentProvider lifeCycleManagerCategoryClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws ContentException {
			LifeCycleManagerCategoryClient categoryClient = (LifeCycleManagerCategoryClient) target;

			if (filter == null) {
				categoryClient.entriesAdded((CategoryEntry[])entries.values().toArray(new CategoryEntry[0]));
			}
			else if (filter instanceof ExactMatchFilter) {
				ExactMatchFilter exactMatchFilter = (ExactMatchFilter) filter;
				Object template = exactMatchFilter.getTemplate();

				if (template instanceof String) {
					String stringTemplate = (String) template;
					CategoryEntry entry = (CategoryEntry) entries.get(stringTemplate);

					if (entry != null) {
						categoryClient.entriesAdded(new CategoryEntry[] {entry});
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
	};

	/**
	 * Persisted field.
	 */
	public Map entries = new HashMap();

	/**
	 * Dependecies
	 */
	private Map lcmDependencies = new HashMap();


	/* ------------------------------ conduits -------------------------------------- */
	
	public ManagedPersistenceHandler managedPersistenceHandlerConduit;
	
	public LifeCycleManagerCategoryConduit lifeCycleManagerCategoryConduit = new LifeCycleManagerCategoryConduitImpl();
	
	public LifeCycleManagerCategoryClient lifeCycleManagerCategoryClientConduit;
	
	public DependencyHandler dependencyHandlerConduit; // outbound


	/* --------------------------- Category interface ------------------------------- */
	
	/**
	 * @see org.openmaji.system.space.Category#addEntry(java.lang.String, org.openmaji.meem.Meem)
	 */
	public void addEntry(String entryName, Meem meem) {
		// add dependency

		DependencyAttribute lcmDependencyAttribute =
			new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meem, "lifeCycleManagerClient", null, true);

		dependencyHandlerConduit.addDependency("lifeCycleManagerClientCategory", lcmDependencyAttribute, LifeTime.TRANSIENT);

		lcmDependencies.put(meem, lcmDependencyAttribute);
	}

	/**
	 * @see org.openmaji.system.space.Category#removeEntry(java.lang.String)
	 */
	public void removeEntry(String entryName) {
		
		CategoryEntry categoryEntry = (CategoryEntry) entries.get(entryName);

		if (categoryEntry != null) {

			LifeCycleManager selfLifeCycleManager = (LifeCycleManager) meemContext.getTarget("lifeCycleManager");
			
			//-mg- this should be replaced with an unbound meem when they appear
			selfLifeCycleManager.destroyMeem(categoryEntry.getMeem());
		}
	}

	/**
	 * @see org.openmaji.system.space.Category#renameEntry(java.lang.String, java.lang.String)
	 */
	public void renameEntry(String oldEntryName, String newEntryName) {
		lifeCycleManagerCategoryConduit.renameEntry(oldEntryName, newEntryName);
	}

	/* ---------------------- LifeCycleManagementClient methods -------------------- */

	/**
	 * @see org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagementClient#parentLifeCycleManagerChanged(org.openmaji.meem.Meem, org.openmaji.system.manager.lifecycle.LifeCycleManager)
	 */
	public void parentLifeCycleManagerChanged(Meem meem, LifeCycleManager lifeCycleManager) {

		LifeCycleManager selfLifeCycleManager = (LifeCycleManager) meemContext.getTarget("lifeCycleManager");
		lifeCycleManager.transferMeem(meem, selfLifeCycleManager);

		// remove dependency

		DependencyAttribute dependencyAttribute = (DependencyAttribute) lcmDependencies.remove(meem);
		dependencyHandlerConduit.removeDependency(dependencyAttribute);
	}

	/* ------------------------ LifeCycleManagerCategory conduit -------------------------------*/

	/*
	 * This is the conduit class. This is the only place where entries are really added/removed 
	 * from the category. 
	 */
	private final class LifeCycleManagerCategoryConduitImpl implements LifeCycleManagerCategoryConduit {
		public void addEntry(String entryName, Meem meem) {
			if (entryName == null) {
				throw new RuntimeException("Attempt to add entry to category with null name");
			}

			CategoryEntry categoryEntry = new CategoryEntry(entryName, meem);
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(logger, Common.getLogLevelVerbose(), "addEntry: " + entryName + " : " + categoryEntry);
			}
			entries.put(entryName, categoryEntry);

			// persist content
			managedPersistenceHandlerConduit.persist();

			lifeCycleManagerCategoryClient.entriesAdded(new CategoryEntry[] { categoryEntry });
			lifeCycleManagerCategoryClientConduit.entriesAdded(new CategoryEntry[] { categoryEntry });
		}

		/**
		 * @see org.openmaji.system.space.Category#removeEntry(java.lang.String)
		 */
		public void removeEntry(String entryName) {
			CategoryEntry categoryEntry = (CategoryEntry) entries.remove(entryName);
			if (Common.TRACE_ENABLED && Common.TRACE_LIFECYCLEMANAGER) {
				LogTools.trace(logger, Common.getLogLevelVerbose(), "removeEntry: " + entryName + " : " + categoryEntry);
			}
			if (categoryEntry != null) {

				// persist content
				managedPersistenceHandlerConduit.persist();

				lifeCycleManagerCategoryClient.entriesRemoved(new CategoryEntry[] {categoryEntry});
				lifeCycleManagerCategoryClientConduit.entriesRemoved(new CategoryEntry[] {categoryEntry});
				
			}

		}

		/**
		 * @see org.openmaji.system.space.Category#renameEntry(java.lang.String, java.lang.String)
		 */
		public void renameEntry(String oldEntryName, String newEntryName) {
			CategoryEntry oldCategoryEntry = (CategoryEntry) entries.remove(oldEntryName);
			if (oldCategoryEntry != null) {

				CategoryEntry newCategoryEntry = oldCategoryEntry.rename(newEntryName);

				entries.put(newEntryName, newCategoryEntry);

				// persist content
				managedPersistenceHandlerConduit.persist();

				lifeCycleManagerCategoryClient.entryRenamed(oldCategoryEntry, newCategoryEntry);
				lifeCycleManagerCategoryClientConduit.entryRenamed(oldCategoryEntry, newCategoryEntry);
			}
		}

		public void sendContent() {
			Iterator iter = entries.values().iterator();
			while (iter.hasNext()) {
				CategoryEntry entry = (CategoryEntry) iter.next();

				lifeCycleManagerCategoryClientConduit.entriesAdded(new CategoryEntry[] { entry });
			}

			lifeCycleManagerCategoryClientConduit.contentSent();
		}
	}
}
