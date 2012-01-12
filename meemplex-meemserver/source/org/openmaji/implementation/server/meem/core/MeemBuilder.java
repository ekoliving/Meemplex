/*
 * @(#)MeemBuilder.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider renaming "addMeemDefinition()" to be "mergeMeemDefinition()".
 */

package org.openmaji.implementation.server.meem.core;

import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.spi.MajiSPI;
import org.openmaji.system.meem.core.MeemCore;


/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.MeemDefinition
 */

public interface MeemBuilder {

  public void addMeemDefinition(
    MeemDefinition meemDefinition);

  public void addMeemDefinition(
    MeemDefinition meemDefinition,
    Object         existingImplementation);

  public MeemCore getMeemCore();  // Provides a strong reference

/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static MeemBuilder create(MeemPath meemPath) {
      return((MeemBuilder) MajiSPI.provider().create(MeemBuilder.class, new Object[]{ meemPath }));
    }
  }
}
