/*
 * Created on Mar 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.openmaji.system.meem.wedge.reference;

import org.openmaji.meem.Facet;
import org.openmaji.meem.wedge.reference.Reference;


/**
 * A callback class to be used in conjunction with the MeemClientConduit. Used to 
 * gain details about inbound facets on other meems.
 * 
 * @author Chris Kakris
 */
public interface MeemClientCallback extends Facet
{
	/**
	 * Returns a reference object containing the facet we want, or just null if there is no facet.
	 * 
	 * @param reference a reference containing the target - null if a suitable facet could not be found.
	 */
    void referenceProvided(Reference reference);
}
