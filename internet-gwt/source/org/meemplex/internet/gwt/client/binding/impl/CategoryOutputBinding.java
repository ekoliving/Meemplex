package org.meemplex.internet.gwt.client.binding.impl;

import org.meemplex.internet.gwt.client.FacetEventHub;
import org.meemplex.internet.gwt.client.OutboundBinding;
import org.meemplex.internet.gwt.client.facets.Category;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class CategoryOutputBinding extends OutboundBinding implements Category {

	public CategoryOutputBinding(FacetReference facetReference, FacetEventHub eventHub) {
		super(facetReference, eventHub);
    }
	
	@Override
	public void addEntries(Entry[] entries) {
		if (entries != null) {
			for (Entry entry : entries) {
				JSONValue[] params = new JSONValue[] { new JSONString(entry.getName()), new JSONString(entry.getMeemId()) };
				send(METHOD_ADD_ENTRY, params);
			}
		}
	}
	
	@Override
	public void removeEntries(String[] names) {
		if (names != null) {
			for (String name : names) {
				JSONValue[] params = new JSONValue[] { new JSONString(name) };
				send(METHOD_REMOVE_ENTRY, params);
			}
		}
	}
	
	public void renameEntry(String oldName, String newName) {
		JSONValue[] params = new JSONValue[] {  new JSONString(oldName),  new JSONString(newName) };
		send(METHOD_RENAME_ENTRY, params);
	};

	private static final String METHOD_ADD_ENTRY = "addEntry";
	private static final String METHOD_REMOVE_ENTRY = "removeEntry";
	private static final String METHOD_RENAME_ENTRY = "renameEntry";	
}