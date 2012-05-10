/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import org.openmaji.meem.Facet;

public interface MeemkitClassloaderMonitor extends Facet
{
  public void meemkitClassloaderStarted(String meemkitName);
  public void meemkitClassloaderStopped(String meemkitName);
}
