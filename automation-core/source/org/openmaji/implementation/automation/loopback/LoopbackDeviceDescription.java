/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.automation.device.BaseDeviceDescription;
import org.openmaji.automation.device.DeviceType;
import org.openmaji.automation.protocol.Protocol;


public class LoopbackDeviceDescription extends BaseDeviceDescription
{
	private static final long serialVersionUID = 6424227717462161145L;

  private static final Protocol protocol = new LoopbackProtocol();
 
  public LoopbackDeviceDescription(DeviceType deviceType)
  {
    super(deviceType,protocol);
  }
}
