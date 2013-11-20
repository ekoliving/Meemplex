/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import org.openmaji.meem.Facet;

/**
 * The MeemkitManagerClient facet is used by the meemkit manager to notify
 * clients of any changes.
 *
 * @author Chris Kakris
 */
public interface MeemkitManagerClient extends Facet
{
  /**
   * Signals that a Meemkit has been installed. 
   * 
   * @param meemkitName The name of the Meemkit
   */
  public void meemkitInstalled(String meemkitName);
  
  /**
   * Signals that a Meemkit has been upgraded.
   * 
   * @param meemkitName The name of the Meemkit
   */
  public void meemkitUpgraded(String meemkitName);

  /**
   * Signals that a Meemkit has been uninstalled.
   * 
   * @param meemkitName The name of the Meemkit
   */
  public void meemkitUninstalled(String meemkitName);

  /**
   * Provides a list of the MeemkitDescriptos known by the meemkit manager
   * whether or not the corresponding meemkits are installed.
   * 
   * @param meemkitDescriptors An array of MeemkitDescriptors
   */
  public void meemkitDescriptorsAdded(MeemkitDescriptor[] meemkitDescriptors);

  /**
   * Provides a list of MeemkitDescriptors that the meemkit manager no longer
   * knows anything about.
   * 
   * @param meemkitDescriptors An array of MeemkitDescriptors
   */
  public void meemkitDescriptorsRemoved(MeemkitDescriptor[] meemkitDescriptors);
}
