/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

public interface MeemPatternState
{
  public void patternMeemsInstalled(String meemkitName);

  public void patternMeemsUninstalled(String meemkitName);
}
