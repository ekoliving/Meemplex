/*
 * @(#)MeemSpace.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.space.meemspace;

import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class MeemSpace {

  /**
   * Property for specifying the MeemSpace identifier
   */

  public static final String PROPERTY_MEEMSPACE_IDENTIFIER = "org.openmaji.meemSpaceIdentifier";

  private static String identifier = null;

  public static String getIdentifier() {
    if (identifier == null) {
      identifier = System.getProperty(PROPERTY_MEEMSPACE_IDENTIFIER);

      if (identifier == null) {
        LogTools.warn(
          logger,
          "MeemSpace Identifier not configured: " +
           PROPERTY_MEEMSPACE_IDENTIFIER
        );
      }
    }

    return(identifier);
  }

/* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static final Logger logger = LogFactory.getLogger();
}
