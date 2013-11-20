/*
 * @(#)MeemStoreCallForwardProxy.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.nursery.jini.meemstore;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;


import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.definition.MeemContent;

public class MeemStoreCallForwardProxy
  implements MeemStoreCallForward, Serializable {
	
	private static final long serialVersionUID = 534540102928363464L;

  private MeemStoreCallForward meemStoreCallForwardServer;

  public MeemStoreCallForwardProxy(
    MeemStoreCallForward meemStoreCallForwardServer) {

    this.meemStoreCallForwardServer = meemStoreCallForwardServer;
  }

  public synchronized void addMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException {

    meemStoreCallForwardServer.addMeemStoreCallBack(meemStoreCallBack);
  }

  public synchronized void removeMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException {

    meemStoreCallForwardServer.removeMeemStoreCallBack(meemStoreCallBack);
  }

  public void storeMeemContent(
    MeemPath    meemPath,
    MeemContent meemContent)
    throws RemoteException {

    meemStoreCallForwardServer.storeMeemContent(meemPath, meemContent);
  }

  public void storeMeemDefinition(
    MeemPath       meemPath,
    MeemDefinition meemDefinition)
    throws RemoteException {

    meemStoreCallForwardServer.storeMeemDefinition(meemPath, meemDefinition);
  }

  public void destroyMeem(
    MeemPath meemPath)
    throws RemoteException {

    meemStoreCallForwardServer.destroyMeem(meemPath);
  }

  public Map<MeemPath, MeemContent> getMeemContent(
    Filter filter)
    throws RemoteException{

    return(meemStoreCallForwardServer.getMeemContent(filter));
  }

  public Map<MeemPath, MeemDefinition> getMeemDefinition(
    Filter filter)
    throws RemoteException{

    return(meemStoreCallForwardServer.getMeemDefinition(filter));
  }
}
