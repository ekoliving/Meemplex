/*
 * Created on 18/08/2004
 *
 */
package org.openmaji.implementation.rpc.binding.facet;

import java.util.List;
import java.util.Vector;

import org.openmaji.implementation.server.utility.ObjectUtility;
import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.Direction;
import org.openmaji.rpc.binding.InboundBinding;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetItem;


/**
 * @author Warren Bloomer
 *
 */
public class InboundFacetClient extends InboundBinding {

	public InboundFacetClient() {
		setFacetClass(FacetClient.class);
	}

	/**
	 * Add a boolean facet to send values to.
	 * 
	 * @param listener
	 */
	public void addFacetClientFacet(FacetClient listener) {
		addListener(listener);
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void removeFacetClientFacet(FacetClient listener) {
		removeListener(listener);
	}
	
	/**
	 * 
	 */
	protected void invoke(String method, Object[] params) {
		
		if ("hasA".equals(method) ) {
			try {
				String facetId = (String)params[0];
				Class<? extends Facet> specification = ObjectUtility.getClass(Facet.class, (String)params[1]);
					//(Class<? extends Facet>)Class.forName((String)params[1]);
				Direction direction = new Direction((String)params[2]);
				
//				((FacetClient)proxy).hasA(facetId , specification, direction );
			}
			catch (ClassNotFoundException ex) {
				System.err.println("Class not found: " + params[1]);
			}
			catch (ClassCastException ex) {
				System.err.println("Class cast exception: " + ex);
			}
		}
		else if ("facetsAdded".equals(method)) {
			List<?> list = (List<?>) params[0];
			FacetItem[] facetItems = new FacetItem[list.size()];
			for (int i=0; i<list.size(); i++) {
				List<?> facetList = (List<?>) list.get(i);
				String facetName = (String)facetList.get(0);
				String facetClass = (String)facetList.get(1);
				Direction direction = new Direction((String)facetList.get(2));
				facetItems[i] = new FacetItem(facetName, facetClass, direction);
			}
			((FacetClient)proxy).facetsAdded(facetItems);
		}
		else if ("facetsRemoved".equals(method)) {
			List<?> list = (List<?>) params[0];
			FacetItem[] facetItems = new FacetItem[list.size()];
			for (int i=0; i<list.size(); i++) {
				List<?> facetList = (List<?>) list.get(i);
				String facetName = (String)facetList.get(0);
				String facetClass = (String)facetList.get(1);
				Direction direction = new Direction((String)facetList.get(2));
				facetItems[i] = new FacetItem(facetName, facetClass, direction);
			}
			((FacetClient)proxy).facetsRemoved(facetItems);
		}
	}
	
}
