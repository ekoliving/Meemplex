/*
 * @(#)Subsystem.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.manager.lifecycle.subsystem;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * A Subsystem is a Meem whose responsibility is to create, start, stop and
 * destroy a collection of related Meems as a single group. The Meems in this
 * collection are all closely related and their functionality is restricted to
 * a specific domain or application.
 * </p>
 * 
 * <p>
 * A Subsystem has intimate knowledge of the Meems it creates, the dependencies
 * between them and how they are configured. This knowledge is hidden from the
 * application programmer. Meems in a Subsystem are configured by setting the
 * properties of the Subsystem itself. It is the responsibility of the Subsystem
 * to correctly configure all of the Meems it creates.
 * </p>
 * 
 * <p>
 * A Subsystem can be commissioned and decommissioned. The act of commissioning a
 * subsystem results in the creation of that subsystem's application specific Meems.
 * As an example, commissioning an X-10 subsystem results in the creation of all the
 * controller Meems that are needed to communicate with X-10 devices. The act of
 * commissioning also includes configuring and creating dependencies for all those
 * Meems. Decommissioning a Subsystem results in all of the Subsystem's constituent
 * Meems being destroyed.
 * </p>
 *
 * <p>
 * A Subsystem is also responsible of creating and configuring additional Meems that
 * interact with that subsystem. Continuing the X-10 example above, the X-10 subsystem
 * creates X-10 Device Meems that interact with the subsystem's controller Meems.
 * These Meems are different to those created during commissioning.
 * </p>
 * 
 * <p>
 * A Subsystem is considered to be STOPPED if all of its constituent Meems are
 * in a LOADED state. If all of a Subsystem's Constituent Meems are in a READY
 * state then that Subsystem is considered to be in a STARTED state. A Subsystem
 * can also optionally transition through the STARTING and STOPPING states to
 * provide clients additional information about the state of its Meems.
 * </p>
 * 
 * <p>
 * A Subsystem uses a LifeCycleManager to actually create and destroy its Meems.
 * It is expected that a Subsystem Wedge will occupy the same Meem as a LifeCycleManager
 * and use conduits to communicate with it.
 * </p>
 * 
 * @author Peter Dettman
 * @author Chris Kakris
 */
public interface Subsystem extends Facet
{
  /**
   * Instructs the Subsystem to change to the specified state.
   * 
   * @param state  The new state the Subsystem should transition to
   */
  public void changeSubsystemState(SubsystemState state);

  /**
   * Instructs the Subsystem to change its commissioned state.
   * 
   * @param state  The new commissioned state the Subsystem should transition to
   */
  public void changeCommissionState(CommissionState state);

  /**
   * Instructs the Subsystem to create a new Meem. The MeemDefinition and
   * MeemDescription parameters, in most cases, will have originally been provided
   * via the SubsystemClient Facet. The user is expected to have set the identifier
   * in the MeemDefinition's MeemAttribute so that the created Meem can be
   * differentiated from other Meems.
   * 
   * @param meemDefinition  The MeemDefinition to use to create the Meem
   * @param meemDescription The MeemDescription used to configure the Meem
   */
  public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription); 
  
	/**
	 * Nested class for service provider.
	 */
	public class spi
  {
    public static Subsystem create()
    {
      return ( (Subsystem) MajiSPI.provider().create(Subsystem.class) );
    }

    public static String getIdentifier()
    {
      return ( "subsystem" );
    };
  }
}
