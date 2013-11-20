/*
 * @(#)EssentialLifeCycleManager.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.lifecycle;

import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.LifeCycleState;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * The EssentialLifeCycleManager is the top of the LifeCycleManagement tree
 * for a MeemServer. It is responsible for starting the systems essential meems.
 * It is different from other Meems and LifeCycleManagers in that it is it own
 * LifeCycleManager.
 * </p>
 * <p>
 * Genesis creates an EssentialLifeCycleManager during system start and 
 * uses it to create all the essential Meems for the MeemServer.
 * </p> 
 * @see org.openmaji.system.genesis.Genesis
 * @version 1.0
 */
public interface EssentialLifeCycleManager {

	/**
	 * This is called by Genesis after it has created an instance of EssentialLifeCycleMangerWedge
	 * to get the wedge to turn itself into a Meem.
	 * @throws RuntimeException Thrown if the EssentialLifeCycleManager has already been bootstrapped.
	 * @see org.openmaji.system.genesis.Genesis
	 */
	public void bootstrap()
		throws RuntimeException;

	/**
	 * Called by Genesis once all the essential Meems are created to commence normal operation
	 * of the EssentialLifeCycleManager. 
	 * @see org.openmaji.system.genesis.Genesis
	 */
	public void start();

	/**
	 * Called by Genesis to create essential Meems. Only works after bootstrap() has been called and
	 * before start() has been called.
	 * @param meemDefinition The MeemDefinition of the Meem to be created
	 * @param initialLifeCycleState The initial LifeCycleState for the Meem
	 * @exception IllegalArgumentException
	 */
	public void createEssentialMeem(
	  MeemDefinition meemDefinition,
	  LifeCycleState initialLifeCycleState)
	  throws IllegalArgumentException;

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static EssentialLifeCycleManager create() {
      return((EssentialLifeCycleManager) MajiSPI.provider().create(
        EssentialLifeCycleManager.class)
      );
    }
    
    public static String getIdentifier() {
      return("essentialLifeCycleManager");
    };    
  }
}