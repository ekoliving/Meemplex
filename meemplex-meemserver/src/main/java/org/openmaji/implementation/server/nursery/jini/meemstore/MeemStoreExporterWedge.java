/*
 * @(#)MeemStoreExporterWedge.java
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
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.space.meemstore.MeemContentClient;
import org.openmaji.system.space.meemstore.MeemDefinitionClient;
import org.openmaji.system.space.meemstore.MeemStore;
import org.openmaji.system.space.meemstore.MeemStoreClient;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  private Map<MeemStoreCallBack, MeemStoreCallBack> meemStoreCallBacks = new WeakHashMap<MeemStoreCallBack, MeemStoreCallBack>();

  private static final Logger logger = Logger.getAnonymousLogger();

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
      logger.log(Level.WARNING, "Exporting MeemStoreExporterWedge: " + exception);
    }

    logger.log(Level.INFO, "Jini Service registered: " + uuid);

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

    logger.log(Level.INFO, "meemStored(" + meemPath + ")");

    Vector<MeemStoreCallBack>   zombies = new Vector<MeemStoreCallBack>();
    Iterator<MeemStoreCallBack> iterator = meemStoreCallBacks.values().iterator();

    while (iterator.hasNext()) {
      MeemStoreCallBack meemStoreCallBack = iterator.next();

      try {
        meemStoreCallBack.meemStored(meemPath);
      }
      catch (Exception exception) {
        logger.log(Level.WARNING, "MeemStoreCallBack removed due to Exception");

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

    logger.log(Level.INFO, "meemDestroyed(" + meemPath + ")");

    List<MeemStoreCallBack>   zombies = new ArrayList<MeemStoreCallBack>();
    Iterator<MeemStoreCallBack> iterator = meemStoreCallBacks.values().iterator();

    while (iterator.hasNext()) {
      MeemStoreCallBack meemStoreCallBack = (MeemStoreCallBack) iterator.next();

      try {
        meemStoreCallBack.meemDestroyed(meemPath);
      }
      catch (Exception exception) {
        logger.log(Level.WARNING, "MeemStoreCallBack removed due to Exception");

        zombies.add(meemStoreCallBack);
      }
    }

    if (zombies.isEmpty() == false) {
      iterator = zombies.iterator();

      while (iterator.hasNext()) meemStoreCallBacks.remove(iterator.next());
    }
  }

/* ---------- Inbound Facet: MeemContentClient ----------------------------- */

	public synchronized void meemContentChanged(MeemPath meemPath, MeemContent meemContent) {

		logger.log(Level.INFO, "meemContentChanged(" + meemPath + ")");

		List<MeemStoreCallBack> zombies = new ArrayList<MeemStoreCallBack>();
		for (MeemStoreCallBack meemStoreCallBack : meemStoreCallBacks.values()) {

			try {
				meemStoreCallBack.meemContentChanged(meemPath, meemContent);
			}
			catch (Exception exception) {
				logger.log(Level.WARNING, 
						"MeemStoreCallBack removed due to Exception");

				zombies.add(meemStoreCallBack);
			}
		}

		if (zombies.isEmpty() == false) {
			for (MeemStoreCallBack zombieCallback : zombies) {
				meemStoreCallBacks.remove(zombieCallback);
			}
		}
	}

/* ---------- Inbound Facet: MeemDefinitionClient -------------------------- */

  public synchronized void meemDefinitionChanged(
    MeemPath       meemPath,
    MeemDefinition meemDefinition) {

    logger.log(Level.INFO, "meemDefinitionChanged(" + meemPath + ")");

    List<MeemStoreCallBack>   zombies = new ArrayList<MeemStoreCallBack>();
    
    for (MeemStoreCallBack meemStoreCallBack : meemStoreCallBacks.values()) {
      try {
        meemStoreCallBack.meemDefinitionChanged(meemPath, meemDefinition);
      }
      catch (Exception exception) {
        logger.log(Level.WARNING, "MeemStoreCallBack removed due to Exception");

        zombies.add(meemStoreCallBack);
      }
    }

    for (MeemStoreCallBack zombie : zombies) {
      meemStoreCallBacks.remove(zombie);
    }
  }

/* ---------- Interface: MeemStoreCallForward ------------------------------ */

  public synchronized void addMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException {

    logger.log(Level.INFO, "addMeemStoreCallBack(" + meemStoreCallBack + ")");

    meemStoreCallBacks.put(meemStoreCallBack, null);
  }

  public synchronized void removeMeemStoreCallBack(
    MeemStoreCallBack meemStoreCallBack)
    throws RemoteException {

    logger.log(Level.INFO, "removeMeemStoreCallBack(" + meemStoreCallBack + ")");

    meemStoreCallBacks.remove(meemStoreCallBack);
  }

/* ---------- Interface: MeemStore ----------------------------------------- */

  public void storeMeemContent(
    MeemPath meemPath,
    MeemContent meemContent)
    throws RemoteException {

    if (meemStore == null) {
      logger.log(Level.WARNING,
        "storeMeemContent() invoked, but 'meemStore' not connected"
      );
    }
    else {
      logger.log(Level.INFO, "storeMeemContent(" + meemPath + ")");

      meemStore.storeMeemContent(meemPath, meemContent);
    }
  }

  public void storeMeemDefinition(
    MeemPath       meemPath,
    MeemDefinition meemDefinition)
    throws RemoteException {

    if (meemStore == null) {
      logger.log(Level.WARNING,
        "storeMeemDefinition() invoked, 'meemStore' not connected"
      );
    }
    else {
      logger.log(Level.INFO, "storeMeemDefinition(" + meemPath + ")");

      meemStore.storeMeemDefinition(meemPath, meemDefinition);
    }
  }

  public void destroyMeem(
    MeemPath meemPath)
    throws RemoteException {

    if (meemStore == null) {
      logger.log(Level.WARNING,
         "destroyMeem() invoked, but 'meemStore' not connected"
      );
    }
    else {
      logger.log(Level.INFO, "destroyMeem(" + meemPath + ")");

      meemStore.destroyMeem(meemPath);
    }
  }

/* ---------- Synchronous: MeemContentClient ------------------------------- */

  public Map<MeemPath,MeemContent> getMeemContent(  // used by sendContent()
    Filter filter)
    throws RemoteException{

    PigeonHole<Map<MeemPath,MeemContent>> pigeonHole = new PigeonHole<Map<MeemPath,MeemContent>>();

    Map<MeemPath,MeemContent> meemContents = null;

    MeemContentClient meemContentClient = (MeemContentClient)
      meemCore.getTargetFor(new MeemContentClientImpl(pigeonHole), MeemContentClient.class);

    meemCore.getMeemStore().addOutboundReference(
      Reference.spi.create("meemContentClient", meemContentClient, true, filter),
      true
    );

    try {
      meemContents = pigeonHole.get(timeout);
    }
    catch (ContentException ce) {
        logger.log(Level.WARNING,
          "ContentException: MeemStoreExporterWedge <- MeemStore: MeemContentClient",
          ce
        );
    }
    catch (TimeoutException timeoutException) {
      logger.log(Level.WARNING,
        "Timeout: MeemStoreExporterWedge <- MeemStore: MeemContentClient",
        timeoutException
      );
    }

    return(meemContents);
  }

  private final class MeemContentClientImpl
    implements MeemContentClient, ContentClient {

    private PigeonHole<Map<MeemPath,MeemContent>> pigeonHole;

    private Map<MeemPath,MeemContent> meemContents = new HashMap<MeemPath,MeemContent>();

    public MeemContentClientImpl(PigeonHole<Map<MeemPath,MeemContent>> pigeonHole) {
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

  public Map<MeemPath,MeemDefinition> getMeemDefinition(  // used by sendContent()
    Filter filter)
    throws RemoteException{

    PigeonHole<Map<MeemPath,MeemDefinition>> pigeonHole = new PigeonHole<Map<MeemPath,MeemDefinition>>();

    Map<MeemPath,MeemDefinition> meemDefinitions = null;

    MeemDefinitionClient meemDefinitionClient = (MeemDefinitionClient)
      meemCore.getTargetFor(new MeemDefinitionClientImpl(pigeonHole), MeemDefinitionClient.class);

    meemCore.getMeemStore().addOutboundReference(
      Reference.spi.create(
        "meemDefinitionClient", meemDefinitionClient, true, filter
      ), true
    );

    try {
      meemDefinitions = pigeonHole.get(timeout);
    }
    catch (ContentException ce) {
        logger.log(Level.WARNING,
          "Content Problem: MeemStoreExporterWedge <- MeemStore: MeemDefinitionClient",
          ce
        );
      }
    catch (TimeoutException timeoutException) {
      logger.log(Level.WARNING,
        "Timeout: MeemStoreExporterWedge <- MeemStore: MeemDefinitionClient",
        timeoutException
      );
    }

    return(meemDefinitions);
  }

  private final class MeemDefinitionClientImpl
    implements MeemDefinitionClient, ContentClient {

    private PigeonHole<Map<MeemPath,MeemDefinition>> pigeonHole;

    private Map<MeemPath,MeemDefinition> meemDefinitions = new HashMap<MeemPath,MeemDefinition>();

    public MeemDefinitionClientImpl(PigeonHole<Map<MeemPath,MeemDefinition>> pigeonHole) {
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
