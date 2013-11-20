/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.protocol;

import java.io.Serializable;

import org.openmaji.automation.address.AddressSpecification;

/**
 * Represents a network communication protocol.
 *
 * @author Chris Kakris
 */
public interface Protocol extends Serializable
{
  /**
   * Returns the name of the protocol. 
   * 
   * @return The name of the protocol
   */
  public String getName();

  /**
   * Returns an address specification that can be used to verify and to
   * construct addresses that are valid for the protocol.
   * 
   * @return The address specification
   */
  public AddressSpecification getAddressSpecification();
}
