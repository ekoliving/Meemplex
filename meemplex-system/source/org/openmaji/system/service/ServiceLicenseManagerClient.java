/*
 * Created on 3/08/2005
 */
package org.openmaji.system.service;

import java.security.Principal;
import java.util.Collection;

import org.openmaji.meem.Facet;


/**
 * @author Warren Bloomer
 *
 */
public interface ServiceLicenseManagerClient extends Facet {

	/**
	 * Provides a collection of services that are available for a particular user identity.
	 * 
	 * @param principal the principal for which services are available.
	 * @param services a collection of org.openmaji.cloud.system.client.ServiceStatus objects.
	 */
	public void availableServices(Principal principal, Collection services);

}
