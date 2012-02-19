/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import org.openmaji.automation.address.Address;
import org.openmaji.automation.protocol.Protocol;


/**
 * A concrete implementation of the DeviceDescription interface.
 *
 * @author Chris Kakris
 */
public class BaseDeviceDescription implements DeviceDescription
{
	private static final long serialVersionUID = 6424227717462161145L;

  private Address address;
  private String identifier;
  private String description;
  private final DeviceType deviceType;
  private final Protocol protocol;

  /**
   * Creates an instance with the specified device type and protocol.
   * 
   * @param deviceType
   * @param protocol
   */
  public BaseDeviceDescription(DeviceType deviceType, Protocol protocol)
  {
    this.deviceType = deviceType;
    this.protocol = protocol;
  }

  public Address getAddress()
  {
    return address;
  }

  public void setAddress(Address address)
  {
    this.address = address;
  }

  public String getIdentifier()
  {
    return identifier;
  }

  public void setIdentifier(String identifier)
  {
    this.identifier = identifier;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public Protocol getProtocol()
  {
    return protocol;
  }

  public DeviceType getDeviceType()
  {
    return deviceType;
  }
  
	public Object clone() throws CloneNotSupportedException {
		BaseDeviceDescription clone = new BaseDeviceDescription(deviceType, protocol);
		clone.setAddress(address);
		clone.setIdentifier(identifier);
		clone.setDescription(description);
		return clone;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("BaseDeviceDescription [");
		buffer.append("identifier=");
		buffer.append(identifier);
		buffer.append(", description=");
		buffer.append(description);
		buffer.append(", protocol=");
		buffer.append(protocol.getName());
		buffer.append("]");
		
		return buffer.toString();
	}


}
