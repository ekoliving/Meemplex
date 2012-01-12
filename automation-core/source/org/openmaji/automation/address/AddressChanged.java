/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.address;

/**
 * This interface is implemented by conduits to communicate
 * changes in the address. Different wedges of a meem may need to advise
 * eachother whenever the address changes.
 *
 * @author Chris Kakris
 */
public interface AddressChanged
{
  /**
   * Called when the address is changed.
   * 
   * @param address The new address
   */
  public void addressChanged(Address address);

  /**
   * Called when the address is changed.
   * 
   * @param address The new address
   */
  public void addressChanged(String address);
}
