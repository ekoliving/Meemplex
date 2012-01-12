/*
 * @(#)MeemStoreCallForward.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.nursery.jini.meemstore;

import java.rmi.*;
import java.util.HashMap;


import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.spi.MajiSPI;
import org.openmaji.system.meem.definition.MeemContent;

public interface MeemStoreCallForward extends Remote {

  public void addMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException;

  public void removeMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException;

/* ---------- Interface: MeemStore ----------------------------------------- */

  public void storeMeemContent(
    MeemPath meemPath,
    MeemContent meemContent)
    throws RemoteException;

  public void storeMeemDefinition(
    MeemPath       meemPath,
    MeemDefinition meemDefinition)
    throws RemoteException;

  public void destroyMeem(
    MeemPath meemPath)
    throws RemoteException;

/* ---------- Synchronous: MeemContentClient ------------------------------ */

  public HashMap getMeemContent(  // used by sendContent()
    Filter filter)
    throws RemoteException;

/* ---------- Synchronous: MeemDefinitionClient --------------------------- */

  public HashMap getMeemDefinition(  // used by sendContent()
    Filter filter)
    throws RemoteException;

/* ---------- Nested class for SPI ----------------------------------------- */

  public class spi {
    public static MeemStoreCallForward create() {
      return(
        (MeemStoreCallForward) MajiSPI.provider().create(
          MeemStoreCallForward.class
        )
      );
    }

    public static String getIdentifier() {
      return("meemStoreExporter");
    };
  }
}