/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import java.io.Serializable;

/**
 * Specifies the 'type' of the device. Devices come in one of a number of
 * basic types according to the fundamental operation of the underlying
 * hardware component. For example a device that can be turned on and off
 * is called a Binary device.
 *
 * @author Chris Kakris
 */
public class DeviceType implements Serializable
{
	private static final long serialVersionUID = 6424227717462161145L;

  private static final int UNARY_INDEX = 0;
  private static final int BINARY_INDEX = 1;
  private static final int LINEAR_INDEX = 2;
  private static final int MULTISTATE_INDEX = 3;
  private static final int VARIABLE_INDEX = 4;

  /**
   * Represents a device that does not maintain any status and is basically
   * just a trigger.
   */
  public static final DeviceType UNARY = new DeviceType(UNARY_INDEX);

  /**
   * Represents a dual state device that can be turned on or off.
   */
  public static final DeviceType BINARY = new DeviceType(BINARY_INDEX);

  /**
   * Represents a device that can take on a range of values, such as a
   * dimmer or a slider.
   */
  public static final DeviceType LINEAR = new DeviceType(LINEAR_INDEX);

  /**
   * Represents a device that can take one of a number of distinct states.
   */
  public static final DeviceType MULTISTATE = new DeviceType(MULTISTATE_INDEX);

  /**
   * Represents a sophisticated device that can accept arbitrary types. 
   */
  public static final DeviceType VARIABLE = new DeviceType(VARIABLE_INDEX);

  private final int type;

  /**
   * Constructs an instance of the specified type.
   * 
   * @param type The type of this device
   */
  public DeviceType(int type)
  {
    this.type = type;
  }

  /**
   * Returns the type of this device
   * 
   * @return  The type of this device
   */
  public int getType()
  {
    return type;
  }

  public boolean equals(Object object)
  {
    if ( object == null ) return false;
    if ( object.getClass().equals(this.getClass()) == false ) return false;
    DeviceType that = (DeviceType) object;
    return ( this.type == that.type );
  }
  
  public int hashCode()
  {
    return type;
  }

}
