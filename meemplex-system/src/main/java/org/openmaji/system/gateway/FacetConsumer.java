/*
 * Created on 27/05/2005
 */
package org.openmaji.system.gateway;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;


/**
 * An interface for providing a Facet target.
 * 
 * @author Warren Bloomer
 *
 */
public interface FacetConsumer<T extends Facet> {

	/**
	 * Provide a facet target.
	 * 
	 * @param meem the meem to which the facet belongs.
	 * @param facetId the identifier of the facet.
	 * @param facet the facet target implementation on which calls can be made.
	 */
	void facet(Meem meem, String facetId, T facet);
	
}
