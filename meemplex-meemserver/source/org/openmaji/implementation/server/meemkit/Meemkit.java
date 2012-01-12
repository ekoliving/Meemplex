/*
 * @(#)Meemkit.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meemkit;

import java.net.URL;

import org.openmaji.meem.Facet;


/**
 * @author mg
 */
public interface Meemkit extends Facet {

  public void detailsChanged(String[] name, URL[] descriptorLocation);
	
  /* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static String getIdentifier() {
      return "meemkit";
    }
  }
}
