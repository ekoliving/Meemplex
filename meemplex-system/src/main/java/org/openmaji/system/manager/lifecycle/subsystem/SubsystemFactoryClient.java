/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.system.manager.lifecycle.subsystem;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;


/**
 * The SubsystemFactoryClient Facet allows clients to observe changes in a
 * SubsystemFactory.
 *
 * @author Chris Kakris
 */
public interface SubsystemFactoryClient extends Facet
{
  /**
   * Notifies the client that MeemDefinitions have been added to the
   * SubsystemFactory. This method is invoked when a dependency is first
   * established (to deliver initial content) and whenever new MeemDefinitions
   * are added. 
   * 
   * @param meemDefinitions  An array of the MeemDefinitions added
   */
  public void definitionsAdded(MeemDefinition[] meemDefinitions);

  /**
   * Notifies the client that MeemDefinitions have been removed
   * from the SubsystemFactory.
   * 
   * @param meemDefinitions  An array of the MeemDefinitions removed
   */
  public void definitionsRemoved(MeemDefinition[] meemDefinitions);

  /**
   * Notifies the client that a new Subsystem has been created.
   * 
   * @param meem  The new Subsystem Meem
   * @param meemDefinition  The MeemDefinition of the Subsystem created
   */
  public void subsystemCreated(Meem meem, MeemDefinition meemDefinition);

  /**
   * Notifies the client that the subsystem has been destroyed. It is recommended
   * that a subsystem be decomissioned before it is destroyed.
   * 
   * @param meem  Unbound meem that has been destroyed
   */
  public void subsystemDestroyed(Meem meem);
}
