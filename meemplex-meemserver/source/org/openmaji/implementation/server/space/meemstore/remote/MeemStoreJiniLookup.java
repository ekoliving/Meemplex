/*
 * @(#)MeemStoreJiniLookup.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.space.meemstore.remote;

import org.openmaji.spi.MajiSPI;

public interface MeemStoreJiniLookup {

/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static MeemStoreJiniLookup create() {
      return(
        (MeemStoreJiniLookup) MajiSPI.provider().create(
      MeemStoreJiniLookup.class
        )
      );
    }

    public static String getIdentifier() {
      return("meemStoreJiniLookup");
    };
  }
}