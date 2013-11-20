/*
 * Created on 7/09/2005
 */
package org.openmaji.implementation.server.meem.wedge.dependency;

import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.LifeTime;


/**
 * @author Warren Bloomer
 *
 */
public interface FacetConnectable extends Connectable {
	
	/**
	 * Return the target Meem that is to be connected to.
	 * 
	 * @return The target meem.
	 */
	Meem getMeem();
	
	/**
	 * Whether the remote facet is a system facet.
	 * 
	 * @return Whether this is for a system facet.
	 */
	boolean isSystemFacet();

	/**
	 * Returns the local facet that is dependent on the remote facet.
	 * 
	 * @return The id of the local facet.
	 */
	String getLocalFacetId();

	/**
	 * Returns the LifeTime of the dependency
	 * 
	 * @return The lifetime of the dependency, i.e. persistent or transient.
	 */
	LifeTime getLifeTime();
}
