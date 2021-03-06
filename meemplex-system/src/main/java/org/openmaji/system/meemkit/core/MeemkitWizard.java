/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
