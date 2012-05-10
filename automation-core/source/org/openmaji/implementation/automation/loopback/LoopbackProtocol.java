/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.automation.loopback;

import org.openmaji.automation.address.AddressSpecification;
import org.openmaji.automation.protocol.Protocol;


/**
 *
 *
 * @author Chris Kakris
 */
public class LoopbackProtocol implements Protocol
{
	private static final long serialVersionUID = 6424227717462161145L;

  private static final String PROTOCOL_NAME = "Loopback";

  public String getName()
  {
    return PROTOCOL_NAME;
  }

  public AddressSpecification getAddressSpecification()
  {
    return null;
  }

}
