/*
 * @(#)CategoryNode.java
 * Created on 28/01/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;


import org.eclipse.jface.viewers.StructuredViewer;
import org.openmaji.implementation.intermajik.worksheet.Worksheet;
import org.openmaji.implementation.tool.eclipse.browser.relationship.space.DiagramNode;
import org.openmaji.implementation.tool.eclipse.client.CategoryProxy;
import org.openmaji.implementation.tool.eclipse.client.ConfigurationHandlerProxy;
import org.openmaji.implementation.tool.eclipse.client.MeemClientProxy;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;


/**
 * <code>CategoryNode</code>.
 * <p>
 * @author Kin Wong
 */
public class CategoryNode extends MeemNode {
	private static final long serialVersionUID = 6424227717462161145L;

	protected CategoryClient categoryClient;
	
	protected class NodeCategoryClient implements CategoryProxy.CategoryContentClient {
		private void entryAdded(CategoryEntry newEntry) {
			if(!hasRefreshed()) {
				return; // Node has NOT been expanded
			}
			Node node = new UnavailableMeemNode(newEntry, CategoryNode.this);//createNode(newEntry);
			if(node != null) {
				addChild(newEntry.getName(), node);
			}
		}
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesAdded(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesAdded(CategoryEntry[] newEntries) {
			for (int i = 0; i < newEntries.length; i++) {
				entryAdded(newEntries[i]);
			}
		}

		private void entryRemoved(CategoryEntry removedEntry) {
			if(!hasRefreshed()) return;	// Node has NOT been expanded
			if (!removeChild(removedEntry.getName())) {
				removeChild(removedEntry.getMeem().getMeemPath().toString());
			}
		}
		
		/**
		 * @see org.openmaji.system.space.CategoryClient#entriesRemoved(org.openmaji.system.space.CategoryEntry[])
		 */
		public void entriesRemoved(CategoryEntry[] removedEntries) {
			for (int i = 0; i < removedEntries.length; i++) {
				entryRemoved(removedEntries[i]);
			}	
		}

		public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
			if(!hasRefreshed()) return;	// Node has NOT been expanded
			
			MeemNode meemNode = (MeemNode)getChild(oldEntry.getName());
			if(meemNode == null) return;

			meemNode.setLabel(newEntry.getName());
			renameChild(oldEntry.getName(), newEntry.getName());
		}

		public void contentSent() {
		}

		public void contentFailed(String reason) {
		}
	};
	
	/**
	 * Constructs an instance of <code>CategoryNode</code>.
	 * <p>
	 * @param label
	 * @param proxy
	 */
	public CategoryNode(String label, MeemClientProxy proxy) {
		super(label, proxy);
	}
	
	/**
	 * Constructs an instance of <code>CategoryNode</code>.
	 * <p>
	 * @param proxy
	 */
	public CategoryNode(MeemClientProxy proxy) {
		super(proxy);
	}
	
	public CategoryProxy getCategory() {
		return getProxy().getCategoryProxy();
	}
	
	public void activate(StructuredViewer viewer) {
		super.activate(viewer);
		getCategory().addClient(getCategoryClient());
	}
	
	/**
	 */
	public void deactivate() {
		getCategory().removeClient(getCategoryClient());
		super.deactivate();
	}
	
	protected boolean initialExpandRequired() {
		return true;
	}
	
	public CategoryClient getCategoryClient() {
		if(categoryClient == null) categoryClient = createCategoryClient();
		return categoryClient;
	}
	
	protected CategoryClient createCategoryClient() {
		return new NodeCategoryClient();
	}

	public void refreshChildren() {
		CategoryEntry[] entries = getCategory().getEntryArray();
		for(int i = 0; i < entries.length; i++) {
			CategoryEntry entry = entries[i];
			Node node = new UnavailableMeemNode(entry, this);//createNode(entry);
			if(node != null) {
				Node currentNode = getChild(entry.getName());
				
				if (currentNode != null) {
					String oldName = getLabel((MeemNode)currentNode);
					String newName = getLabel((MeemNode)node);
					if (currentNode.getClass() != node.getClass() || oldName != newName) {
						removeChildInternal(entry.getName());
						addChild(entry.getName(), node);	
					}				
				}	else if (currentNode == null) {
					addChild(entry.getName(), node);
				}
			}
			
		}
	}

	private String getLabel(MeemNode node) {
		ConfigurationHandlerProxy config = node.getProxy().getConfigurationHandler();
		return (String)config.getValue(ConfigurationHandlerProxy.ID_MEEM_IDENTIFIER);
	}
	
	protected Node createNode(String name, MeemClientProxy proxy) {
		Node node;
		if(proxy.isA(Worksheet.class)) {
			node = new DiagramNode(name, proxy);
		}
		else
		if(proxy.isA(CategoryClient.class)) {
			node = new CategoryNode(name, proxy);
		}
		else {
			node = new MeemNode(name, proxy);
		}
		return node;
	}

}
