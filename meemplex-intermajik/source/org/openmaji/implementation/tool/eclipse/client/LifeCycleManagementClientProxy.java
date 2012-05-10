/*
 * @(#)LifeCycleManagementClientProxy.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.client;

import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.meem.wedge.lifecycle.LifeCycleManagementClient;



/**
 * @author mg
 */
public class LifeCycleManagementClientProxy extends FacetProxy {

	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetOutboundSpecification(LifeCycleManagementClient.class, "lifeCycleManagementClient")
		);
	
	LifeCycleManager parentLCM = null;
	Meem meem = null;
	
	//=== Internal  LifeCycleManagement Client Implementation ===============================
	public static class LocalLifeCycleManagementClient implements  LifeCycleManagementClient {
		LifeCycleManagementClientProxy outer;

		public LocalLifeCycleManagementClient(LifeCycleManagementClientProxy outer) {
			this.outer = outer;
		}

		public void parentLifeCycleManagerChanged(final Meem meem, final LifeCycleManager lifeCycleManager) {

			outer.parentLCM = (LifeCycleManager) SecurityManager.getInstance().getGateway().getTarget(lifeCycleManager, LifeCycleManager.class);
			outer.meem = meem;

			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireParentLifeCycleManagerChanged(meem, lifeCycleManager);
					}
				});
			
			outer.setContentInitialize(true);
		}

	}
	
	public LifeCycleManagementClientProxy(MeemClientProxy meemClientProxy) {
		super(meemClientProxy, defaultSpecs);
	}
	
	private LocalLifeCycleManagementClient lifeCycleManagementClient = null;
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */
	protected void clearClientContent(Object client) {
		// -mg- Auto-generated method stub
	}
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	protected void clearContent() {
		lifeCycleManagementClient = null;
		setContentInitialize(false);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		if (lifeCycleManagementClient == null) {
			lifeCycleManagementClient = new LocalLifeCycleManagementClient(this);
		}

		return lifeCycleManagementClient;
	}
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */
	protected void realizeClientContent(Object client) {
		LifeCycleManagementClient lifeCycleManagementClient = (LifeCycleManagementClient) client;
		lifeCycleManagementClient.parentLifeCycleManagerChanged(meem, parentLCM);
	}
	
	public LifeCycleManager getParentLifeCycleManager() {
		return parentLCM;
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#isEssential()
	 */
	protected boolean isEssential() {
		return true;
	}
	
	//=== Client Management ======================================================	
	private void fireParentLifeCycleManagerChanged(Meem meem, LifeCycleManager lifeCycleManager) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			LifeCycleManagementClient client = (LifeCycleManagementClient) clients[i];
			client.parentLifeCycleManagerChanged(meem, lifeCycleManager);
		}
	}
}
