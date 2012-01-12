/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import org.openmaji.implementation.server.meemkit.Meemkit;
import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;



public interface MeemkitLifeCycleManager extends Facet, Meemkit
{
  /* ---------- Nested class for SPI ----------------------------------------- */

  public class spi
  {
    public static MeemkitLifeCycleManager create()
    {
      return ((MeemkitLifeCycleManager) MajiSPI.provider().create(MeemkitLifeCycleManager.class));
    }

    public static String getIdentifier()
    {
      return "meemkitLifeCycleManager";
    }
  }

}
