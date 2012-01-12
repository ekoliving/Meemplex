/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


import org.openmaji.implementation.rpc.binding.util.MeemHelper;
import org.openmaji.meem.Meem;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;


/**
 * @author Warren Bloomer
 *
 */
public class InboundCategoryClient extends InboundBinding {

	public InboundCategoryClient() {
		setFacetClass(CategoryClient.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addCategoryClientFacet(CategoryClient listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeCategoryClientFacet(CategoryClient listener) {
		removeListener(listener);
	}
	
	/**
	 * 
	 */
	protected void invoke(String method, Object[] params) {
		
		if ("entriesAdded".equals(method) ) {
			Vector<Hashtable<String, String>> entriesVector = (Vector<Hashtable<String, String>>)params[0];
			((CategoryClient)proxy).entriesAdded( getAsArray(entriesVector) );
		}
		else if ("entriesRemoved".equals(method) ) {
			Vector<Hashtable<String, String>> entriesVector = (Vector<Hashtable<String, String>>)params[0];
			((CategoryClient)proxy).entriesRemoved( getAsArray(entriesVector) );
		}
		else if ("entryRenamed".equals(method) ) {
			try {
				String oldName  = (String) params[0];
				Meem   oldMeem  = MeemHelper.getMeem((String) params[1]);
				String newName  = (String) params[2];
				Meem   newMeem  = MeemHelper.getMeem((String) params[3]);
				CategoryEntry oldEntry = new CategoryEntry(oldName, oldMeem);
				CategoryEntry newEntry = new CategoryEntry(newName, newMeem);
				((CategoryClient)proxy).entryRenamed( oldEntry, newEntry );
			}
			catch (URISyntaxException ex) {
				System.err.println("Exception in URI: " + ex);
			}
		}
	}
	
	private CategoryEntry[] getAsArray(Vector<Hashtable<String, String>> entryList) {
		CategoryEntry[] entries = new CategoryEntry[entryList.size()];
		Iterator<Hashtable<String, String>> iter = entryList.iterator();
		
		for (int i=0; iter.hasNext(); i++) {
			Hashtable<String, String> table = iter.next();
			String name = table.get(OutboundCategoryClient.KEY_NAME);
			Meem meem = null;
			try {
				String pathString = table.get(OutboundCategoryClient.KEY_MEEM);
				meem = MeemHelper.getMeem(pathString);
			}
			catch (URISyntaxException ex) {
				System.err.println("Exception in URI: " + ex);
			}

			entries[i] = new CategoryEntry(name, meem);
		}
		
		return entries;
	}

}
