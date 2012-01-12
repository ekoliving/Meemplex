/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;

import org.openmaji.meem.Meem;
import org.openmaji.rpc.binding.OutboundBinding;
import org.openmaji.system.space.Category;


/**
 * An outbound Boolean Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundCategory extends OutboundBinding implements Category {

	/**
	 *
	 */
	public OutboundCategory() {
		setFacetClass(Category.class);
	}
	
	/**
	 * Send a value to the remove Meem
	 */
	public void addEntry(String name, Meem meem) {
		send("addEntry", new Serializable[] { name, meem.getMeemPath().toString() });
	}
	
	public void removeEntry(String name) {
		send("removeEntry", new Serializable[] { name });		
	}
	
	public void renameEntry(String oldName, String newName) {
		send("renameEntry", new Serializable[] { oldName, newName });		
	}

}
