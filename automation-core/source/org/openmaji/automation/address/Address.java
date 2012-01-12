/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.address;

import java.io.Serializable;

/**
 * <p>
 * Each Device has an Address that allows it to be uniquely identified on a network.
 * The specific format of an address is peculiar to a particular network type and will
 * vary from one to another, however in many cases it is often represented as a simple string.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public interface Address extends Serializable
{
  /**
   * Return the address as a String but not to be used for debugging purposes.
   * Normally the toString() method includes a classname but this method returns
   * a String that can be used by the underlying hardware or the device controller code.
   * 
   * @return The address as a String
   */

  public String toString();
}

