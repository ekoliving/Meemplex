package org.openmaji.implementation.rpc.binding.facet;

import java.net.URISyntaxException;


import org.openmaji.implementation.rpc.binding.util.MeemHelper;
import org.openmaji.meem.Meem;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.system.space.Category;
import org.openmaji.system.space.CategoryClient;

/**
 * @author Warren Bloomer
 *
 */
public class InboundCategory extends InboundBinding {

	public InboundCategory() {
		setFacetClass(CategoryClient.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addCategoryFacet(Category listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeCategoryFacet(Category listener) {
		removeListener(listener);
	}
	
	/**
	 * 
	 */
	protected void invoke(String method, Object[] params) {
		
		if ("addEntry".equals(method) ) {
			try {
				String name  = (String) params[0];
				Meem   meem  = MeemHelper.getMeem((String) params[1]);
				((Category)proxy).addEntry( name, meem );
			}
			catch (URISyntaxException ex) {
				System.err.println("Exception in URI: " + ex);
			}
		}
		else if ("removeEntry".equals(method) ) {
			String name  = (String) params[0];
			((Category)proxy).removeEntry( name );
		}
		else if ("renameEntry".equals(method) ) {
			String oldName  = (String) params[0];
			String newName  = (String) params[1];
			((Category)proxy).renameEntry( oldName, newName );
		}
	}
}
