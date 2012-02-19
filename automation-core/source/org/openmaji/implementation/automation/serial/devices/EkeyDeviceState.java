/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.serial.devices;

import org.openmaji.automation.device.SimpleDeviceState;

public class EkeyDeviceState extends SimpleDeviceState
{
	private static final long serialVersionUID = 6424227717462161145L;

  private boolean scan;
  private int id;

  public EkeyDeviceState(boolean on)
  {
    super(on);
  }

  public void setSuccessfulScan(boolean scan)
  {
    this.scan = scan;
  }

  public boolean successfulScan()
  {
    return scan;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }
}
