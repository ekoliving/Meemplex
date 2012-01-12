/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import java.io.Serializable;

import org.openmaji.common.Position;
import org.openmaji.common.Value;

/**
 * <p>
 * DeviceState represents the state of a device. For example a Binary
 * hardware device, which is operating normally, will be either "on"
 * or "off" so its state can be represented as a Binary.
 * The state of a Linear hardware device can be represented with a Position.
 * And there are some devices whose state is represented by something else,
 * for example a String or a bitmap. DeviceState provides methods
 * to allow the extraction of a device's state in the most common formats
 * that the Maji framework knows about.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public interface DeviceState extends Serializable
{
  /**
   * <p>
   * Return a simple yes/no, on/off or true/false to represent the state of
   * the Device. If the underlying state of the device is not a Binary then this
   * method will return a boolean aproximation that makes sense.
   * </p>
   * 
   * <p>
   * For example, if the state of the device is represented as a Position then this
   * method will return whether or not the Position is in the minimum limit of
   * the allowed range.
   * </p>
   * 
   * @return    The state of the Device as a boolean.
   */

  public boolean getBinaryState();

  /**
   * <p>
   * Return the state of the Device as a Position within a range. If the underlying state
   * of the Device is not a Position then this
   * method will return a Position that aproximates the state in a sensible manner.
   * </p>
   * 
   * <p>
   * For example, if the state of the device is represented as a boolean then this
   * method will return an IntegerPosition with a range of 0 - 1 and value of 0=false
   * or 1=true.
   * </p>
   * 
   * @return    The state of the Device as a Position
   */

  public Position getLinearState();

  /**
   * <p>
   * Return the state of the Device as a Value. If the underlying state
   * of the Device is not a Value then this
   * method will return a Value that aproximates the state in a sensible manner.
   * </p>
   * 
   * <p>
   * For example, if the state of the device is represented as a text String then this
   * method will return a StringValue. If the state is represented as a Position or boolean then this will
   * return a NumberValue.
   * </p>
   * 
   * @return    The state of the Device as a Value
   */

  public Value getVariableState();
}


