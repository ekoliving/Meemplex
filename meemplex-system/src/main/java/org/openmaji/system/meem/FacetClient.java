/*
 * @(#)FacetClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem;

import org.openmaji.meem.Facet;


/**
 * <p>
 * Client facet that allows a meem to broadcast what facets it has.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 * @see org.openmaji.meem.Meem
 */

public interface FacetClient extends Facet {

	/**
	 * Facets added to the Meem
	 * 
	 * @param facetItems
	 */
	void facetsAdded(FacetItem[] facetItems);

	/**
	 * Facets removed from the Meem
	 * 
	 * @param facetItems
	 */
	void facetsRemoved(FacetItem[] facetItems);
	
	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static String getIdentifier() {
      return("facetClient");
    };
  }
}