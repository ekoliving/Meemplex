/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.openmaji.rpc.binding.OutboundBinding;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetItem;


/**
 * An outbound Boolean Facet
 * Connect to an inbound Facet on a Meem Server
 * 
 * @author Warren Bloomer
 *
 */
public class OutboundFacetClient extends OutboundBinding implements FacetClient {

	public static final String KEY_NAME = "name";
	public static final String KEY_MEEM = "meem";
	
	/**
	 *
	 */
	public OutboundFacetClient() {
		setFacetClass(FacetClient.class);
	}
	
//	public void hasA(String facetId, Class specification, Direction direction) {
//		send(
//				"hasA", 
//				new Serializable[] { 
//						facetId,
//						specification.getName(),
//						direction.getIdentifier(),
//					}
//			);
//	}
	
	public void facetsAdded(FacetItem[] facetItems) {
		List<List<String>> facetList = new ArrayList<List<String>>();
		for (FacetItem item : facetItems) {
			List<String> facet = new ArrayList<String>();
			facet.add(item.identifier);
			facet.add(item.interfaceName);
			facet.add(item.direction.getIdentifier());
			facetList.add(facet);
		}
		
		send("facetsAdded", new Object[] { facetList });
	}
	
	public void facetsRemoved(FacetItem[] facetItems) {
		List<List<String>> facetList = new ArrayList<List<String>>();
		for (FacetItem item : facetItems) {
			List<String> facet = new ArrayList<String>();
			facet.add(item.identifier);
			facet.add(item.interfaceName);
			facet.add(item.direction.getIdentifier());
			facetList.add(facet);
		}
		send("facetsRemoved", new Object[] { facetList });
	}
	
}
