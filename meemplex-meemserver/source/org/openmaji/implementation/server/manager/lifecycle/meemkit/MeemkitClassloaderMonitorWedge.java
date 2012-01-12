/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.server.manager.lifecycle.meemkit;

import org.openmaji.meem.Wedge;

public class MeemkitClassloaderMonitorWedge implements Wedge
{
  public MeemkitClassloaderMonitor meemkitClassloaderMonitorConduit = new MeemkitClassloaderMonitorConduit();
  public MeemkitClassloaderMonitor meemkitClassloaderMonitorClient;

  /* ------------------------------------------------------------------------ */

  private class MeemkitClassloaderMonitorConduit implements MeemkitClassloaderMonitor
  {
    public void meemkitClassloaderStarted(String meemkitName)
    {
      meemkitClassloaderMonitorClient.meemkitClassloaderStarted(meemkitName);
    }

    public void meemkitClassloaderStopped(String meemkitName)
    {
      meemkitClassloaderMonitorClient.meemkitClassloaderStopped(meemkitName);
    }
  }
}
