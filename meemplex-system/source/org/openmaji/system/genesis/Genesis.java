/*
 * @(#)Genesis.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.genesis;

import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * Genesis is the environment independent means of bootstrapping a MeemServer.
 * This takes it from being nothing to being operational, i.e. able to create
 * and activate Meems.
 * </p>
 * <p>
 * Genesis expects that an environment specific Launch routine has placed
 * all Maji related properties into the System properties.
 * </p>
 * <p>
 * The actual set of essential Meems is specified by the property
 * "org.openmaji.system.genesis.profile", which defaults to the "core" set.
 * </p>
 * <p>
 * Genesis profiles can be specified as either ...
 * <ul>
 *   <li>"core"                   - Minimum required to run a MeemServer</li>
 *   <li>"custom:factory,factory" - Customized list of essential Meems</li>
 *   <li>"all"                    - All essential Meems will be used</li>
 * </ul>
 * </p>
 * <p>
 * Based on the profile, the LifeCycleManager is given a MeemDefinition
 * for each essential Meem, so that they can be created and registered with
 * MeemRegistry.  When all the essential Meems are ready, then an initial
 * MeemPath is given to the LifeCycleManager, so that all Meems specific
 * to this MeemServer are activated.
 * </p>
 * <p>
 * Genesis performs the absolute minimum effort to get a LifeCycleManager
 * going.  It is required that this initial LifeCycleManager is fully
 * self-contained.  The LifeCycleManager is then responsible for starting
 * up as much or as little of the Maji system, that is required within the
 * currently running MeemServer.
 * </p>
 * <p>
 * By "launching" a number of MeemServers, whose Meems interoperate via a
 * distributed system platform, such as Jini ... a complete Maji system can
 * be made operational.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface Genesis {

  /**
   * Property for specifying the set of essential Meems in this MeemServer
   */

  public static final String PROPERTY_GENESIS_PROFILE =
    "org.openmaji.system.genesis.profile";

  /**
   * Default profile for essential Meems in this MeemServer
   */

  public static final String DEFAULT_GENESIS_PROFILE = "core";

  /**
   * Property for specifying the set of essential Meems in this MeemServer
   */

  public static final String PROPERTY_GENESIS_MEEMSERVER_CATEGORYPATH =
    "org.openmaji.system.genesis.meemServerCategoryPath";

  /**
   * Property for specifying the Category listened to by the top-level
   * PersistingLifeCycleManager in this MeemServer.
   */

  public static final String PROPERTY_GENESIS_LIFECYCLEMANAGER_CATEGORYNAME =
    "org.openmaji.system.genesis.lifeCycleManagerCategoryName";

  /**
   * <p>
   * In the beginning ...
   * </p>
   * <p>
   * Creates and registers a set of essential Meems.
   * The actual set is specified by the PROPERTY_GENESIS_PROFILE.
   * Then the LifeCycleManager is given its initial MeemPath.
   * </p>
   * <p>
   * ... and a "void" was returned !?!?!
   * </p>
   * @exception RuntimeException         Incorrect Genesis profile specified
   * @exception IllegalArgumentException Problem loading essential Meem
   * @exception RuntimeException         Problem instantiating essential Meem
   */

  public void bigBang()
    throws IllegalArgumentException;
	
 /**
  * Nested class for service provider.
  * 
  * @see org.openmaji.spi.MajiSPI
  */
  public class spi {
  	private static Genesis genesis;
  	
    public static Genesis create() {
    	genesis = (Genesis) MajiSPI.provider().create(Genesis.class);
      return genesis;
    }
  }
}
