/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.network;

/**
 * Contains static fields used for debugging the CBUS Meemkit. The various
 * Wedges in this Meemkit use the static fields of this class as flags for
 * trace level logging. These can be enabled during runtime using the beanshell
 * environment.
 *
 * @author Ben Stringer
 */

public class DebugFlag
{
  /**
   * Turns on trace level logging
   */
  public static boolean TRACE = false;
}
