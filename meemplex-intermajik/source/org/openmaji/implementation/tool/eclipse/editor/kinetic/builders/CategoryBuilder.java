/*
 * @(#)CategoryBuilder.java
 * Created on 16/10/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Category;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Dependency;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.EntryOf;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.Meem;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;




/**
 * <code>CategoryBuilder</code> extends <code>MeemBuilder</code> with the logic
 * of building category entry connections.
 * <p>
 * @author Kin Wong
 */
public class CategoryBuilder extends MeemBuilder {
	//=== Internal CategoryClient Implementation =================================
	private CategoryClient categoryClient = new CategoryClient() {
		public void entriesAdded(CategoryEntry[] newEntries) {
			for (int i = 0; i < newEntries.length; i++) {
				addCategoryEntry(newEntries[i]);
			}
		}
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(CategoryEntry[] removedEntries) {
			for (int i = 0; i < removedEntries.length; i++) {
				removeCategoryEntry(removedEntries[i], true);
			}	
		}
		
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			renameCategoryEntry(oldEntry, newEntry);
		}
	};
	
	/**
	 * Constructs an instance of <code>CategoryBuilder</code>.
	 * <p>
	 * @param category
	 */
	public CategoryBuilder(Category category) {
		super(category);
	}
	
	/**
	 * Gets the category associates with this category builder.
	 * <p>
	 * @return Meem The meem associates with this category builder.
	 */
	protected Category getCategory() {
		return (Category)getModel();
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.MeemBuilder#activate()
	 */
	public void activate() {
		super.activate();
		getCategory().getProxy().getCategoryProxy().addClient(categoryClient);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.MeemBuilder#deactivate()
	 */
	public void deactivate() {
		getCategory().getProxy().getCategoryProxy().removeClient(categoryClient);
		super.deactivate();
	}

	/**
	 * Overridden to builds all category entry connections.
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#buildConnections()
	 */
	protected void refreshConnections() {
		Map tempEntryOfs = createEntryOfMap();
		// Going through the category content.
		Iterator it = getCategory().getProxy().getCategoryProxy().getEntries();
		while(it.hasNext()) {
			CategoryEntry entry = (CategoryEntry)it.next();
			EntryOf entryOf = (EntryOf)tempEntryOfs.get(entry);
			if(entryOf == null) {
				// Add this new category.
				addCategoryEntry(entry);
			}
			else {
				// remove EntryOf from the temporary map.
				tempEntryOfs.remove(entry);
			}
		}
		
		// Remove all the entries in the view model but not in the category.
		it = tempEntryOfs.values().iterator();
		while(it.hasNext()) {
			EntryOf entryOf = (EntryOf)it.next();
			removeCategoryEntry(entryOf.getCategoryEntry(), false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.MeemBuilder#clearConnections()
	 */
	protected void clearConnections() {
		super.clearConnections();
		
		// Going through all the entryOfs in the view model.
		EntryOf[] entryOfs = createEntryOfArray();
		for(int i = 0; i < entryOfs.length; i++) {
			EntryOf entryOf = entryOfs[i];
			removeCategoryEntry(entryOf.getCategoryEntry(), false);
		}
		
		// Going through all the Many dependencies of which this category is the 
		// target
		Dependency[] dependencies = 
			(Dependency[])getCategory().getDependencies().toArray(new Dependency[0]);
		
		for(int i = 0; i < dependencies.length; i++) {
			Dependency dependency = dependencies[i];
			getRoot().registerUnresolvedConnection(dependency);

			getRoot().removeConnection(dependency);
			dependency.detach();
			dependency.setSourceFacet(null);
			dependency.setTarget(null);
			dependency.attach();
		}
	}

	/**
	 * Creates a map that maps element ids to elements.
	 * <p>
	 * @return Map A map that maps element ids to elements.
	 */
	protected Map createEntryOfMap() {
		Iterator it = getCategory().getMeemEntries().iterator();
		HashMap entryOfs = new HashMap();
		while(it.hasNext()) {
			EntryOf entryOf = (EntryOf)it.next();
			entryOfs.put(entryOf.getId(), entryOf);
		}
		return entryOfs;
	}
	
	/**
	 * Creates a map that maps element ids to elements.
	 * <p>
	 * @return Map A map that maps element ids to elements.
	 */
	protected EntryOf[] createEntryOfArray() {
		return (EntryOf[])getCategory().getMeemEntries().toArray(new EntryOf[0]);
	}

	protected void addCategoryEntry(CategoryEntry newEntry) {
		EntryOf entryOf = (EntryOf)getRoot().findConnection(newEntry);
		if(entryOf != null) return;
		
		Meem meem = getRoot().findMeem(newEntry.getMeem().getMeemPath());
		if(meem == null) {
			// The target meem has not yet been added to the configuration
			getRoot().registerUnresolvedConnection(getId(), 
				newEntry.getMeem().getMeemPath(), newEntry);
			return;			
		}
		else {
			// Check whether this Category includes itself - Ignore the entry if so.
			if(meem.getMeemPath().equals(getCategory().getMeemPath()))
			return;
		}
		
		entryOf = new EntryOf(newEntry);
		entryOf.setCategory(getCategory());
		entryOf.setMeem(meem);
		entryOf.attach();
		getRoot().addConnection(this, entryOf, newEntry.getMeem().getMeemPath());
		if(!loadVariable(entryOf)) {
			 addVariable(entryOf);
		} 
	}
	
	protected void removeCategoryEntry(CategoryEntry removedEntry, boolean deleteVariable) {
		EntryOf entryOf = (EntryOf)getRoot().findConnection(removedEntry);
		if(entryOf == null) return;
		if(deleteVariable) deleteVariable(entryOf.getPath());
		
		getRoot().removeConnection(entryOf);
		entryOf.detach();
		entryOf.setCategory(null);
		entryOf.setMeem(null);
		entryOf.attach();
	}
	
	protected void renameCategoryEntry(CategoryEntry oldEntry, CategoryEntry newEntry) {
		EntryOf entryOf = (EntryOf)getRoot().findConnection(oldEntry);
		if(entryOf == null) return;
		entryOf.setName(newEntry.getName());
	}
}
