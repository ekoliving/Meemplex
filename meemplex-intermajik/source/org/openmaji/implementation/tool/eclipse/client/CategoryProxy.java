/*
 * @(#)CategoryProxy.java
 * Created on 22/09/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;


import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.system.meem.wedge.reference.*;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;

/**
 * <code>CategoryProxy</code> represents the client-side proxy of Category. It
 * provides a simple cache of category entries.
 * <p>
 * @author Kin Wong
 */
public class CategoryProxy extends FacetProxy implements Category {
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(Category.class, "category"),
			new FacetOutboundSpecification(CategoryClient.class, "categoryClient"));

	static private CategoryEntry[] EMPTY_CATEGORY_ENTRY = new CategoryEntry[0];
	private Map nameToEntries;
	
	//=== Internal CategoryContentClient Implementation ==========================
	public static interface CategoryContentClient extends CategoryClient, ContentClient {}
	
	public static class LocalCategoryClient
		implements CategoryContentClient
	{
		Map	nameToEntries;
		CategoryProxy	p;
		
		public LocalCategoryClient(
			CategoryProxy	p,
			Map					nameToEntries)
		{
			this.p = p;
			this.nameToEntries = nameToEntries;
		}
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesAdded(final CategoryEntry[] newEntries) {
			// Put all of them into Map
			for (int i = 0; i < newEntries.length; i++) {
				CategoryEntry newEntry = newEntries[i];
				nameToEntries.put(newEntry.getName(), newEntry);
			}
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireEntryAdded(newEntries);
				}
			});
		}
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(final CategoryEntry[] removedEntries) {
			for (int i = 0; i < removedEntries.length; i++) {
				CategoryEntry entry = removedEntries[i];
				nameToEntries.remove(entry.getName());
			}
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireEntryRemoved(removedEntries);
				}
			});
		}
	
	public void entryRenamed(final CategoryEntry oldEntry, final CategoryEntry newEntry) {
			nameToEntries.remove(oldEntry.getName());
			nameToEntries.put(newEntry.getName(), newEntry);
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireEntryRenamed(oldEntry, newEntry);
				}
			});
		}
		
		/**
		 * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
		 */		
		public void contentSent() {
			p.contentInitialized = true;
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireContentSent();
				}
			});
		}

		/**
		 * 
		 */
		public void contentFailed(final String reason) {
			p.contentInitialized = true;
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireContentFailed(reason);
				}
			});
		}
	}
	
	protected CategoryClient categoryClient;

	/**
	 * Constructs an instance of <code>CategoryProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 */
	public CategoryProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}
	
	public CategoryProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}	

	private Category getCategory() {
		return (Category)getInboundReference();
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected synchronized Facet getOutboundTarget() {
		if(categoryClient == null) {
			categoryClient = createClient(nameToEntries);
		}
		return categoryClient;
	}
	
	protected CategoryClient createClient(Map nameToEntries) {
		return new LocalCategoryClient(this, nameToEntries);
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	synchronized protected void clearContent() {
		nameToEntries = new HashMap();
		categoryClient = null;
	}

	/**
	 * Checks whether this entry name is already used as an entry name in the 
	 * category.
	 * @param name 
	 * @return boolean
	 */
	public synchronized boolean contains(String name) {
		return nameToEntries.containsKey(name);
	}
	
	public synchronized boolean isEmpty() {
		return nameToEntries.isEmpty();
	}
	
	/**
	 * Gets all the entries of this category.
	 * @return Iterator An iterator that can used to iterate through all the 
	 * enties of this category as <code>CategoryEntry</code>.
	 */
	public synchronized Iterator getEntries() {
		if(nameToEntries == null) return Collections.EMPTY_LIST.iterator();
//		return nameToEntries.values().iterator();
		return new Vector(nameToEntries.values()).iterator();
	}

	public synchronized CategoryEntry[] getEntryArray() {
		if(nameToEntries == null) return EMPTY_CATEGORY_ENTRY;
		return (CategoryEntry[])nameToEntries.values().toArray(new CategoryEntry[0]);
	}

	/**
	 * Checks whether this category contains the entry name.
	 * @param entryName The entry name to check
	 * @return boolean true if it contains it, false otherwise.
	 */	
	public synchronized boolean containsEntryName(String entryName) {
		if(nameToEntries == null)	return false;
		return nameToEntries.containsKey(entryName);
	}

	//=== Client Management ======================================================
	private void fireEntryAdded(CategoryEntry[] newEntries) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			CategoryClient client = (CategoryClient)clients[i];
			client.entriesAdded(newEntries);
		}
	}
	
	private void fireEntryRemoved(CategoryEntry[] removedEntries) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			CategoryClient client = (CategoryClient)clients[i];
			client.entriesRemoved(removedEntries);
		}
	}
	
	private void fireEntryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			CategoryClient client = (CategoryClient)clients[i];
			client.entryRenamed(oldEntry, newEntry);
		}
	}

	private void fireContentSent() {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			CategoryClient client = (CategoryClient)clients[i];
			if(client instanceof ContentClient) {
				((ContentClient)client).contentSent();
			}
		}
	}

	private void fireContentFailed(String reason) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			CategoryClient client = (CategoryClient)clients[i];
			if(client instanceof ContentClient) {
				((ContentClient)client).contentFailed(reason);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */	
	protected void realizeClientContent(Object client) {
		CategoryClient categoryClient = (CategoryClient)client;
		categoryClient.entriesAdded(getEntryArray());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */	
	protected void clearClientContent(Object client) {
		CategoryClient categoryClient = (CategoryClient)client;
		categoryClient.entriesRemoved(getEntryArray());
	}
	
	//=== External Category Implementation =======================================
	/**
	 * @see org.openmaji.system.space.Category#addEntry(java.lang.String, org.openmaji.meem.Meem)
	 */	
	public void addEntry(final String entryName, final Meem meem) {
		if(isReadOnly()) return;
	
		getCategory().addEntry(entryName, meem);
	}
	
	/**
	 * @see org.openmaji.system.space.Category#removeEntry(java.lang.String)
	 */
	public void removeEntry(final String entryName) {
		if(isReadOnly()) return;

		getCategory().removeEntry(entryName);
	}
	
	/**
	 * @see org.openmaji.system.space.Category#renameEntry(java.lang.String, java.lang.String)
	 */
	public void renameEntry(final String oldEntryName, final String newEntryName) {
		if(isReadOnly()) return;

		getCategory().renameEntry(oldEntryName, newEntryName);
	}
}
