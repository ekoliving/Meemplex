/*
 * Created on 26/08/2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.meemplex.internet.gwt.shared;

/**
 * 
 * Sends and receives Facet events
 * 
 * @author Warren Bloomer
 *
 */
public interface FacetHealthSender {

	void addFacetHealthListener(FacetHealthListener listener);
	
	void removeFacetHealthListener(FacetHealthListener listener);
}
