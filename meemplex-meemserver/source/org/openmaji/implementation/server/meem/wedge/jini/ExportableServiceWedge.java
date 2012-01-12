/*
 * @(#)ExportableServiceWedge.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.jini;

import java.rmi.Remote;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openmaji.implementation.server.manager.registry.jini.ExporterHelper;

import org.openmaji.meem.*;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meem.wedge.jini.ExportableServiceClient;
import org.openmaji.system.meem.wedge.jini.ExportableServiceConduit;
import org.openmaji.system.meem.wedge.jini.Exporter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;



public class ExportableServiceWedge 
    implements Exporter, Wedge
{
    Map    services = new HashMap();
    
    public ExportableServiceConduit  exportableServiceConduit = new ExportableServiceClientAdapter(this);
    
    private class ServiceEntry
    {
        final Remote    service;
        final List      itemList;
        
        ServiceEntry(
            Remote  service,
            List    itemList)
        {
            this.service = service;
            this.itemList = itemList;
        }
    }
    
    private class ExportableServiceClientAdapter
        implements ExportableServiceConduit
    {
        ExportableServiceWedge  parent;
        
        ExportableServiceClientAdapter(
            ExportableServiceWedge parent)
        {
            this.parent = parent;
        }

        public void serviceAdded(String serviceID, Remote service, List itemList)
        {
            parent.services.put(serviceID, new ServiceEntry(service, itemList));
            
            doServiceAdded(exportableServiceClientFacet, serviceID, service, itemList);
        }

        /* (non-Javadoc)
         * @see org.openmaji.implementation.server.meem.wedge.jini.ExportableServiceClient#serviceRemoved(java.lang.String)
         */
        public void serviceRemoved(String serviceID)
        {
            ServiceEntry    entry = (ServiceEntry)parent.services.get(serviceID);
            
            exportableServiceClientFacet.serviceRemoved(serviceID, entry.service);
        }
    }
    
    /**
     * Internal reference to MeemCore
     */
    public MeemCore meemCore;

    /**
     * RemoteMeemClient (out-bound Facet)
     */

    public ExportableServiceClient exportableServiceClientFacet;
    public final ContentProvider   exportableServiceClientFacetProvider = new ContentProvider() 
                                                                   {
                                                                        public synchronized void sendContent(
                                                                            Object target,
                                                                            Filter filter)
                                                                        {
                                                                            Iterator    it = services.keySet().iterator();
                                                                            while (it.hasNext())
                                                                            {
                                                                                String          id = (String)it.next();
                                                                                ServiceEntry    entry = (ServiceEntry)services.get(id);

                                                                                doServiceAdded((ExportableServiceClient)target, id, entry.service, entry.itemList);
                                                                            }
                                                                        }
                                                                   };

    private void doServiceAdded(
        ExportableServiceClient     client,
        String                      id,
        Remote                      service,
        List                        itemList)
    {
        client.serviceAdded(id, ExporterHelper.export(service), itemList);
    }

}
