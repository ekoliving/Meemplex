/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

public class SimpleDeviceHealth implements DeviceHealth
{
	private static final long serialVersionUID = 6424227717462161145L;

  private final int health;

  public SimpleDeviceHealth(int health)
  {
    this.health = health;
  }

  public int getHealth()
  {
    return health;
  }

  public boolean isUseful()
  {
    return ( health == USEFUL );
  }
}
