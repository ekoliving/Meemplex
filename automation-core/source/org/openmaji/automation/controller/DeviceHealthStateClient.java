/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.controller;

import org.openmaji.automation.address.Address;
import org.openmaji.automation.device.DeviceHealth;
import org.openmaji.automation.device.DeviceState;
import org.openmaji.meem.Facet;


/**
 * <p>
 * The DeviceHealthStateClient facet is implented by Wedges that require notification
 * of any health or state changes that may occur for a device.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 */

public interface DeviceHealthStateClient extends Facet
{
  /**
   * Notification of a response received from a device upon processing a command.
   * Note that for some device networks there may be no responses to commands and
   * there may also not be enough information to determine the health or state of the device.
   * 
   * @param address       The address of the Device
   * @param deviceHealth  The health of the Device
   * @param deviceState   The state of the Device
   */

  public void update(Address address, DeviceHealth deviceHealth, DeviceState deviceState);
}

