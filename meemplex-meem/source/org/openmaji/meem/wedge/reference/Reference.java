/*
 * @(#)Reference.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.wedge.reference;

import java.io.Serializable;

import org.openmaji.meem.Facet;
import org.openmaji.meem.filter.Filter;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * Low-level mechanism for connecting <code>Meem</code>s. 
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface Reference extends Serializable {

  /**
   * Provide the identifier of the Provider Meem's Facet to depend upon.
   *
   * @return Identifier of the Provider Meem's Facet to depend upon
   */

  public String getFacetIdentifier();
  
  /**
   * Provide the Filter used to limit information flow.
   *
   * @return Filter used to limit information flow.
   */

  public Filter getFilter();

  /**
   * <p>
   * Indicates that the Meem Provider must send it's current content.
   * </p>
   * @return boolean Meem Provider must send it's current content
   */

  public boolean isContentRequired();
  
  /**
   * Provide the target of the specified Facet.
   *
   * @return Target of the specified Facet
   */

  public <T extends Facet> T getTarget();
  
  /**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		
		public static Reference create(String facetIdentifier, Facet target, boolean contentRequired) {
			return ((Reference) MajiSPI.provider().create(Reference.class, new Object[] { facetIdentifier, target, new Boolean(contentRequired), null }));
		}
		
		public static Reference create(String facetIdentifier, Facet target, boolean contentRequired, Filter filter) {
			return ((Reference) MajiSPI.provider().create(Reference.class, new Object[] { facetIdentifier, target, new Boolean(contentRequired), filter }));
		}
	}
}
