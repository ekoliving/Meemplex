/*
 * @(#)MeemStoreExporterWedge.java
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

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import net.jini.config.*;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.id.*;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;



import org.openmaji.meem.*;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.utility.*;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.meemstore.MeemStoreClient;
import org.swzoo.log2.core.*;

public class MeemStoreExporterWedge
  implements MeemDefinitionProvider,
             Wedge,
             MeemStoreClient,        // Inbound Facet from real MeemStore
             MeemContentClient,      // Inbound Facet from real MeemStore
             MeemDefinitionClient,   // Inbound Facet from real MeemStore
             MeemStoreCallForward {  // Remote interface

  private static final String MEEMSTORE_EXPORTER_WEDGE_NAME =
    "org.openmaji.implementation.server.nursery.jini.meemstore.MeemStoreExporterWedge";

  private static final String MEEMSTORE_EXPORTER_NAME = "meemStoreExporter";

  public MeemCore meemCore;

  public MeemStore meemStore;  // Outbound Facet

  public DependencyHandler dependencyHandlerConduit;  // Outbound Conduit
  
  public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

  private DependencyAttribute meemStoreDependencyAttribute = null;

  private DependencyAttribute meemStoreClientDependencyAttribute = null;

  private DependencyAttribute meemContentClientDependencyAttribute = null;

  private DependencyAttribute meemDefinitionClientDependencyAttribute = null;

  private WeakHashMap meemStoreCallBacks = new WeakHashMap();

  private static final Logger logger = LogFactory.getLogger();

  private static MeemStoreExporterWedge meemStoreExporterWedge;  // Singleton

  private static final long timeout = 60000L;  // One minute

  private Configuration configuration = null;

  private LookupDiscoveryManager lookupDiscoveryManager = null;

  private JoinManager joinManager;

  public static MeemStoreExporterWedge getInstance() {
    if (meemStoreExporterWedge == null) {
      throw new RuntimeException("meemStoreExporterWedge not instantiated");
    }

    return(meemStoreExporterWedge);
  }

  public MeemStoreExporterWedge() {
    meemStoreExporterWedge = this;
  }

  public void commence() {
    meemStoreDependencyAttribute = new DependencyAttribute(
      DependencyType.STRONG,
      Scope.LOCAL,
      meemCore.getMeemStore(),
      "meemStore",
      null, 
			true
    );

    dependencyHandlerConduit.addDependency(
      "meemStore", meemStoreDependencyAttribute, LifeTime.TRANSIENT
    );

    meemStoreClientDependencyAttribute = new DependencyAttribute(
      DependencyType.STRONG,
      Scope.LOCAL,
      meemCore.getMeemStore(),
      "meemStoreClient",
      null, 
			true
    );

    dependencyHandlerConduit.addDependency(
      "meemStoreClient", meemStoreClientDependencyAttribute, LifeTime.TRANSIENT
    );

    meemContentClientDependencyAttribute = new DependencyAttribute(
      DependencyType.STRONG,
      Scope.LOCAL,
      meemCore.getMeemStore(),
      "meemContentClient",
      null, 
			true
    );

    dependencyHandlerConduit.addDependency(
      "meemContentClient",
       meemContentClientDependencyAttribute,
       LifeTime.TRANSIENT
    );

    meemDefinitionClientDependencyAttribute = new DependencyAttribute(
      DependencyType.STRONG,
      Scope.LOCAL,
      meemCore.getMeemStore(),
      "meemDefinitionClient",
      null, 
			true
    );

    dependencyHandlerConduit.addDependency(
      "meemDefinitionClient",
       meemDefinitionClientDependencyAttribute,
       LifeTime.TRANSIENT
    );

/* ---------- Jini initialization ------------------------------------------ */

    String majitekDirectory =
      System.getProperty(Common.PROPERTY_MAJI_HOME);

    if (majitekDirectory == null) {
      throw new RuntimeException(
        "Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME
      );
    }

    try {
      configuration = ConfigurationProvider.getInstance(
        new String[] {
          majitekDirectory +
          System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE)
        }
      );
    }
    catch (ConfigurationException configurationException) {
      throw new RuntimeException(
        "ConfigurationProviderException:" + configurationException
      );
    }

    if (lookupDiscoveryManager == null) {
      try {
        lookupDiscoveryManager = new LookupDiscoveryManager(
          new String[] { MeemSpace.getIdentifier() },
          null,  // LookupLocator[]
          null   // DiscoveryListener
        );
      }
      catch(IOException ioException) {
        throw new RuntimeException("LookupDiscoveryManager: IOException");
      }
    }

/* ---------- Jini Service register ---------------------------------------- */

    Uuid uuid = UuidFactory.generate();

    try {
      Exporter meemStoreExporter = (Exporter) configuration.getEntry(
        MEEMSTORE_EXPORTER_WEDGE_NAME,  // component
        MEEMSTORE_EXPORTER_NAME,        // name
        Exporter.class                  // class
      );

      MeemStoreCallForward meemStoreCallForwardProxy = (MeemStoreCallForward)
        meemStoreExporter.export(new MeemStoreCallForwardProxy(this));

      ServiceID serviceID = new ServiceID(
        uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()
      );

      LeaseRenewalManager leaseRenewalManager = new LeaseRenewalManager();

//      (LeaseRenewalManager) configuration.getEntry(
//        "net.jini.lease.LeaseRenewalManager",  // Component
//        "leaseRenewalManager",                 // Name
//        LeaseRenewalManager.class              // Class
//      );

      try {
        joinManager = new JoinManager(
          meemStoreCallForwardProxy,  // Object
          null,                       // Entry[] AttrSets
          serviceID,                  // ServiceID
          lookupDiscoveryManager,     // LookupDiscoveryManager
          leaseRenewalManager
        );
      }
      catch(IOException ioException) {
        throw new RuntimeException("JoinManager: IOException: " + ioException);
      }
      catch(IllegalArgumentException illegalArgumentException) {
        throw new RuntimeException(
          "JoinManager: MeemStoreExporter: " + illegalArgumentException
        );
      }
    }
    catch (Exception exception) {
      LogTools.error(logger, "Exporting MeemStoreExporterWedge: " + exception);
    }

    LogTools.info(logger, "Jini Service registered: " + uuid);

/* ------------------------------------------------------------------------- */
  }

  public void conclude() {
    dependencyHandlerConduit.removeDependency(
      meemStoreDependencyAttribute
    );

    dependencyHandlerConduit.removeDependency(
      meemStoreClientDependencyAttribute
    );

    dependencyHandlerConduit.removeDependency(
      meemContentClientDependencyAttribute
    );

    dependencyHandlerConduit.removeDependency(
      meemDefinitionClientDependencyAttribute
    );

    // -----------------------
    // Jini Service deregister
    //
    // Supposed to use meemStoreExporter.unexport() when deregistering ?

    if (joinManager != null) joinManager.terminate();
  }

/* ---------- Inbound Facet: MeemStoreClient ------------------------------- */

  public synchronized void meemStored(
    MeemPath meemPath) {

    LogTools.info(logger, "meemStored(" + meemPath + ")");

    Vector   zombies = new Vector();
    Iterator iterator = meemStoreCallBacks.values().iterator();

    while (iterator.hasNext()) {
      MeemStoreCallBack meemStoreCallBack = (MeemStoreCallBack) iterator.next();

      try {
        meemStoreCallBack.meemStored(meemPath);
      }
      catch (Exception exception) {
        LogTools.error(logger, "MeemStoreCallBack removed due to Exception");

        zombies.add(meemStoreCallBack);
      }
    }

    if (zombies.isEmpty() == false) {
      iterator = zombies.iterator();

      while (iterator.hasNext()) meemStoreCallBacks.remove(iterator.next());
    }
  }

  public synchronized void meemDestroyed(
    MeemPath meemPath) {

    LogTools.info(logger, "meemDestroyed(" + meemPath + ")");

    Vector   zombies = new Vector();
    Iterator iterator = meemStoreCallBacks.values().iterator();

    while (iterator.hasNext()) {
      MeemStoreCallBack meemStoreCallBack = (MeemStoreCallBack) iterator.next();

      try {
        meemStoreCallBack.meemDestroyed(meemPath);
      }
      catch (Exception exception) {
        LogTools.error(logger, "MeemStoreCallBack removed due to Exception");

        zombies.add(meemStoreCallBack);
      }
    }

    if (zombies.isEmpty() == false) {
      iterator = zombies.iterator();

      while (iterator.hasNext()) meemStoreCallBacks.remove(iterator.next());
    }
  }

/* ---------- Inbound Facet: MeemContentClient ----------------------------- */

  public synchronized void meemContentChanged(
    MeemPath    meemPath,
    MeemContent meemContent) {

    LogTools.info(logger, "meemContentChanged(" + meemPath + ")");

    Vector   zombies = new Vector();
    Iterator iterator = meemStoreCallBacks.values().iterator();

    while (iterator.hasNext()) {
      MeemStoreCallBack meemStoreCallBack = (MeemStoreCallBack) iterator.next();

      try {
        meemStoreCallBack.meemContentChanged(meemPath, meemContent);
      }
      catch (Exception exception) {
        LogTools.error(logger, "MeemStoreCallBack removed due to Exception");

        zombies.add(meemStoreCallBack);
      }
    }

    if (zombies.isEmpty() == false) {
      iterator = zombies.iterator();

      while (iterator.hasNext()) meemStoreCallBacks.remove(iterator.next());
    }
  }

/* ---------- Inbound Facet: MeemDefinitionClient -------------------------- */

  public synchronized void meemDefinitionChanged(
    MeemPath       meemPath,
    MeemDefinition meemDefinition) {

    LogTools.info(logger, "meemDefinitionChanged(" + meemPath + ")");

    Vector   zombies = new Vector();
    Iterator iterator = meemStoreCallBacks.values().iterator();

    while (iterator.hasNext()) {
      MeemStoreCallBack meemStoreCallBack = (MeemStoreCallBack) iterator.next();

      try {
        meemStoreCallBack.meemDefinitionChanged(meemPath, meemDefinition);
      }
      catch (Exception exception) {
        LogTools.error(logger, "MeemStoreCallBack removed due to Exception");

        zombies.add(meemStoreCallBack);
      }
    }

    if (zombies.isEmpty() == false) {
      iterator = zombies.iterator();

      while (iterator.hasNext()) meemStoreCallBacks.remove(iterator.next());
    }
  }

/* ---------- Interface: MeemStoreCallForward ------------------------------ */

  public synchronized void addMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException {

    LogTools.info(logger, "addMeemStoreCallBack(" + meemStoreCallBack + ")");

    meemStoreCallBacks.put(meemStoreCallBack, null);
  }

  public synchronized void removeMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException {

    LogTools.info(logger, "removeMeemStoreCallBack(" + meemStoreCallBack + ")");

    meemStoreCallBacks.remove(meemStoreCallBack);
  }

/* ---------- Interface: MeemStore ----------------------------------------- */

  public void storeMeemContent(
    MeemPath meemPath,
    MeemContent meemContent)
    throws RemoteException {

    if (meemStore == null) {
      LogTools.error(
        logger, "storeMeemContent() invoked, but 'meemStore' not connected"
      );
    }
    else {
      LogTools.info(logger, "storeMeemContent(" + meemPath + ")");

      meemStore.storeMeemContent(meemPath, meemContent);
    }
  }

  public void storeMeemDefinition(
    MeemPath       meemPath,
    MeemDefinition meemDefinition)
    throws RemoteException {

    if (meemStore == null) {
      LogTools.error(
        logger, "storeMeemDefinition() invoked, 'meemStore' not connected"
      );
    }
    else {
      LogTools.info(logger, "storeMeemDefinition(" + meemPath + ")");

      meemStore.storeMeemDefinition(meemPath, meemDefinition);
    }
  }

  public void destroyMeem(
    MeemPath meemPath)
    throws RemoteException {

    if (meemStore == null) {
      LogTools.error(
        logger, "destroyMeem() invoked, but 'meemStore' not connected"
      );
    }
    else {
      LogTools.info(logger, "destroyMeem(" + meemPath + ")");

      meemStore.destroyMeem(meemPath);
    }
  }

/* ---------- Synchronous: MeemContentClient ------------------------------- */

  public HashMap getMeemContent(  // used by sendContent()
    Filter filter)
    throws RemoteException{

    PigeonHole pigeonHole = new PigeonHole();

    HashMap meemContents = null;

    MeemContentClient meemContentClient = (MeemContentClient)
      meemCore.getTargetFor(new MeemContentClientImpl(pigeonHole), MeemContentClient.class);

    meemCore.getMeemStore().addOutboundReference(
      Reference.spi.create("meemContentClient", meemContentClient, true, filter),
      true
    );

    try {
      meemContents = (HashMap) pigeonHole.get(timeout);
    }
    catch (TimeoutException timeoutException) {
      LogTools.error(
        logger,
        "Timeout: MeemStoreExporterWedge <- MeemStore: MeemContentClient",
        timeoutException
      );
    }

    return(meemContents);
  }

  private final class MeemContentClientImpl
    implements MeemContentClient, ContentClient {

    private PigeonHole pigeonHole;

    private HashMap meemContents = new HashMap();

    public MeemContentClientImpl(
      PigeonHole pigeonHole) {

      this.pigeonHole = pigeonHole;
    }

    public void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {
      if (meemContents != null) meemContents.put(meemPath, meemContent);
    }

    public void contentSent() {
      if (meemContents != null) {
        pigeonHole.put(meemContents);
        meemContents = null;  // Don't accept further meemContent
      }
    }

	public void contentFailed(String reason) {
		if (meemContents != null) {
          meemContents.clear();
		  pigeonHole.put(meemContents);
		  meemContents = null;  // Don't accept further meemContent
		}
	}
  }

/* ---------- Synchronous: MeemDefinitionClient ---------------------------- */

  public HashMap getMeemDefinition(  // used by sendContent()
    Filter filter)
    throws RemoteException{

    PigeonHole pigeonHole = new PigeonHole();

    HashMap meemDefinitions = null;

    MeemDefinitionClient meemDefinitionClient = (MeemDefinitionClient)
      meemCore.getTargetFor(new MeemDefinitionClientImpl(pigeonHole), MeemDefinitionClient.class);

    meemCore.getMeemStore().addOutboundReference(
      Reference.spi.create(
        "meemDefinitionClient", meemDefinitionClient, true, filter
      ), true
    );

    try {
      meemDefinitions = (HashMap) pigeonHole.get(timeout);
    }
    catch (TimeoutException timeoutException) {
      LogTools.error(
        logger,
        "Timeout: MeemStoreExporterWedge <- MeemStore: MeemDefinitionClient",
        timeoutException
      );
    }

    return(meemDefinitions);
  }

  private final class MeemDefinitionClientImpl
    implements MeemDefinitionClient, ContentClient {

    private PigeonHole pigeonHole;

    private HashMap meemDefinitions = new HashMap();

    public MeemDefinitionClientImpl(
      PigeonHole pigeonHole) {

      this.pigeonHole = pigeonHole;
    }

    public void meemDefinitionChanged(
      MeemPath       meemPath,
      MeemDefinition meemDefinition) {

      if (meemDefinitions != null) {
        meemDefinitions.put(meemPath, meemDefinition);
      }
    }

    public void contentSent() {
      if (meemDefinitions != null) {
		pigeonHole.put(meemDefinitions);
		meemDefinitions = null;  // Don't accept further meemDefinition
      }
    }

	public void contentFailed(String reason) {
	  if (meemDefinitions != null) {
	  	meemDefinitions.clear();
		pigeonHole.put(meemDefinitions);
		meemDefinitions = null;  // Don't accept further meemDefinition
	  }
	}
  }
  
/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

  private MeemDefinition meemDefinition = null;

  public MeemDefinition getMeemDefinition() {
    if (meemDefinition == null) {
      meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(
        new Class[] {this.getClass()}
      );
    }
    
    return(meemDefinition);
  }
}
