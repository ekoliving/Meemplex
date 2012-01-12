/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.manager.lifecycle.subsystem;

import org.openmaji.meem.definition.MeemDefinition;

/**
 *
 *
 * @author Chris Kakris
 */
public interface SubsystemMeemControl
{
  public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription);
}
