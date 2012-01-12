/*
 * @(#)ExportableServiceClient.java
 * 
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.jini;

import java.util.List;

import org.openmaji.meem.Facet;


/**
 * A symmetrical facet for monitoring the adding and removal of Jini services with the wedge supporting the Exporter
 * facet.
 */
public interface ExportableServiceClient
    extends Facet
{
    /**
     * Register a service as added.
     * 
     * @param serviceID the Jini serviceID to use.
     * @param service the proxy for the object providing the service.
     * @param itemList a Jini item list.
     */
    public void serviceAdded(String serviceID, Object service, List itemList);

    /**
     * Register a service as removed.
     * 
     * @param serviceID an identifier for the service which should be unique within the meem.
     * @param service the proxy for the object that was providing the service.
     */
    public void serviceRemoved(String serviceID, Object service);
}
