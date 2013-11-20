/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import java.io.Serializable;

/**
 * <p>
 * A DeviceHealth indicates how responsive a device is to commands.
 * When processing a command some devices are able to reply with an
 * indication that the request failed. In these cases we can accurately
 * model the health of a device. In some cases however a device can not
 * provide any failure information. And there are other devices whose
 * state can not be determined at all.
 * </p>
 *
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 *
 * @author  Christos Kakris
 */

public interface DeviceHealth extends Serializable
{
  /**
   * The Health of the Device has not been determined.
   */

  public static final int UNKNOWN = 0;

  /**
   * The Device has failed to perform a command, usually resulting in an error.
   */

  public static final int FAILED = 1;
  
  /**
   * The Device has not responded to a command.
   */

  public static final int UNRESPONSIVE = 2;

  /**
   * The Device is operating normally.
   */

  public static final int USEFUL = 3;

  /**
   * The Health of the Device.
   * 
   * @return     The Health of the Device.
   */

  public int getHealth();

  /**
   * Whether or not the Device is capable of processing commands.
   * 
   * @return    Whether or not this Device is capable of processing commands.
   */

  public boolean isUseful();
}

