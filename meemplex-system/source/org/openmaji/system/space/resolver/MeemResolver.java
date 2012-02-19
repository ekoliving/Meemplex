/*
 * @(#)MeemResolver.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.space.resolver;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * This <code>Facet</code> is used for its SPI only, to create meems that act as resolvers.
 * Resolvers are used entirely through their outbound MeemResolverClient facet.
 * </p>
 * @author  mg
 * @version 1.0
 * @see org.openmaji.system.space.resolver.MeemResolverClient
 */

public interface MeemResolver extends Facet {

  /**
   * Nested class for service provider.
   * 
   * @see org.openmaji.spi.MajiSPI
   */
  public class spi {
    public static MeemResolver create() {
      return((MeemResolver) MajiSPI.provider().create(MeemResolver.class));
    }

    public static String getIdentifier() {
      return("meemResolver");
    };
  }
}