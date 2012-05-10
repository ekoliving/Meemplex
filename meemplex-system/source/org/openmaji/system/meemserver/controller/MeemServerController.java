/*
 * @(#)MeemServerController.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.system.meemserver.controller;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface MeemServerController extends Facet {

	/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static MeemServerController create() {
      return((MeemServerController) MajiSPI.provider().create(MeemServerController.class));
    }

    public static String getIdentifier() {
      return("meemServerController");
    };
  }
}
