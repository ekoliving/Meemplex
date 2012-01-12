package org.meemplex.internet.gwt.client.widget;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meemplex.internet.gwt.client.InboundBinding;
import org.meemplex.internet.gwt.client.binding.CategoryBinding;
import org.meemplex.internet.gwt.client.facets.Category;
import org.meemplex.internet.gwt.shared.BindingFactory;
import org.meemplex.internet.gwt.shared.FacetClasses;
import org.meemplex.internet.gwt.shared.FacetEvent;
import org.meemplex.internet.gwt.shared.FacetEventListener;
import org.meemplex.internet.gwt.shared.FacetReference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * TODO listen on LifeCycle to grey-out when not ready.
 * TODO remove this item when meem is deleted.
 * 
 * @author stormboy
 *
 */
public class CategoryTreeItem extends TreeItem {

	private static final boolean DEBUG = true;
	
	private BindingFactory bindingFactory;
	
	/**
	 * Path of the meem this tree item represents
	 */
	private String meemPath;
	
	/**
	 * Binding to receive Facets from the Meem.
	 */
	private InboundBinding facetBinding;

	/**
	 * If meem is a Category, this is the Binding to send and receive Category messages.
	 */
	private CategoryBinding categoryBinding;

	/**
	 * A Map of child tree items
	 */
	private HashMap<String, CategoryTreeItem> items = new HashMap<String, CategoryTreeItem>();

	private boolean doLoad = false;
	private boolean loading = false;
	
	/**
	 * hyperSpace:/
	 * @param bindingFactory
	 * @param hyperspacePath
	 */
	public CategoryTreeItem(BindingFactory bindingFactory, String meemPath) {
		this.bindingFactory = bindingFactory;
		this.meemPath = meemPath;
		
		FacetReference facetReference = new FacetReference(meemPath, "facetClientFacet", FacetClasses.FACET_CLIENT);
		facetBinding = bindingFactory.createInboundBinding(facetReference);
		facetBinding.addListener(facetEventListener);
		
    }
	
	public String getMeemPath() {
		return meemPath;
	}

	public boolean isCategory() {
		return getStyleName().contains("category");
	}
	
	/**
	 * 
	 */
	public void load() {
		doLoad = true;
		if (isCategory() && !loading) {
			loading = true;
			this.categoryBinding = new CategoryBinding(bindingFactory, meemPath);
			this.categoryBinding.getInboundBinding().addListener(categoryClientEventListener);
		}
	}
	
	public void unload() {
		// remove children
		if (categoryBinding != null) {
			categoryBinding.getInboundBinding().removeListener(categoryClientEventListener);
			categoryBinding.release();
			categoryBinding = null;
		}
		for (int i=0; i<getChildCount(); i++) {
			CategoryTreeItem item = (CategoryTreeItem) getChild(i);
			item.unload();
		}
		removeItems();
		doLoad = false;
		loading = false;
	}
	
	private void meemPath(String meemPath) {
		if (meemPath != null && !meemPath.equals(this.meemPath)) {
			unload();
			this.meemPath = meemPath;
			load();
		}
	}
	
	private void initCategory() {
		this.addStyleName("category");
		if (doLoad) {
			load();
		}
	}

	private Category.Entry[] getAsArray(List<Map<String, String>> entryList) {
		Category.Entry[] entries = new Category.Entry[entryList.size()];		
		int i=0;
		for (Map<String,String> entryMap : entryList) {
			String name = entryMap.get(KEY_NAME);
			String meemId = entryMap.get(KEY_MEEM);
			entries[i++] = new Category.Entry(name, meemId);
		}
		
		return entries;
	}

	private Category.Entry[] getAsArray(JSONArray entryList) {
		Category.Entry[] entries = new Category.Entry[entryList.size()];		
		for (int i=0; i<entryList.size(); i++) {
			//Map<String,String> entryMap : entryList) {
			JSONObject entryMap = entryList.get(i).isObject();
			String name = entryMap.get(KEY_NAME).isString().stringValue();
			String meemId = entryMap.get(KEY_MEEM).isString().stringValue();
			entries[i] = new Category.Entry(name, meemId);
		}
		
		return entries;
	}
	
	private CategoryTreeItem createTreeItem(Category.Entry entry) {
		CategoryTreeItem item = new CategoryTreeItem(bindingFactory, entry.getMeemId());
		item.setText(entry.getName());
		return item;
	}
	
	private void addEntryItems(Category.Entry[] entries) {
		for (Category.Entry entry : entries) {
			CategoryTreeItem item = items.get(entry.getName());
			if (item == null) {
				// new item
				item = createTreeItem(entry);
				addItem(item);
				items.put(entry.getName(), item);
			}
			else {
				item.meemPath(entry.getMeemId());
			}
		}
	}
	
	private void removeEntryItems(Category.Entry[] entries) {
		for (Category.Entry entry : entries) {
			CategoryTreeItem item = items.get(entry.getName());
			removeItem(item);
			items.remove(entry.getName());
		}
	}
	
	private void renameEntryItem(String oldName, String newName) {
		CategoryTreeItem item = items.remove(oldName);
		if (item != null) {
			CategoryTreeItem oldItem = items.remove(newName);
			if (oldItem != null) {
				removeItem(oldItem);
			}
			
			item.setText(newName);
			items.put(newName, item);
		}
	}

	@Override
	protected void finalize() throws Throwable {
	    super.finalize();
	    if (facetBinding != null) {
	    	// TODO release
	    	bindingFactory.releaseBinding(facetBinding.getFacetReference());
	    }
	    if (categoryBinding != null) {
	    	categoryBinding.release();
	    }
	}
	
	private FacetEventListener facetEventListener = new FacetEventListener() {
		public void facetEvent(FacetEvent event) {
			if (DEBUG) {
				GWT.log("CategoryTreeItem: " + meemPath + " got event " + event);
			}
			 if ("facetsAdded".equals(event.getMethod())) {
				JSONArray list = JSONParser.parseLenient(event.getParams()[0]).isArray();

				boolean hasCategoryFacet = false;
				for (int i=0; i<list.size(); i++) {
					JSONArray facetList = list.get(i).isArray();
					String facetName = facetList.get(0).isString().stringValue();
					String facetClass = facetList.get(1).isString().stringValue();
					//String direction = ((String)facetList.get(2));
					if (FacetClasses.CATEGORY_CLIENT.equals(facetClass) && "categoryClient".equals(facetName)) {
						hasCategoryFacet = true;
					}
				}
				if (hasCategoryFacet) {
					initCategory();
				}
			}
			else if ("facetsRemoved".equals(event.getMethod())) {
				// TODO if the categoryClient facet is removed, set this widget to not be a category 
//				List<?> list = (List<?>) event.getParams()[0];
//				for (int i=0; i<list.size(); i++) {
//					List<?> facetList = (List<?>) list.get(i);
//					String facetName = (String)facetList.get(0);
//					String facetClass = (String)facetList.get(1);
//					String direction = (String)facetList.get(2);
//					removeFacet(facetName);
//				}
			}
		}
	};
	
	/**
	 * 
	 */
	private FacetEventListener categoryClientEventListener = new FacetEventListener() {
		public void facetEvent(FacetEvent event) {
			String method = event.getMethod();
			String[] params = event.getParams();
			
			if (DEBUG) {
				GWT.log("CategoryTreeItem: " + meemPath + " got event " + event);
			}
			
			if ("entriesAdded".equals(method) ) {
				JSONArray entryList = JSONParser.parseLenient(params[0]).isArray();
				//List<Map<String, String>> entryList = (List<Map<String, String>>) params[0];
				Category.Entry[] entries = getAsArray(entryList);
				addEntryItems(entries);
			}
			else if ("entriesRemoved".equals(method) ) {
				JSONArray entryList = JSONParser.parseLenient(params[0]).isArray();
				//List<Map<String, String>> entryList = (List<Map<String, String>>)params[0];
				Category.Entry[] entries = getAsArray(entryList);
				removeEntryItems(entries);
			}
			else if ("entryRenamed".equals(method) ) {
				String oldName  = (String) params[0];
				//String oldMeem = (String) params[1];
				String newName  = (String) params[2];
				//String newMeem  = (String) params[3];
				renameEntryItem(oldName, newName);
			}

		}
	};
	
	private static final String KEY_NAME = "name";
	private static final String KEY_MEEM = "meem";
}
