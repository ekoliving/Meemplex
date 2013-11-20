/*
 * @(#)HookTest2.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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

public interface HookTest2 extends Hook {

/* ---------- Factory specification ---------------------------------------- */

  /**
   * Unique Factory identifier for this Maji platform concept
   */

  public final static String IDENTIFIER = "hookTest2";

  /**
   * Default Factory implementation class name
   */

  public final static String DEFAULT_IMPLEMENTATION =
    "org.openmaji.implementation.server.meem.hook.test.HookTest2Impl";

  /**
   * Property that can override the default Factory implementation class name
   */

  public final static String PROPERTY_IMPLEMENTATION_CLASSNAME =
    "org.openmaji.meem.aspect.hook.test.HookTest2ImplClassName";

  /**
   * Factory type used to group similar Maji platform concepts
   */

  public final static String FACTORY_TYPE = "hook";
}
