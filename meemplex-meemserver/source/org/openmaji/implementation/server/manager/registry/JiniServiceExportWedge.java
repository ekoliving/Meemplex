/*
 * @(#)JiniServiceExportWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.manager.registry;

import java.io.IOException;
import java.util.*;

import net.jini.admin.Administrable;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceDiscoveryManager;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.Direction;
import org.openmaji.meem.filter.FacetFilter;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.system.manager.registry.MeemRegistryClient;
import org.openmaji.system.meem.FacetClient;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.jini.ExportableServiceClient;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import com.sun.jini.admin.DestroyAdmin;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class JiniServiceExportWedge
    implements Wedge
{
	public MeemCore meemCore;

    public MeemRegistryClient   meemRegistryClientConduit = new MeemRegistryClientAdapter(this);
   
    private ExportableServiceClient exporterClient = new ExporterClientAdapter();
    
	private LookupDiscoveryManager lookupDiscoveryManager = null;

	private Configuration configuration = null;
    
    private final Set<MeemPath> processedMeems = Collections.synchronizedSet(new HashSet<MeemPath>());
    
	private final Map<String, ServiceID> exports = Collections.synchronizedMap(new HashMap<String, ServiceID>());

	private Map<String, JoinManager> joinManagers = new HashMap<String, JoinManager>();
	
	private LeaseRenewalManager leaseRenewalManager = null;
    
    private void initialize()
    {
        String majitekDirectory = System
                .getProperty(Common.PROPERTY_MAJI_HOME);

        if (majitekDirectory == null)
        {
            throw new RuntimeException("Empty Majitek directory property: "
                    + Common.PROPERTY_MAJI_HOME);
        }

        try
        {
            configuration = ConfigurationProvider
                    .getInstance(new String[] { majitekDirectory
                            + System
                                    .getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE) });
        }
        catch (ConfigurationException configurationException)
        {
            throw new RuntimeException("ConfigurationProviderException:"
                    + configurationException);
        }

        if (lookupDiscoveryManager == null)
        {
            try
            {
                lookupDiscoveryManager = new LookupDiscoveryManager(
                        new String[] { MeemSpace.getIdentifier() }, null, // LookupLocator[]
                        null // DiscoveryListener
                );
            }
            catch (IOException ioException)
            {
                throw new RuntimeException(
                        "LookupDiscoveryManager: IOException");
            }
        }

        if (leaseRenewalManager == null)
        {
            leaseRenewalManager = new LeaseRenewalManager();

            //        (LeaseRenewalManager) configuration.getEntry(
            //          "net.jini.lease.LeaseRenewalManager", // Component
            //          "leaseRenewalManager", // Name
            //          LeaseRenewalManager.class // Class
            //        );
        }
    }

    public static class FacetClientCallback implements FacetClient, ContentClient
    {
        private JiniServiceExportWedge  parent;
        private Meem                    meem;
        
        public FacetClientCallback(
            JiniServiceExportWedge  parent,
            Meem                    meem)
        {
            this.parent = parent;
            this.meem = meem;
        }
        
//        public void hasA(String facetIdentifer, Class<? extends Facet> specification, Direction direction)
//        {
//            //
//            // add a listener requesting initial content - we stay connected as the meem may be planning
//            // to publish a proxy some time in the future.
//            //
//            Reference exportableServiceClientReference = Reference.spi.create(
//                    "exportableServiceClientFacet", parent.meemCore.getLimitedTargetFor(
//                            parent.exporterClient, ExportableServiceClient.class), true);
//
//            meem.addOutboundReference(exportableServiceClientReference, false);
//    
//            parent.processedMeems.add(meem.getMeemPath());
//        }
        
        public void facetsAdded(FacetItem[] facetItems) {
        	if (facetItems != null && facetItems.length > 0) {
                //
                // add a listener requesting initial content - we stay connected as the meem may be planning
                // to publish a proxy some time in the future.
                //
                Reference exportableServiceClientReference = Reference.spi.create(
                        "exportableServiceClientFacet", parent.meemCore.getLimitedTargetFor(
                                parent.exporterClient, ExportableServiceClient.class), true);

                meem.addOutboundReference(exportableServiceClientReference, false);
        
                parent.processedMeems.add(meem.getMeemPath());
        	}
        }
        
        public void facetsRemoved(FacetItem[] facetItems) {
            // TODO Auto-generated method stub
        }
        

        /* (non-Javadoc)
         * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
         */
        public void contentSent()
        {
        }

        /* (non-Javadoc)
         * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
         */
        public void contentFailed(String reason)
        {
            LogTools.error(logger, "Can't determine whether or not meem: " + meem.getMeemPath() + " has a Jini Service");
        }
    }
    
    private class MeemRegistryClientAdapter
        implements MeemRegistryClient
        {
            JiniServiceExportWedge  parent;
            
            MeemRegistryClientAdapter(
                JiniServiceExportWedge  parent)
            {
                this.parent = parent;
            }
            
            /*
             * (non-Javadoc)
             * 
             * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemRegistered(org.openmaji.meem.Meem)
             */
            public void meemRegistered(Meem meem)
            {
                if (processedMeems.contains(meem.getMeemPath()))
                {
                    LogTools.error(logger,
                            "Attempt to process already processed meem: " + meem);
                    return;
                }
        
                if (lookupDiscoveryManager == null)
                    initialize();
                
                FacetFilter filter = new FacetFilter("exportableServiceClientFacet", ExportableServiceClient.class, Direction.OUTBOUND);
                
                Facet proxy = parent.meemCore.getTargetFor(new FacetClientCallback(parent, meem), FacetClient.class);
                final Reference meemReference = Reference.spi.create("facetClientFacet", proxy, true, filter);

                meem.addOutboundReference(meemReference, true);
            }
        
            /*
             * (non-Javadoc)
             * 
             * @see org.openmaji.system.manager.registry.MeemRegistryClient#meemDeregistered(org.openmaji.meem.Meem)
             */
            public void meemDeregistered(Meem meem)
            {
                MeemPath meemPath = meem.getMeemPath();
        
                JoinManager joinManager = (JoinManager)joinManagers.get(meemPath);
        
                if (joinManager != null)
                {
                    joinManager.terminate();
        
                    joinManagers.remove(meemPath);
        
                    // Supposed to use remoteMeemExporter.unexport() when deregistering
                    // remoteMeem ?
                }
        
                exports.remove(meemPath);
                processedMeems.remove(meem);
        
                //      leaseRenewalManager.cancel(serviceRegistration.getLease());
            }
        }

    public class ExporterClientAdapter implements ExportableServiceClient, ContentClient
    {
        private ServiceID createServiceID()
        {
            Uuid uuid = UuidFactory.generate();

            return new ServiceID(uuid.getMostSignificantBits(), uuid
                    .getLeastSignificantBits());
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.openmaji.system.meem.wedge.jini.ExportableServiceClient#serviceAdded(java.lang.String,
         *      java.lang.Object, java.util.List)
         */
        public void serviceAdded(String serviceId, Object service, List itemList)
        {
            try
            {
                ServiceID serviceID = createServiceID();

                exports.put(serviceId, serviceID);

                //  leaseRenewalManager.renewUntil(
                //    serviceRegistration.getLease(),
                //    Lease.ANY,
                //    null //LeaseListener
                //  );

                Entry[] entries = null;

                if (itemList != null)
                {
                    entries = (Entry[])itemList.toArray(new Entry[itemList.size()]);
                }

                try
                {
                    JoinManager joinManager = new JoinManager(service, // Object
                            entries, // Entry[] AttrSets
                            serviceID, // ServiceID
                            lookupDiscoveryManager, // LookupDiscoveryManager
                            leaseRenewalManager);

                    if (joinManagers.containsKey(serviceId) == false)
                    {
                        joinManagers.put(serviceId, joinManager);
                    }
                }
                catch (IOException ioException)
                {
                    throw new RuntimeException("JoinManager: IOException: "
                            + ioException);
                }
                catch (IllegalArgumentException illegalArgumentException)
                {
                    throw new RuntimeException("JoinManager: "
                            + illegalArgumentException + ", serviceID = "
                            + serviceID);
                }
            }
            catch (Exception exception)
            {
                LogTools.error(logger, "Exporting RemoteMeem: " + exception);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.openmaji.system.meem.wedge.jini.ExportableServiceClient#serviceRemoved(java.lang.String,
         *      java.lang.Object)
         */
        public void serviceRemoved(String serviceId, Object service)
        {
            JoinManager joinManager = (JoinManager)joinManagers.get(serviceId);

            if (joinManager != null) {
                try
                {
                    ServiceDiscoveryManager serviceDiscovery = new ServiceDiscoveryManager(
                            new LookupDiscovery(new String[] { MeemSpace.getIdentifier()}, configuration),
                            null, configuration);
                                                                                            
                    ServiceItem serviceItem = serviceDiscovery.lookup(
                        new ServiceTemplate((ServiceID)exports.get(serviceId), null, null),
                        null, 200L);
                    
                    //
                    // if we've got a "well behaved" service it should be Administrable
                    //
                    if (serviceItem != null && serviceItem.service instanceof Administrable)
                    {
                        DestroyAdmin destroyAdmin = (DestroyAdmin)((Administrable)serviceItem.service).getAdmin();

                        destroyAdmin.destroy();
                    }
                }
                catch (Exception e)
                {
                    LogTools.error(logger, "Exception removing Jini service with Administrable: " + e.toString());
                }

                joinManager.terminate();

                joinManagers.remove(serviceId);

                // Supposed to use remoteMeemExporter.unexport() when deregistering remoteMeem ?
            }

            exports.remove(serviceId);
        }

        /* (non-Javadoc)
         * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentSent()
         */
        public void contentSent()
        {
            // TODO Auto-generated method stub
        }

        /* (non-Javadoc)
         * @see org.openmaji.system.meem.wedge.reference.ContentClient#contentFailed(java.lang.String)
         */
        public void contentFailed(String reason)
        {
            // TODO Auto-generated method stub
            
        }
    }

    /* ---------- Logging fields ----------------------------------------------- */

    /**
     * Create the per-class Software Zoo Logging V2 reference.
     */

    private static final Logger logger = LogFactory.getLogger();
}