/*
 * @(#)JiniMeemRegistry.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

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
public interface JiniMeemRegistry extends Facet {
	
	public class spi {
    public static JiniMeemRegistry create() {
      return((JiniMeemRegistry) MajiSPI.provider().create(JiniMeemRegistry.class));
    }

    public static String getIdentifier() {
      return("jiniMeemRegistry");
    };
  }
  
}
