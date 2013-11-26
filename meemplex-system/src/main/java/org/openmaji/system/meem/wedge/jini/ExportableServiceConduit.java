/*
 * @(#)ExportableServiceClient.java
 * 
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.jini;

import java.rmi.Remote;
import java.util.List;

/**
 * General conduit to provide a mechanism for export and unexporting objects to a Jini lookup service.
 */
public interface ExportableServiceConduit
{
    /**
     * Register a service as added.
     * 
     * @param serviceID an identifier for the service which should be unique within the meem server.
     * @param service the object providing the service.
     * @param itemList a list of Jini Entry objects.
     */
    public void serviceAdded(String serviceID, Remote service, List<?> itemList);

    /**
     * Register a service as removed.
     * 
     * @param serviceID an identifier for the service which should be unique within the meem server.
     */
    public void serviceRemoved(String serviceID);
}
