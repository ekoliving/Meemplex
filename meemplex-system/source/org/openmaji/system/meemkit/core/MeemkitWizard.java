/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.system.meemkit.core;

import java.awt.Container;

/**
 * Interface implemented by a small GUI application that a Meemkit can
 * install into an IDE.
 *
 * @author Chris Kakris
 */
public interface MeemkitWizard
{
  public void initialize(Container container);
}
