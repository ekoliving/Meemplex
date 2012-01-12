/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.emulator;

/**
 * Used when writing a protocol emulator this Interface defines the interactions
 * that the emulator can have with simulated devices. Designed to work with
 * telnet-like protocols where the commands are usually simple commands and
 * queries.
 *
 * @author Chris Kakris
 */

public interface SimulatedDevice
{
  /**
   * Returns the current status of the simulated device. 
   * 
   * @return The status of the device
   */
  public String getStatus();

  /**
   * Perform the default action on this simulated device, for example, toggling
   * a light switch.
   * 
   * @return The result of the default action
   */
  public String defaultAction();

  /**
   * Perform a device specific operation.
   * 
   * @param value The operation to be performed
   * @return  The result of the operation
   */
  public String process(String value);
}
