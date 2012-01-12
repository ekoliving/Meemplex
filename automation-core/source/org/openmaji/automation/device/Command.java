/*
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.automation.device;

import java.io.Serializable;

/**
 * <p>
 * Instructions sent to devices are encapsulated as Commands. These instructions
 * will often cause some sort of action to be undertaken by the device: for example
 * to switch on a light. The format of a command will differ from one hardware network
 * type to another, however it is often represented as a simple string.
 * </p>
 * 
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author  Christos Kakris
 */

public interface Command extends Serializable
{
  /**
   * Returm the command as a String.
   * 
   * @return The command as a String.
   */

  public String getCommand();
}

