/*
 * @(#)ServiceLicenseResponse.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.service;

import org.openmaji.meem.Facet;

/**
 * <p>A client facet which notifies a connected service component of all service events,
 * including connection/disconnection, rights expiry, license lease renewals, and errors.</p>
 */

public interface ServiceLicenseConnectionClient extends Facet {
	
	/**
	 * <p>The event interface which provides the client with events surrounding the status of the 
	 * service connection.</p>
	 * 
	 * @param event The event from the ServiceLicenseManager 
	 */
	
	public void serviceLicenseEvent(ServiceRightsEvent event);

}
