/*
 * @(#)ManagedPersistenceHandler.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem.wedge.persistence;

import org.openmaji.meem.Facet;
import org.openmaji.meem.conduit.Persistence;
import org.openmaji.system.meem.definition.MeemContent;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */

public interface ManagedPersistenceHandler extends Persistence, Facet {

	public void restore(
    MeemContent meemContent);

/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static String getIdentifier() {
      return("managedPersistenceHandler");
    };
  }
}