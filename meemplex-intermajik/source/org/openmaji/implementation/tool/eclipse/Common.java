/*
 * @(#)Common.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse;

/**
 * @author mg
 */
public class Common {

	/**
   * Property for specifying the root Majitek directory
   */

  public final static String PROPERTY_MAJITEK_DIRECTORY = "org.openmaji.directory";
	
	/**
   * Whether trace logging calls should be made.
   * This field should be checked before every LogTools.trace() call.
   */
  public static boolean TRACE_ENABLED    = false;
	
  public static boolean TRACE_WEDGE_LIBRARY = false;
  
  /**
   * InterMajik default logging level for log messages
   */

  private static final int DEFAULT_LOG_LEVEL = 8;

  /**
   * InterMajik verbose logging level for log messages
   */

  private static final int VERBOSE_LOG_LEVEL = 64;
  
  /**
   * Provide the current InterMajik logging level.
   *
   * @return InterMajik current logging level
   */

  public static int getLogLevel() {
    return(DEFAULT_LOG_LEVEL);
  }

  /**
   * Provide the verbose InterMajik logging level.
   *
   * @return InterMajik verbose logging level
   */

  public static int getLogLevelVerbose() {
    return(VERBOSE_LOG_LEVEL);
  }
  
  /**
   * Provide the current InterMajik identification.
   * Use the identification string from the Maji server.
   *
   * @return InterMajik identification
   */
  
  public static String getIdentification() {
    return(org.openmaji.implementation.server.Common.getIdentification());
  }
  
}
