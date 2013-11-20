/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.io.Serializable;

/**
 * Describes a single Meemkit dependency that is required to be resolved
 * before a Meemkit can be installed.
 * 
 * @author Chris Kakris
 */

public class MeemkitDependency implements Serializable
{
	private static final long serialVersionUID = 534540102928363464L;
	
  private final MeemkitVersion meemkitVersion;
  
  /**
   * Creates an instance representing a dependency on a specified meemkit. 
   * 
   * @param meemkitVersion  The meemkit version on which the dependency exists
   */

  public MeemkitDependency(MeemkitVersion meemkitVersion)
  {
    this.meemkitVersion = meemkitVersion;
  }
  
  /**
   * Returns the meemkit version that this dependency represents.
   * 
   * @return  The meemkit version
   */

  public MeemkitVersion getMeemkitVersion()
  {
    return meemkitVersion;
  }

}
