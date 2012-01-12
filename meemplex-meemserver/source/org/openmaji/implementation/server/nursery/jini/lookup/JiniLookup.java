/*
 * @(#)JiniLookup.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.jini.lookup;

import org.openmaji.system.meem.FacetItem;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface JiniLookup {
	
	/**
	 * This is called to start the lookup
	 * @param facetItem A FacetItem that is used to match the service
	 */
	public void startLookup(FacetItem facetItem, boolean returnLatestOnly);
	
	/**
	 * This is called to stop the lookup
	 */
	public void stopLookup();
	
}
