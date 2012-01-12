/*
 * @(#)MeemStoreCallBack.java
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


import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.definition.MeemContent;

public interface MeemStoreCallBack extends Remote {

  public void meemStored(
    MeemPath meemPath)
    throws RemoteException;

  public void meemDestroyed(
    MeemPath meemPath)
    throws RemoteException;

  public void meemContentChanged(
    MeemPath    meemPath,
    MeemContent meemContent)
    throws RemoteException;

  public void meemDefinitionChanged(
    MeemPath       meemPath,
    MeemDefinition meemDefinition)
    throws RemoteException;
}
