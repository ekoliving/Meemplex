/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.nursery.startup;

import org.openmaji.spi.MajiSPI;

public interface SystemStartup
{
  public static final String CREATE_MEEMSPACE_FLAG = "CREATE_MEEMSPACE_FLAG";

  /* ---------- Nested class for SPI ----------------------------------------- */

  public class spi
  {
    public static SystemStartup create()
    {
      return ( (SystemStartup) MajiSPI.provider().create(SystemStartup.class) );
    }

    public static String getIdentifier()
    {
      return ( "systemStartup" );
    };
  }
}
