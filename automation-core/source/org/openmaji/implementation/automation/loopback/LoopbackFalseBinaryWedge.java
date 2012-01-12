/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.meem.Wedge;
import org.openmaji.common.Binary;

/**
 * The LoopbackFalseBinaryWedge will always produce an 'false' value
 * on the binaryStateConduit no matter what value it receives on the
 * binaryControlConduit.
 */

public class LoopbackFalseBinaryWedge implements Wedge
{
  public Binary binaryControlConduit = new BinaryControlConduit();
  public Binary binaryStateConduit = null;
  
  /* ---------- BinaryControlConduit ---------------------------------------- */

  class BinaryControlConduit implements Binary
  {
    public synchronized void valueChanged(boolean value)
    {
      binaryStateConduit.valueChanged(false);
    }
  }
}
