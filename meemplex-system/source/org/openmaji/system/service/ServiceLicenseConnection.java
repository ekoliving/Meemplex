/*
 * @(#)ServiceLicenseRequest.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.service;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemPath;


/**
 * <p>This is the facet implemented by client components and the service license manager to connect
 * and disconnect services, and their associated licenses and billing events throughout the system.
 * Components calling this interface are required to be already authenticated with the remote
 * service provider using the <code>SRMProviderConnection</code> interface, which will set the 
 * appropriate credentials in the executing thread context.</p>
 */

public interface ServiceLicenseConnection extends Facet {

	/**
	 * <p>This method is called by a component to initiate a service connection to either itself, or another
	 * component on its behalf, specified by the MeemPath. It invokes an action on the service
	 * license manager of the meemspace to download a license as required, and to connect the 
	 * meem component to the service of the given name, and the user set in the executing thread context.</p>
	 * 
	 * <p>Once the service is connected, the user will be billed for use of the service according to
	 * the business model and business rules set up by the service provider until the service is 
	 * disconnected.</p>
	 * 
	 * <p>A message is returned on the <code>ServiceLicenseConnectionClient</code> which 
	 * informs the initiating wedge as to the result of the call.</p>
	 * 
	 * <p>This version of the service manager only allows a single meem to be conneccted to a service 
	 * for a given user, although this restriction may be lifted in future releases.</p>
	 * 
	 * @param serviceType The type of the service which to activate.
	 * @param meemPath The meem component to bind to the service.
	 * @param planKey An identifier for the service plan to connect to. 
	 */
	
	public void connectService(String serviceType, MeemPath meemPath, String planKey);

	/**
	 * Get authorisation for use of a service.  If successful, the service may
	 * be commited to by calling commitService.
	 * 
	 * @param serviceType
	 * @param meemPath
	 * @param planKey
	 */
	
	public void authoriseService(String serviceType, MeemPath meemPath, String planKey);

	/**
	 * Commit to an authorised service.  This will essentially "connect" to the service.
	 * 
	 * @param serviceType
	 * @param meemPath
	 */
	
	public void commitService(String serviceType, MeemPath meemPath);

	/**
	 * <p>This method is called by a component to disconnect a service to either itself, or another
	 * component on its behalf, specified by the MeemPath. It invokes an action on the service
	 * license manager of the meemspace relinquish the license as required, and to unbind the 
	 * meem component from the service of the given name, and the user set in the executing thread context.</p>
	 * 
	 * <p>Once the service is disconnected, the billing of the service will cease according to
	 * the business model and business rules set up by the service provider.</p>
	 * 
	 * <p>A message is returned on the <code>ServiceLicenseConnectionClient</code> which 
	 * informs the initiating wedge as to the result of the call.</p>
	 * 
	 * <p>This version of the service manager only allows a single meem to be conneccted to a service 
	 * for a given user, although this restriction may be lifted in future releases.</p>
	 * 
	 * @param serviceType The type of the service to disconnect.
	 * @param meemPath The meem component to bind to the service.
	 */

	public void disconnectService(String serviceType, MeemPath meemPath);
	
	/**
	 * <p>This method is called by a component to unbind the given meem component from a connected service.
	 * This can only be successfully executed subsequent to a call to <code>connectService</code>, and
	 * will result in an error on the client facet if there is no correspoding connected service.</p>
	 * 
	 * <p>Typically, this method would be called to unbind the current component to the service connection,
	 * and subsequently rebind it to a new component via the <code>rebindService</code> method. For example,
	 * if a service is consumed on one device, and then is requested to be tranferred to another, one would
	 * first call the unbindService method, and subsequently call the rebindService method with the MeemPath
	 * of the newly bound device component.</p>
	 * 
	 * @param serviceType The type of the already connected service
	 * @param meemPath The meem component to unbind from the connected service
	 */
	
	public void unbindService(String serviceType, MeemPath meemPath);
	
	/**
	 * <p>This method is called by a component to rebind the given meem component to an existing connected
	 * service. This can only be successfully executed subsequent to a call to <code>connectService</code>, and
	 * will result in an error on the client facet if there is no correspoding connected service.</p>
	 * 
	 * <p>Typically, this method would be called in conjunction with the unbindService method to release the service
	 * to component binding, and subsequently rebind it to a new component via the <code>rebindService</code> method. 
	 * For example, if a service is consumed on one device, and then is requested to be tranferred to another, one would
	 * first call the unbindService method, and subsequently call the rebindService method with the MeemPath
	 * of the newly bound device component.</p>
	 *
	 * @param serviceType The type of the already connected service
	 * @param meemPath The meem component to rebind to the connected service
	 */
	public void rebindService(String serviceType, MeemPath meemPath);

	/**
	 * <p>This method is called from the service wedge on a component to upgrade the service to a 
	 * a new service grade within the same service group. Before invoking this method, information on what upgrade
	 * options are available for the given service should be first ascertained via the
	 * <code>SRMProviderConnection</code> interface.</p>
	 * 
	 * <p>A message is returned on the <code>ServiceLicenseConnectionClient</code> which 
	 * informs the initiating wedge as to the result of the call.</p>
	 * 
	 * @param oldServiceType The identifier of the service to be upgraded
	 * @param newServiceType The identifier of the new upgraded service
	 */
	
	public void upgradeService(String oldServiceType, String newServiceType);

	/**
	 * <p>This method is called from the service wedge on a component to temporarily upgrade the service to a 
	 * a new service grade within the same service group. Before invoking this method, information on what upgrade
	 * options are available for the given service should be first ascertained via the
	 * <code>SRMProviderConnection</code> interface.</p>
	 * 
	 * <p>A message is returned on the <code>ServiceLicenseConnectionClient</code> which 
	 * informs the initiating wedge as to the result of the call.</p>
	 * 
	 * @param oldServiceType The identifier of the service to be upgraded
	 * @param newServiceType The identifier of the new upgraded service
	 * @param milliseconds The amount of time to upgrade the service for.  
	 */
	public void upgradeService(String oldServiceType, String newServiceType, long milliseconds);
	
	/**
	 * <p>This method is called by a third party on a component to downgrade the service to a 
	 * a new service grade. Before invoking this method, information on what downgrade
	 * options are available for the given service should be first ascertained via the
	 * <code>SRMProviderConnection</code> interface.</p>
	 * 
	 * <p>A message is returned on the <code>ServiceLicenseConnectionClient</code> which 
	 * informs the initiating wedge as to the result of the call.</p>
	 * 
	 * @param oldServiceType The identifier of the service to be downgraded
	 * @param newServiceType The identifier of the new downgraded service
	 */
	
	public void downgradeService(String oldServiceType, String newServiceType);
		
}
