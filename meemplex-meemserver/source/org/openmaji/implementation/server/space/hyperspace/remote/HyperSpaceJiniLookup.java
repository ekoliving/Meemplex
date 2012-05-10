/*
 * @(#)HyperSpaceJiniLookup.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.space.hyperspace.remote;

import org.openmaji.spi.MajiSPI;

public interface HyperSpaceJiniLookup {
  
/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static HyperSpaceJiniLookup create() {
      return(
        (HyperSpaceJiniLookup) MajiSPI.provider().create(
          HyperSpaceJiniLookup.class
        )
      );
    }

    public static String getIdentifier() {
      return("hyperSpaceJiniLookup");
    };
  }
}