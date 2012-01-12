/*
 * @(#)DiagramBuilder.java
 * Created on 24/09/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.util.Iterator;
import java.util.Map;

import org.openmaji.implementation.tool.eclipse.client.*;
import org.openmaji.implementation.tool.eclipse.editor.common.model.Element;
import org.openmaji.implementation.tool.eclipse.editor.kinetic.model.*;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;



/**
 * <code>DiagramBuilder</code> represents the incremental building logic of a 
 * diagram based on the content of a category. 
 * <p>
 * Given a category, the <code>DiagramBuilder</code> verified the content of the 
 * associated diagram view model with the content of the category and ensures 
 * they are in-sync. It also listens to changes in the category and update the 
 * diagram accordingly.
 * <p>
 * @author Kin Wong
 */

public class DiagramBuilder extends ElementContainerBuilder {
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
				removeCategoryEntry(removedEntries[i].getMeem().getMeemPath());
			}	
		}
		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			renameCategoryEntry(oldEntry, newEntry);
		}

	};

	/**
	 * Constructs an instance of <code>DiagramBuilder</code>.
	 * <p>
	 * @param diagram The diagram view model to be associated with this diagram
	 * builder.
	 */
	public DiagramBuilder(Diagram diagram) {
		super(diagram);
	}
	
	/**
	 * Gets the diagram associates with this diagram builder.
	 * @return Diagram The diagram associatres with this builder.
	 */
	public Diagram getDiagram() {
		return (Diagram)getModel();
	}
	
	/**
	 * Gets the category associates with this diagram builder.
	 * <p>
	 * @return CategoryProxy The category proxy associates with this diagram 
	 * builder.
	 */
	protected CategoryProxy getCategory() {
		return getDiagram().getProxy().getCategoryProxy();
	}
	
	/**
	 * Gets the variable map associates with this diagram builder.
	 * <p>
	 * @return VariableMapProxy The variable map associates with this diagram
	 * builder.
	 */
	protected VariableMapProxy getVariableMap() {
		return getDiagram().getProxy().getVariableMapProxy();
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#activate()
	 */
	public void activate(){
		getCategory().addClient(categoryClient);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#deactivate()
	 */
	public void deactivate() {
		getCategory().removeClient(categoryClient);
	}
	
	/**
	 * Builds the meem view model based on the category content and optionally
	 * the variable map.
	 */
	protected void refreshContents() {
		// Build a temporary map, element(s) left must be removed at the end.
		Map tempMeems = createChildElementMap();
	
		// Going through the category content.
		Iterator it = getCategory().getEntries();
		while(it.hasNext()) {
			CategoryEntry entry = (CategoryEntry)it.next();
			Meem meem = (Meem)tempMeems.get(entry.getMeem().getMeemPath());
			if(meem == null) {
				// Add this new category.
				addCategoryEntry(entry);
			}
			else {
				getChildBuilder(meem.getId()).refresh();
				// remove from the temporary map.
				tempMeems.remove(entry.getMeem().getMeemPath());
			}
		}
		// Remove all the meem in the view model but not in the category.
		it = tempMeems.keySet().iterator();
		while(it.hasNext()) {
			removeCategoryEntry((MeemPath)it.next());
		}
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearContents()
	 */
	protected void clearContents() {
		// Going through the category content.
		Element[] children = createChildElementArray();
		for(int i = 0; i < children.length; i++) {
			Meem meem = (Meem)children[i];
			removeCategoryEntry(meem.getMeemPath());
		}
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#refreshConnections()
	 */
	protected void refreshConnections() {
	}

	/**
	 * @see org.openmaji.implementation.tool.eclipse.editor.kinetic.builders.ElementBuilder#clearConnections()
	 */
	protected void clearConnections() {
	}
	
	protected void refreshCategoryEntry(MeemPath meemPath) {
		Meem meem = getDiagram().findMeem(meemPath);
		if(meem != null) {
			removeCategoryEntry(meemPath);
		}
		addCategoryEntry(new CategoryEntry(meem.getName(), meem.getProxy().getUnderlyingMeem()));
	}
	/**
	 * Adds a new category entry to the diagram.
	 * @param newEntry The new category entry to be added.
	 */
	protected void addCategoryEntry(CategoryEntry newEntry) {
		locateProxy(newEntry);
	}
	
	protected void proxyLocated(final CategoryEntry newEntry, final MeemClientProxy proxy) {
		proxy.getSynchronizer().execute(new Runnable() {  
			public void run() {
				addCategoryEntry(newEntry, proxy);	
			}
		});				
	}

	protected void addCategoryEntry(CategoryEntry newEntry, MeemClientProxy proxy) {
		Meem meem = (Meem)getDiagram().findMeem(newEntry.getMeem().getMeemPath());
		
		if(meem == null) {
			// The Meem is not found, create the meem view model.
			meem = MeemModelFactory.create(proxy);
			meem.setName(newEntry.getName());
			getContainerModel().addChild(meem);
		}
		
		ElementBuilder builder = getChildBuilder(meem.getId());
		if(builder == null) {
			MeemBuilder meemBuilder = createMeemBuilder(meem);
			addChildBuilder(meemBuilder);
		}
		if(!loadVariable(meem)) {
			getRoot().invalidateLayout(meem);
			addVariable(meem); 
		} 
	}
	
	protected void removeCategoryEntry(MeemPath meemPath) {
		Meem meem = getDiagram().findMeem(meemPath);
		if(meem != null) removeChild(meem.getId());
	}

	protected void renameCategoryEntry(CategoryEntry oldEntry, CategoryEntry newEntry) {
		Meem meem = getDiagram().findMeem(oldEntry.getMeem().getMeemPath());
		if(meem != null) {
			meem.setName(newEntry.getName());
		}
	}

	/**
	 * Creates a meem builder from the meem model.
	 * @param meem The meem the builder is created for.
	 * @return MeemBuilder The meem builder for building the meem.
	 */
	private MeemBuilder createMeemBuilder(Meem meem) {
		if(meem instanceof Category) {
			return new CategoryBuilder((Category)meem);
		}
		return new MeemBuilder(meem);
	}
	
	private void locateProxy(final CategoryEntry newEntry) {
		Runnable runnable = new Runnable() {
			public void run() {
				MeemClientProxy proxy = InterMajikClientProxyFactory.getInstance().create(newEntry.getMeem()); 
				proxyLocated(newEntry, proxy);
			}
		};
		new Thread(runnable).start();
	}

}
