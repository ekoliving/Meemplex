/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.controller;

import org.openmaji.automation.address.Address;
import org.openmaji.automation.device.DeviceState;
import org.openmaji.meem.Facet;


/**
 * <p>
 * The DeviceControllerClient facet is implented by Wedges that require notification
 * of any state changes that may occur for a device.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Christos Kakris
 */

public interface DeviceControllerClient extends Facet
{
  /**
   * Notification of a change in state of the Device.
   * 
   * @param address         The address of the Device
   * @param deviceState     The new state of the Device
   */

	public void deviceStateChanged(Address address, DeviceState deviceState);
}

