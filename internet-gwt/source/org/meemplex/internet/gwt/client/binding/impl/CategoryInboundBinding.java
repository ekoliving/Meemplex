package org.meemplex.internet.gwt.client.binding.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.meemplex.internet.gwt.client.FacetEventHub;
import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.facets.Category;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * 
 * @author stormboy
 *
 */
public class CategoryInboundBinding extends InboundBinding {

	private HashSet<Category> categoryHandlers = new HashSet<Category>();
	
	public CategoryInboundBinding(FacetReference facetReference, FacetEventHub eventHub) {
		super(facetReference, eventHub);
		init();
    }

	public void addHandler(Category categoryHandler) {
		categoryHandlers.add(categoryHandler);
	}
	
	public void removeHandler(Category categoryHandler) {
		categoryHandlers.remove(categoryHandler);
	}

	private void init() {
		addListener(new FacetEventListener() {
			public void facetEvent(FacetEvent event) {
				String method = event.getMethod();
				String[] params = event.getParams();
				
				if (METHOD_ENTRIES_ADDED.equals(method) ) {
					JSONArray entries = JSONParser.parseLenient(params[0]).isArray();
					for (Category category : categoryHandlers) {
						category.addEntries(getAsArray(entries));
					}
				}
				else if (METHOD_ENTRIES_REMOVED.equals(method) ) {
					JSONArray entries = JSONParser.parseLenient(params[0]).isArray();
					List<String> names = new ArrayList<String>();
					for (int i=0; i<entries.size(); i++) {
						JSONObject entry = entries.get(i).isObject();
						names.add(entry.get(KEY_NAME).isString().stringValue());
					}
					for (Category category : categoryHandlers) {
						category.removeEntries(names.toArray(new String[]{}));
					}
				}
				else if (METHOD_ENTRY_RENAMED.equals(method) ) {
					String oldName  = (String) params[0];
					//String oldMeem = (String) params[1];
					String newName  = (String) params[2];
					//String newMeem  = (String) params[3];
					for (Category category : categoryHandlers) {
						category.renameEntry(oldName, newName);
					}
				}
			}
		});
	}
	
	private Category.Entry[] getAsArray(JSONArray entryList) {
		Category.Entry[] entries = new Category.Entry[entryList.size()];		
		for (int i=0; i<entryList.size(); i++) {
			JSONObject entryMap = entryList.get(i).isObject();
			String name = entryMap.get(KEY_NAME).isString().stringValue();
			String meemId = entryMap.get(KEY_MEEM).isString().stringValue();
			entries[i] = new Category.Entry(name, meemId);
		}
		
		return entries;
	}

	private static final String METHOD_ENTRIES_ADDED = "entriesAdded";
	private static final String METHOD_ENTRIES_REMOVED = "entriesRemoved";
	private static final String METHOD_ENTRY_RENAMED = "entryRenamed";

	private static final String KEY_NAME = "name";
	private static final String KEY_MEEM = "meem";

}
