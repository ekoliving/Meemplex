/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.automation.address;


public class SimpleAddress implements Address
{
	private static final long serialVersionUID = 6424227717462161145L;

  private final String address;

  public SimpleAddress(String address)
  {
    this.address = address;
  }

  public String toString()
  {
    return address;
  }
}
