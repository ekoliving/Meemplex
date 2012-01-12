/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.manager.lifecycle.subsystem;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.spi.MajiSPI;


/**
 * A SubsystemFactory fulfills two roles: firstly, it keeps track of what types
 * of Subsystems can be created; and secondly, it is able to create instances
 * of new SubSystems.
 *
 * @author Chris Kakris
 */
public interface SubsystemFactory extends Facet
{
  /**
   * Tells the factory that a new type of Subsystem is available.
   * 
   * @param meemDefinition The MeemDefinition of the subsystem 
   */
  public void addSubsystemDefinition(MeemDefinition meemDefinition);

  /**
   * Tells the factory that an existing type of Subsystem is no longer available.
   * 
   * @param meemDefinition The MeemDefintion of the subsystem 
   */
  public void removeSubsystemDefinition(MeemDefinition meemDefinition);

  /**
   * Causes the factory to create a new Subsystem using the provided MeemDefinition.
   * The user is expected to have set the identifier in the MeemDefinition's
   * MeemAttribute so that the created Subsystem can be differentiated from other
   * Subsystems. Once the Meem has been created it is added to the category of
   * installed Subsystems.
   * 
   * @param meemDefinition The MeemDefintion of the subsystem 
   */
  public void createSubsystem(MeemDefinition meemDefinition);

  /**
   * Instructs the factory to destroy the specified Meem and remove if
   * from the category of installed subsystems.
   * 
   * @param meem  The meem to destroy
   */
  public void destroySubsystem(Meem meem);

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
      return ( "subsystemFactory" );
    };
  }
}
