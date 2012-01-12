/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.net.URL;

import org.openmaji.meem.Facet;
import org.openmaji.spi.MajiSPI;


/**
 * The MeemkitManager Facet is used to ask the meemkit manager to install and
 * uninstall meemkits. Meemkits are jars packaged using the ant build scripts
 * from the Maji SDK.    
 * 
 * Note that when you uninstall a meemkit that 
 *
 * @author Chris Kakris
 */
public interface MeemkitManager extends Facet
{
  /**
   * This is the name of System Property used to define the directory into which
   * the MeemkitManager stores meemkits. 
   */
  public static final String PROPERTY_MEEMKIT_MANAGER_DIRECTORY = "org.openmaji.meemkit.manager.dir";

  /**
   * The name of the directory, relative to PROPERTY_MEEMKIT_MANAGER_DIRECTORY, where
   * available meemkits are located. A meemkit is considered to be available if it
   * has been previously downloaded and cached by the MeemkitManager - whether or not
   * it has been installed.
   */
  public static final String AVAILABLE_MEEMKITS_DIRECTORY = "/available";

  /**
   * The name of the directory, relative to PROPERTY_MEEMKIT_MANAGER_DIRECTORY, where
   * installed meemkits are located.
   */
  public static final String INSTALLED_MEEMKITS_DIRECTORY = "/installed";

  /**
   * Install a Meemkit from a specified URL. The URL should specify the location
   * of a jar file that contains a correctly packaged meemkit. 
   * 
   * @param meemkitName  The name of the meemkit
   * @param meemkitURL  The URL from where the meemkit can be retrieved
   */
  public void installMeemkit(String meemkitName, URL meemkitURL);

  /**
   * Install a Meemkit that has previously been downloaded and stored in a cache
   * managed by the meemkit manager. Do not use this method if you know that the
   * original meemkit has been updated.
   * 
   * @param meemkitName  The name of the meemkit
   */
  public void installMeemkit(String meemkitName);

  /**
   * Upgrade an already installed Meemkit. This is handled differently to an
   * uninstall followed by an install.
   * 
   * @param meemkitName  The name of the meemkit
   * @param meemkitURL  The URL from where the meemkit can be retrieved
   */
  public void upgradeMeemkit(String meemkitName, URL meemkitURL);

  /**
   * Uninstall a meemkit.
   * 
   * @param meemkitName  The name of the meemkit
   */
  public void uninstallMeemkit(String meemkitName);

  /* ---------- Nested class for SPI ----------------------------------------- */

  public class spi
  {
    public static MeemkitManager create()
    {
      return ((MeemkitManager) MajiSPI.provider().create(MeemkitManager.class));
    }

    public static String getIdentifier()
    {
      return "meemkitManager";
    }
  }
}
