/*
 * @(#)Common.java
 *
 * Copyright 2003-2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Turn Common into a Meem.
 *
 * - Common stuff is always ugly.  Consider a better way to do this !
 *
 * - Provide a Common implementation that can be asynchronously configured.
 *   In other words, makes use of the fact that it should be a Meem.
 */

package org.openmaji.implementation.server;

import org.openmaji.implementation.server.utility.TraceFlagUtility;

/**
 * <p>
 * Common exists as a single location for widely used constants and
 * attributes, such as logging level.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-02-24)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public final class Common {

  /**
   * Maji platform default identification for log messages, etc
   *
   * Maji 1.10 Alpha 1 (Aho)
   * http://en.wikipedia.org/wiki/Alfred_Aho
   * 
   * The engineering naming theme for the Maji 1.6+ series is ...
   * ... names of famous computer scientists !
   * 
   * http://en.wikipedia.org/wiki/List_of_computer_scientists
   */

  private static final String IDENTIFICATION = "[Meemplex 2.00 Alpha 1 (Yo)]";

  /**
   * Property for specifying the root Majitek directory
   */

  public final static String PROPERTY_MAJI_HOME = "org.openmaji.directory";

  /**
   * Whether trace logging calls should be made.
   * This field should be checked before every LogTools.trace() call.
   */

  public static boolean TRACE_ENABLED    = false;

  public static boolean TRACE_AUTOMATION = false;
  public static boolean TRACE_AUTOMATION_CBUS = false;
  public static boolean TRACE_AUTOMATION_LONTALK = false;
  public static boolean TRACE_EXAMPLE = false;
  public static boolean TRACE_AUDIOVISUAL = false;
  public static boolean TRACE_MWT         = false;

  // system wedge/meem logging settings
  public static boolean TRACE_CATEGORY = false;
  public static boolean TRACE_DEPENDENCY	= false;
  public static boolean TRACE_SUBSYSTEM = false;
  public static boolean TRACE_JINI_SERVICES	= false;
  public static boolean TRACE_LEASING	= false;
  public static boolean TRACE_LICENSING	= false;
  public static boolean TRACE_LIFECYCLE	= false;
  public static boolean TRACE_LIFECYCLEMANAGER	= false;
  public static boolean TRACE_MEEMKIT	= false;
  public static boolean TRACE_MEEMPATHRESOLVER	= false;
  public static boolean TRACE_MEEMREGISTRY	= false;
  public static boolean TRACE_MEEMSERVER	= false;
  public static boolean TRACE_MEEMSTORE	= false;
  public static boolean TRACE_MEEMSYSTEM	= false;
  public static boolean TRACE_TELNET_SERVER = false;
  public static boolean TRACE_WEB_SERVICES = false;
  public static boolean TRACE_WEDGE_INTROSPECTOR = false;
  public static boolean TRACE_WEDGE_LIBRARY = false;
  
  static
  {
    TraceFlagUtility.add("TRACE_ENABLED","General trace level logging");
    TraceFlagUtility.add("TRACE_AUTOMATION","General trace level logging in all automation Meems");
    TraceFlagUtility.add("TRACE_AUTOMATION_CBUS","Trace level logging in C-Bus Meems");
    TraceFlagUtility.add("TRACE_AUTOMATION_LONTALK","Trace level logging in Lontalk Meems");
    TraceFlagUtility.add("TRACE_EXAMPLE","Trace level logging for the Meems in the example Meemkit");
    TraceFlagUtility.add("TRACE_AUDIOVISUAL","Trace level logging in audio-visual Meems");
    TraceFlagUtility.add("TRACE_MWT","Trace level logging in Meem Windowing Toolkit Meems");
  }

  /**
   * Maji platform default logging level for log messages
   */

  private static final int DEFAULT_LOG_LEVEL = 8;

  /**
   * Maji platform verbose logging level for log messages
   */

  private static final int VERBOSE_LOG_LEVEL = 64;

  /**
   * Per MeemServer top level category containing system Meem information.
   */

  private static String meemServerCategoryPath = null;

  /**
   * Provide the current Maji platform identification.
   *
   * @return Maji platform identification
   */

  public static String getIdentification() {
    return(IDENTIFICATION);
  }

  /**
   * Provide the current Maji platform logging level.
   *
   * @return Maji platform current logging level
   */

  public static int getLogLevel() {
    return(DEFAULT_LOG_LEVEL);
  }

  /**
   * Provide the verbose Maji platform logging level.
   *
   * @return Maji platform verbose logging level
   */

  public static int getLogLevelVerbose() {
    return(VERBOSE_LOG_LEVEL);
  }

  /**
   * Sets the per MeemServer top level category.
   *
   * @param newMeemServerCategoryPath New category path
   */

  public static void setMeemServerCategoryPath(
    String newMeemServerCategoryPath) {

    if (meemServerCategoryPath == null) {
      meemServerCategoryPath = newMeemServerCategoryPath;
    }
  }

  /**
   * Provide the MeemServer category path
   *
   * @return MeemServer category path
   */

  public static String getMeemServerCategoryPath() {
    return(meemServerCategoryPath);
  }
}
