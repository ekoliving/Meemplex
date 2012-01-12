/*
 * @(#)HookTest1.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.server.meem.hook.test;

import org.openmaji.system.meem.hook.Hook;

/**
 * <p>
 * Very simple test to demonstration that Hooks function correctly.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface HookTest1 extends Hook {

/* ---------- Factory specification ---------------------------------------- */

  /**
   * Unique Factory identifier for this Maji platform concept
   */

  public final static String IDENTIFIER = "hookTest1";

  /**
   * Default Factory implementation class name
   */

  public final static String DEFAULT_IMPLEMENTATION =
    "org.openmaji.implementation.server.meem.hook.test.HookTest1Impl";

  /**
   * Property that can override the default Factory implementation class name
   */

  public final static String PROPERTY_IMPLEMENTATION_CLASSNAME =
    "org.openmaji.meem.aspect.hook.test.HookTest1ImplClassName";

  /**
   * Factory type used to group similar Maji platform concepts
   */

  public final static String FACTORY_TYPE = "hook";
}
