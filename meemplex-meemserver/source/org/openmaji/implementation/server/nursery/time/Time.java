/*
 * @(#)Time.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.nursery.time;

import org.openmaji.meem.Facet;

/**
 * <p>
 * The Time Facet ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.Facet
 */

public interface Time extends Facet {

/* ---------- Factory specification ---------------------------------------- */

  /**
   * Unique Factory identifier for this Maji platform concept
   */

  public final static String IDENTIFIER = "time";

  /**
   * Default Factory implementation class name
   */

  public final static String DEFAULT_IMPLEMENTATION =
    "org.openmaji.implementation.server.nursery.time.TimeWedge";

  /**
   * Property that can override the default Factory implementation class name
   */

  public final static String PROPERTY_IMPLEMENTATION_CLASSNAME =
    "org.openmaji.domain.test.TimeWedgeClassName";

  /**
   * Factory type used to group similar Maji platform concepts
   */

  public final static String FACTORY_TYPE = "meem";
}
