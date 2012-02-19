/*
 * @(#)MeemStoreImporterWedge.java
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

import java.rmi.RemoteException;
import java.util.*;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;
import net.jini.config.NoSuchEntryException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryGroupManagement;
import net.jini.discovery.LookupDiscovery;
import net.jini.lookup.ServiceDiscoveryManager;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;


import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.reference.ContentProvider;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.meemstore.MeemStoreClient;
import org.swzoo.log2.core.*;

public class MeemStoreImporterWedge
  implements MeemStore, MeemStoreCallBack, Wedge {

  public MeemStoreClient meemStoreClient;  // Outbound Facet

  public MeemContentClient meemContentClient;  // Outbound Facet

  public final ContentProvider meemContentClientProvider =
    new MeemContentClientContentProvider();  // Inner class defined below

  public MeemDefinitionClient meemDefinitionClient;  // Outbound Facet

  public final ContentProvider meemDefinitionClientProvider =
    new MeemDefinitionClientContentProvider();  // Inner class defined below

 public LifeCycleClient	lifeCycleClientConduit = new LifeCycleClientAdapter(this);
 
  private static MeemStoreImporterWedge meemStoreImporterWedge;  // Singleton

  private MeemStoreCallForward meemStoreCallForward = null;

  private static final Logger logger = LogFactory.getLogger();

  private Configuration configuration = null;

  private ServiceDiscoveryManager serviceDiscoveryManager = null;

  private static Class[] serviceInterfaces =
    new Class[] { MeemStoreCallForward.class };

  private static ServiceTemplate serviceTemplate =
    new ServiceTemplate(null, serviceInterfaces, null);

  public static MeemStoreImporterWedge getInstance() {
    if (meemStoreImporterWedge == null) {
      throw new RuntimeException("meemStoreImporterWedge not instantiated");
    }

    return(meemStoreImporterWedge);
  }

  public MeemStoreImporterWedge() {
    meemStoreImporterWedge = this;
  }

  public void commence() {
/* ---------- Jini initialization ------------------------------------------ */

    String majitekDirectory =
      System.getProperty(Common.PROPERTY_MAJI_HOME);

    if (majitekDirectory == null) {
      throw new RuntimeException(
        "Empty Majitek directory property: " +
         Common.PROPERTY_MAJI_HOME
      );
    }

    try {
      configuration = ConfigurationProvider.getInstance(
        new String[] {
          majitekDirectory +
          System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE)
        }
      );

      if (serviceDiscoveryManager == null) {
        try {
          serviceDiscoveryManager = (ServiceDiscoveryManager)
            configuration.getEntry(
              "org.openmaji.implementation.server.nursery.jini.meemstore.MeemStoreExporterWedge",
              "serviceDiscovery",
              ServiceDiscoveryManager.class
            );
        }
        catch (NoSuchEntryException noSuchEntryException) {
        /* Default to search in the public group */
          serviceDiscoveryManager = new ServiceDiscoveryManager(
            new LookupDiscovery(
              DiscoveryGroupManagement.ALL_GROUPS, configuration
            ),
            null,
            configuration
          );
        }
      }

/* ---------- Jini Service lookup ------------------------------------------ */

      ServiceItem serviceItem = serviceDiscoveryManager.lookup(
        serviceTemplate, null, Long.MAX_VALUE
      );

      meemStoreCallForward = (MeemStoreCallForward) serviceItem.service;

      LogTools.info(logger, "Jini Service lookup: " + serviceItem.serviceID);
    }
    catch (Exception exception) {
      throw new RuntimeException("Exception:" + exception);
    }
  }

  public void conclude() {
  }

/* ---------- Inbound Facet: MeemStore ------------------------------------- */

  public void storeMeemContent(
    MeemPath    meemPath,
    MeemContent meemContent) {

    try {
      LogTools.info(logger, "# storeMeemContent(" + meemPath + ")");

      meemStoreCallForward.storeMeemContent(meemPath, meemContent);
    }
    catch (Exception exception) {
      LogTools.error(logger, "# MeemStoreCallForward: " + exception);
    }
  }

  public void storeMeemDefinition(
    MeemPath       meemPath,
    MeemDefinition meemDefinition) {

    try {
      LogTools.info(logger, "# storeMeemDefinition(" + meemPath + ")");

      meemStoreCallForward.storeMeemDefinition(meemPath, meemDefinition);
    }
    catch (Exception exception) {
      LogTools.error(logger, "# MeemStoreCallForward: " + exception);
    }
  }

  public void destroyMeem(
    MeemPath meemPath) {

    try {
      LogTools.info(logger, "# destroyMeem(" + meemPath + ")");

      meemStoreCallForward.destroyMeem(meemPath);
    }
    catch (Exception exception) {
      LogTools.error(logger, "# MeemStoreCallForward: " + exception);
    }
  }

/* ---------- Interface: MeemStoreCallBack --------------------------------- */

  public void meemStored(
    MeemPath meemPath)
    throws RemoteException {

    LogTools.info(logger, "# meemStored(" + meemPath + ")");

    meemStoreClient.meemStored(meemPath);
  }

  public void meemDestroyed(
    MeemPath meemPath)
    throws RemoteException {

    LogTools.info(logger, "# meemDestroyed(" + meemPath + ")");

    meemStoreClient.meemDestroyed(meemPath);
  }

  public void meemContentChanged(
    MeemPath    meemPath,
    MeemContent meemContent)
    throws RemoteException {

    LogTools.info(logger, "# meemContentChanged(" + meemPath + ")");

    meemContentClient.meemContentChanged(meemPath, meemContent);
  }

  public void meemDefinitionChanged(
    MeemPath       meemPath,
    MeemDefinition meemDefinition)
    throws RemoteException {

    LogTools.info(logger, "# meemDefinitionChanged(" + meemPath + ")");

    meemDefinitionClient.meemDefinitionChanged(meemPath, meemDefinition);
  }

/* ---------- ContentProvider: MeemContentClient --------------------------- */

  private final class MeemContentClientContentProvider
    implements ContentProvider {

    public void sendContent(
      Object target,
      Filter filter) {

      try {
        LogTools.info(logger, "# getMeemContents(" + filter + ")");

        MeemContentClient meemContentClient = (MeemContentClient) target;

        HashMap meemContents = meemStoreCallForward.getMeemContent(filter);

        Iterator iterator = meemContents.keySet().iterator();

        while (iterator.hasNext()) {
          MeemPath meemPath = (MeemPath) iterator.next();

          MeemContent meemContent = (MeemContent) meemContents.get(meemPath);

          meemContentClient.meemContentChanged(meemPath, meemContent);
        }
      }
      catch (Exception exception) {
        LogTools.error(logger, "# sendContent(): " + exception);
      }
    }
  }

/* ---------- ContentProvider: MeemDefinitionClient ------------------------ */

  private final class MeemDefinitionClientContentProvider
    implements ContentProvider {

    public void sendContent(
      Object target,
      Filter filter) {

      try {
        LogTools.info(logger, "# getMeemDefinitions(" + filter + ")");

        MeemDefinitionClient meemDefinitionClient =
          (MeemDefinitionClient) target;

        HashMap meemDefinitions =
          meemStoreCallForward.getMeemDefinition(filter);

        Iterator iterator = meemDefinitions.keySet().iterator();

        while (iterator.hasNext()) {
          MeemPath meemPath = (MeemPath) iterator.next();

          MeemDefinition meemDefinition =
            (MeemDefinition) meemDefinitions.get(meemPath);

          meemDefinitionClient.meemDefinitionChanged(meemPath, meemDefinition);
        }
      }
      catch (Exception exception) {
        LogTools.error(logger, "# sendContent(): " + exception);
      }
    }
  }
}
