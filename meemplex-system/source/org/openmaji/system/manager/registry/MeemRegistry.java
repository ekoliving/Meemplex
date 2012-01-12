/*
 * @(#)MeemRegistry.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.registry;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * Basic facet interface for doing registratrion with a meem registry.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public interface MeemRegistry extends Facet {

  /**
   * <p>
   * Register a meem with the registry associated with this facet.
   * </p>
   * @param meem Meem Reference to be registered.
   */
  public void registerMeem(
    Meem meem);

  /**
   * <p>
   * Deregister a meem with the registry associated with this facet.
   * </p>
   * @param meem Meem Reference to be deregistered.
   */
  public void deregisterMeem(
    Meem meem);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
    public class spi {
      public static MeemRegistry create() {
        return((MeemRegistry) MajiSPI.provider().create(MeemRegistry.class));
      }

      public static String getIdentifier() {
        return("meemRegistry");
      };
    }
}