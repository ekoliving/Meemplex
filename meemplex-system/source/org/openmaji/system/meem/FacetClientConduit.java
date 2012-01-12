/*
 * @(#)FacetClientConduit.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.Direction;


/**
 * service interface for FacetClient
 */
public interface FacetClientConduit
{
	/**
	 * Confirm whether or not the passed in meem has a facet fitting the description passed in.
	 * The confirmation is confirmed by using the callback.
	 * 
	 * @param meem the meem of interest.
	 * @param facetIdentifier the identifier of the facet of interest.
	 * @param specification the class the facet of interest implements.
	 * @param direction the direction of the facet.
	 * @param callback the callback to communicate the results of the query on.
	 */
	public void hasA(
			Meem meem, 
			String facetIdentifier, 
			Class<? extends Facet> specification, 
			Direction direction, 
			FacetClientCallback callback);
}
