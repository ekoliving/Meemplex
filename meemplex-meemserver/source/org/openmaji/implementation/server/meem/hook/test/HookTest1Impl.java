/*
 * @(#)HookTest1Impl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.meem.hook.test;


import org.openmaji.server.meem.hook.test.HookTest1;
import org.openmaji.system.meem.hook.HookProcessor;
import org.openmaji.system.meem.hook.invoke.Invocation;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Very simple test to demonstration that Hooks function correctly.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class HookTest1Impl implements HookTest1 {

  public boolean process(Invocation invocation, HookProcessor hookProcessor) {

    logger.log(Level.INFO, "Invoked");

    return(true);
  }

/* ---------- Logging fields ----------------------------------------------- */

  /**
   * Create the per-class Software Zoo Logging V2 reference.
   */

  private static Logger logger = Logger.getAnonymousLogger();
}
