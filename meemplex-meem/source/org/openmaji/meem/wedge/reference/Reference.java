/*
 * @(#)Reference.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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

public interface Reference<T extends Facet> extends Serializable {

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

  public T getTarget();
  
  /**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		
		@SuppressWarnings("unchecked")
		public static <T extends Facet> Reference<T> create(String facetIdentifier, T target, boolean contentRequired) {
			return ((Reference<T>) MajiSPI.provider().create(Reference.class, new Object[] { facetIdentifier, target, new Boolean(contentRequired), null }));
		}
		
		@SuppressWarnings("unchecked")
		public static <T extends Facet> Reference<T> create(String facetIdentifier, T target, boolean contentRequired, Filter filter) {
			return ((Reference<T>) MajiSPI.provider().create(Reference.class, new Object[] { facetIdentifier, target, new Boolean(contentRequired), filter }));
		}
	}
}
