/*
 * @(#)StandardHyperSpaceCategory.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.space.hyperspace;

import org.openmaji.system.meemserver.MeemServer;

/**
 * <p>
 * StandardHyperSpaceCategory provides the Maji system defined HyperSpace
 * layout as a collection of UnboundMeems.  Currently, this looks like ...
 * </p>
 * <blockquote>
 * /application/
 *   /maji/
 *      /configuration/
 *        /toolkit
 *          /meem/
 *          /wedge/
 *      /installation/
 *        /pattern/
 *          /meem
 *          /wedge
 * </blockquote>
 * <p>
 * See also https://dev.majitek.com/snipsnap//space/MDN-004
 * </p>
 * @author  Andy Gelme
 * @author  Ben Stringer
 * @version 1.0
 */

public final class StandardHyperSpaceCategory {

  /**
   * ------ /application ------
   */

  /**
   * Installed applications releases
   */

  public static final String APPLICATION = "/application";

  /**
   * Maji system Category
   */

  public static final String MAJI_SYSTEM = APPLICATION + "/maji";

  /**
   * Maji system local configuration and customization
   */

  public static final String MAJI_SYSTEM_CONFIGURATION =
    MAJI_SYSTEM + "/configuration";

  /**
   * Maji system local Category for all toolkit patterns
   */

  public static final String MAJI_CONFIGURATION_TOOLKIT =
    MAJI_SYSTEM_CONFIGURATION + "/toolkit";
    
    /**
      * Maji system local Category for all Meem patterns used by the Toolkit MeemView
      */

   public static final String MAJI_CONFIGURATION_TOOLKIT_MEEM =
      MAJI_CONFIGURATION_TOOLKIT + "/meem";

  /**
   * Maji system local Category for all Wedge patterns used by the Toolkit WedgeView
   */
  
  public static final String MAJI_CONFIGURATION_TOOLKIT_WEDGE =
     MAJI_CONFIGURATION_TOOLKIT + "/wedge";
    
	/**
	  * Maji system local Category for all Wedge patterns used by the Toolkit WedgeView
	  */

	 public static final String MAJI_CONFIGURATION_TOOLKIT_WEDGE_UNFILED =
	   MAJI_CONFIGURATION_TOOLKIT_WEDGE + "/unfiled";
        
  /**
   * Maji system installed release
   */

  public static final String MAJI_SYSTEM_INSTALLATION =
    MAJI_SYSTEM + "/installation";

  /**
   * Maji system installed patterns
   */

  public static final String MAJI_SYSTEM_PATTERN =
    MAJI_SYSTEM_INSTALLATION + "/pattern";

  /**
   * Maji system installed Meem patterns
   */

  public static final String MAJI_SYSTEM_PATTERN_MEEM =
    MAJI_SYSTEM_PATTERN + "/meem";

  /**
   * Maji system installed Wedge patterns
   */

  public static final String MAJI_SYSTEM_PATTERN_WEDGE =
    MAJI_SYSTEM_PATTERN + "/wedge";

	/**
	  * Maji system installed libraries
	  */

  public static final String MAJI_SYSTEM_LIBRARY =
    MAJI_SYSTEM_INSTALLATION + "/library";

	/**
	 * Maji system class library 
	 */
	
  public final static String MAJI_SYSTEM_CLASS_LIBRARY =	 
	MAJI_SYSTEM_LIBRARY + "/classlibrary";
	
	/**
	 * Maji system wedge library
	 */
	
  public final static String MAJI_SYSTEM_WEDGE_LIBRARY =	 
	MAJI_SYSTEM_LIBRARY + "/wedgelibrary"; 
		
	/**
	 * Maji system installed command filter, pipes etc.
	 */

  public static final String MAJI_SYSTEM_COMMAND =
    MAJI_SYSTEM_INSTALLATION + "/command";    		    

	/**
	 * ------ /federation ------
	 */
	 
	/**
	 * Federated meemSpace category
	 */
	
  public static final String FEDERATION = "/federation";

	/**
	 * ------ /meemServer ------
	 */
	
	/**
	 * Maji MeemServer category
	 */

  public static final String DEPLOYMENT = "/deployment";
  public static final String MEEMSERVER_DEPLOYMENT = DEPLOYMENT + "/" + MeemServer.spi.getName();  
	
  /**
   * Location of the Subsystem meems
   */
  public static final String SUBSYSTEM = "subsystem";
  public static final String MAJI_SUBSYSTEM = MEEMSERVER_DEPLOYMENT + "/" + SUBSYSTEM;
  public static final String SUBSYSTEM_FACTORY ="subsystemFactory";
  public static final String MAJI_SUBSYSTEM_FACTORY = MAJI_SUBSYSTEM + "/" + SUBSYSTEM_FACTORY;
  public static final String SUBSYSTEM_LCM = "subsystemLifeCycleManager";
  public static final String MAJI_SUBSYSTEM_LCM = MAJI_SUBSYSTEM + "/" + SUBSYSTEM_LCM;
  public static final String SUBSYSTEM_INSTALLED = "installedSubsystems";
  public static final String MAJI_SUBSYSTEM_INSTALLED = MAJI_SUBSYSTEM + "/" + SUBSYSTEM_INSTALLED;
  public static final String SUBSYSTEM_MEEMS_PATH = "subsystemMeems";
  public static final String MAJI_SUBSYSTEM_MEEMS = MAJI_SUBSYSTEM + "/" + SUBSYSTEM_MEEMS_PATH;
  
	/**
	 * ------ /meemPool ------
	 */
		
	/**
	 * meemPool category for contributed meems
	 */

  public static final String MEEMPOOL = "/meemPool";

  /**
   * ------ /site ------
   */
    
  /**
   * site category for site-specific deployment meems
   */

  public static final String SITE = "/site";

	/**
	 * ------ /user ------
	 */
		
	/**
	 * user category for user-created meems
	 */

  public static final String USER = "/user";

	/**
	 * ------ /work ------
	 */
	
	/**
	 * General purpose view category
	 */

  public static final String WORK = "/work";	
}