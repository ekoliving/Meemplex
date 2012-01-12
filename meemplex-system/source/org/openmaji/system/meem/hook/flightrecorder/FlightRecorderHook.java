/*
 * @(#)FlightRecorderHook.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.hook.flightrecorder;

import org.openmaji.system.meem.hook.Hook;

/**
 * <p>
 * FlightRecorderHook records Meem Facet invocations as they occur.
 * This record is intended to assist in problem diagnosis.
 * </p>
 * <p>
 * This hook does not suspend processing of the InvocationList and is optional,
 * if you want to make use of it PROPERTY_ENABLE needs to be set to "inbound"
 * in your maji properties file.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.system.meem.hook.Hook
 */
public interface FlightRecorderHook extends Hook {

  /**
   * Property for specifying the FlightRecorder level-of-detail
   * @deprecated no longer used as this hook is now configurable via InterMajik
   */
  public final static String PROPERTY_DETAIL =
    "org.openmaji.meem.aspect.hook.flightrecorder.flightRecorderDetail";

  /**
   * Property for enabling the FlightRecorder for either inbound and/or
   * outbound invocation processing
   */
  public final static String PROPERTY_ENABLE =
    "org.openmaji.meem.aspect.hook.flightrecorder.flightRecorderEnable";

  /**
   * Property value for enabling the FlightRecorder for inbound invocations
   */
  public final static String ENABLE_INBOUND = "inbound";

  /**
   * Property value for enabling the FlightRecorder for invocations both ways
   */
  public final static String ENABLE_INOUTBOUND = "inoutbound";

  /**
   * Property value for enabling the FlightRecorder for outbound invocations
   */
  public final static String ENABLE_OUTBOUND = "outbound";

  /**
   * Service provider access point.
   */
  public class spi {
    public static String getIdentifier() {
      return("flightRecorderHook");
    };
  }
}