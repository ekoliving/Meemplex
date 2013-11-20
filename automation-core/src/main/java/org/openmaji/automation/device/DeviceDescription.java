/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.automation.address.Address;
import org.openmaji.automation.protocol.Protocol;


/**
 * <p>
 * A DeviceDescription allows Meems to refer to devices without 
 * having to pass references to actual device Meems but instead to use
 * a simple description of a device.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public interface DeviceDescription extends MeemDescription, Cloneable
{
  /**
   * The Address of the Device.
   * 
   * @return     The Address of the Device
   */

  public Address getAddress();

  /**
   * Set the address of the device.
   * 
   * @param address The new address for the device
   */

  public void setAddress(Address address);

  /**
   * A unique short-hand identifier of the Device.
   * 
   * @return     The identifier for this device.
   */

  public String getIdentifier();

  /**
   * Set the identifier of the device.
   * 
   * @param identifier The new identifier for the device
   */

  public void setIdentifier(String identifier);

  /**
   * Returns the protocol that this device uses to communicate.
   * 
   * @return  The protocol
   */
  public Protocol getProtocol();
  
  /**
   * Returns the type of this device
   * 
   * @return  The type of this device
   */
  public DeviceType getDeviceType();
  
  public Object clone() throws CloneNotSupportedException;
}

