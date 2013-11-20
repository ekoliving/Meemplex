/*
 * @(#)SubsystemClient.java
 *
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
 * The SubsystemClient Facet allows clients to observe changes in a subsystem's
 * state, receive notification of the creation of Subsystem Meems, and to obtain
 * a list of all the possible Meems that a Subsystem can create.
 * 
 * @author Peter Dettman
 * @author Chris Kakris
 */
public interface SubsystemClient extends Facet
{
  /**
   * Notifies the client that the Subsystem has changed state.
   * 
   * @param subsystemState  The new state of the Subsystem
   */
  public void subsystemStateChanged(SubsystemState subsystemState);

  /**
   * Notifies the client that the Subsystem's commissioned state has changed.
   * 
   * @param commissionState  The new commissioned state
   */
  public void commissionStateChanged(CommissionState commissionState);

  /**
   * Notifies the client that a new subsystem Meem has been created
   * 
   * @param meem  The newly created Meem
   * @param meemDefinition  The MeemDefinition of the newly created Meem
   */
  public void meemCreated(Meem meem, MeemDefinition meemDefinition);

  /**
   * Notifies the client of all the different possible types of Meem that this
   * Subsystem can create.
   * 
   * @param meemDefinitions  An array of MeemDefinitions
   * @param meemDescriptions A corresponding array of MeemDescriptions
   */
  public void meemsAvailable(MeemDefinition[] meemDefinitions, MeemDescription[] meemDescriptions); 
}
