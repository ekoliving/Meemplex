/*
 * @(#)Hook.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.meem.hook;

import org.openmaji.system.meem.hook.invoke.Invocation;


/**
 * <p>
 * A Hook allows transparent interception of either in-bound or out-bound
 * method invocations between the Facets of two Meems.
 * </p>
 * <p>
 * Regarding the naming of "Hook", a quote from the Fact Guru,
 * <pre>
 * http://www.site.uottawa.ca:4321/oose/index.html#hook ...
 *</pre>
 * "An aspect of the design deliberately added to allow other designers to
 *  add additional functionality. It does nothing in the basic version of
 *  the system, but is designed to be implemented or overridden when the
 *  system is extended or reused.  A hook is similar to a slot except that
 *  a hook represents functionality that it is optional for the developer
 *  to provide when they exploit the framework."
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface Hook {

  /**
   * <p>
   * Hook specific processing of the provided method invocation.
   * A call hookProcessor can be made to continue processing hooks.
   * </p>
   * @param invocation  invocation being processed
   * @param hookProcessor reference to a HookProcessor that may re-entered
   * @return Indicates whether Hook processing should continue
   * @exception Throwable
   */
  boolean process(Invocation invocation, HookProcessor hookProcessor) 
    throws Throwable;
}
