/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmaji.meem.Meem;
import org.openmaji.rpc.binding.OutboundBinding;
import org.openmaji.system.space.CategoryClient;
import org.openmaji.system.space.CategoryEntry;


/**
 * An outbound Boolean Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundCategoryClient extends OutboundBinding implements CategoryClient {

	public static final String KEY_NAME = "name";
	public static final String KEY_MEEM = "meem";
	
	/**
	 *
	 */
	public OutboundCategoryClient() {
		setFacetClass(CategoryClient.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void entriesAdded(CategoryEntry[] entries) {
		send(
				"entriesAdded", 
				new Object[] { getAsList(entries) }
			);
	}
	
	public void entriesRemoved(CategoryEntry[] entries) {
		
		send(
				"entriesRemoved", 
				new Object[] { getAsList(entries) }
			);		
	}
	
	public void entryRenamed(CategoryEntry oldEntry, CategoryEntry newEntry) {
		send(
				"entryRenamed", 
				new Object[] { 
						oldEntry.getName(), 
						oldEntry.getMeem().getMeemPath().toString(),
						newEntry.getName(),
						newEntry.getMeem().getMeemPath().toString()
					}
			);		
	}

	
	private List<Map<String,String>> getAsList(CategoryEntry[] entries) {
		List<Map<String,String>> entryList = new ArrayList<Map<String,String>>();
		for (int i=0; i<entries.length; i++) {
			String name = entries[i].getName();
			Meem meem = entries[i].getMeem();
			Map<String, String> entry = new HashMap<String, String>();
			entry.put(KEY_NAME, name);
			entry.put(KEY_MEEM, meem.getMeemPath().toString());
			
			entryList.add(entry);
		}

		return entryList;
	}
}
