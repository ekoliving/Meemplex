/*
 * Created on 26/08/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.openmaji.rpc.binding;

/**
 * 
 * Sends and receives Facet events
 * 
 * @author Warren Bloomer
 *
 */
public interface FacetEventSender {

	void addFacetEventListener(FacetEventListener listener);
	
	void removeFacetEventListener(FacetEventListener listener);
}
