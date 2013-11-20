/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.manager.lifecycle.subsystem;

import java.io.Serializable;

/**
 * MeemDescription is used to provide additional information to a Subsystem
 * when it is creating new instances of a Subsystem Meem. It is expected that
 * implementors will extend this class with concrete implementations that provide
 * domain specific information.
 *
 * @author Chris Kakris
 */
public interface MeemDescription extends Serializable
{
  /**
   * Return the textual description used to describe a Meem.
   * 
   * @return The textual description
   */
  public String getDescription();

  /**
   * Set the textual description for a Meem
   * 
   * @param description The textual description
   */
  public void setDescription(String description);
}
