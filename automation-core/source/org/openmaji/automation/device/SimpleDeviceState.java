/*
 * Copyright 2006 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import org.openmaji.common.IntegerPosition;
import org.openmaji.common.NumberValue;
import org.openmaji.common.Position;
import org.openmaji.common.Value;

public class SimpleDeviceState implements DeviceState
{
	private static final long serialVersionUID = 6424227717462161145L;

  private boolean on = false;

  public SimpleDeviceState(boolean on)
  {
    this.on = on;
  }

  public boolean getBinaryState()
  {
    return on;
  }

  public Position getLinearState()
  {
    return new IntegerPosition(on ? 1 : 0,1,0,1);
  }

  public Value getVariableState()
  {
    return new NumberValue(new Integer(on ? 1 : 0));
  }
}
