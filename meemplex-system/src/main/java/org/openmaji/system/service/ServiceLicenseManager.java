/*
 * Created on 3/08/2005
 */
package org.openmaji.system.service;

import org.openmaji.meem.Facet;

/**
 * @author Warren Bloomer
 *
 */
public interface ServiceLicenseManager extends Facet {

	/**
	 * Initiates a request to retrieve the available services for the user that
	 * is executing the current thread.
	 * 
	 * Results will be sent on the ServiceLicenseConnectionClient.availableServices() method.
	 */
	public void pollAvailableServices();

}
