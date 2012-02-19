/*
 * @(#)LifeCycleProxy.java
 * Created on 2/05/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.*;


import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.system.manager.lifecycle.LifeCycleManager;
import org.openmaji.system.manager.lifecycle.LifeCycleManagerClient;



public class LifeCycleManagerProxy extends FacetProxy implements LifeCycleManager {
	
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(LifeCycleManager.class, "lifeCycleManager"), 
			new FacetOutboundSpecification(LifeCycleManagerClient.class, "lifeCycleManagerClient")
		);

	private HashMap meems = new HashMap();

	//=== Internal LifeCycleManager Client Implementation ===============================
	public static class LocalLifeCycleManagerClient implements LifeCycleManagerClient {
		LifeCycleManagerProxy outer;

		public LocalLifeCycleManagerClient(LifeCycleManagerProxy outer) {
			this.outer = outer;
		}
		
		public void meemCreated(final Meem meem, final String identifier) {
			outer.addMeem(meem, identifier);

			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireMeemCreated(meem, identifier);
					}
				});
		
		}

		public void meemDestroyed(final Meem meem) {
			outer.removeMeem(meem);

			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireMeemDestroyed(meem);
					}
				});
		
		}

		public void meemTransferred(final Meem meem, final LifeCycleManager targetLifeCycleManager) {
			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireMeemTransferred(meem, targetLifeCycleManager);
					}
				});
		
		}
	}

	private LifeCycleManagerClient lifeCycleManagerClient;

	public LifeCycleManagerProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}

	public LifeCycleManagerProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}

	public LifeCycleManager getLifeCycleManager() {
		return (LifeCycleManager) getInboundReference();
	}

	protected Facet getOutboundTarget() {
		if (lifeCycleManagerClient == null) {
			lifeCycleManagerClient = new LocalLifeCycleManagerClient(this);
		}
		return lifeCycleManagerClient;
	}

	protected void addMeem(Meem meem, String identifier) {
		meems.put(meem, identifier);
	}
	
	protected void removeMeem(Meem meem) {
		meems.remove(meem);
	}
	
	protected void clearContent() {
		if (meems != null) {
			meems.clear();
		}
		lifeCycleManagerClient = null;
	}
	
	//=== Client Management ======================================================
	
	private void fireMeemCreated(Meem meem, String identifier) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			LifeCycleManagerClient client = (LifeCycleManagerClient) clients[i];
			client.meemCreated(meem, identifier);
		}
	}
	
	private void fireMeemDestroyed(Meem meem) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			LifeCycleManagerClient client = (LifeCycleManagerClient) clients[i];
			client.meemDestroyed(meem);
		}
	}
	
	private void fireMeemTransferred(Meem meem, LifeCycleManager targetLifeCycleManager) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			LifeCycleManagerClient client = (LifeCycleManagerClient) clients[i];
			client.meemTransferred(meem, targetLifeCycleManager);
		}
	}

	protected void realizeClientContent(Object client) {
		LifeCycleManagerClient lifeCycleManagerClient = (LifeCycleManagerClient) client;
		
		for (Iterator iter = meems.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Map.Entry)iter.next();
			
			lifeCycleManagerClient.meemCreated((Meem)entry.getKey(), (String)entry.getValue());
		}
	}

	protected void clearClientContent(Object client) {
		LifeCycleManagerClient lifeCycleManagerClient = (LifeCycleManagerClient) client;
		
		for (Iterator iter = meems.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Map.Entry)iter.next();
			
			lifeCycleManagerClient.meemDestroyed((Meem)entry.getKey());
		}
	}

	//=== External LifeCycle Implementation ======================================

	public void createMeem(MeemDefinition meemDefinition, LifeCycleState initialLifeCycleState) throws IllegalArgumentException {
		if (isReadOnly())
			return;
		getLifeCycleManager().createMeem(meemDefinition, initialLifeCycleState);
	}

	public void destroyMeem(Meem meem) {
		if (isReadOnly())
			return;
		getLifeCycleManager().destroyMeem(meem);
	}

	public void transferMeem(Meem meem, LifeCycleManager targetLifeCycleManager) {
		if (isReadOnly())
			return;
		getLifeCycleManager().transferMeem(meem, targetLifeCycleManager);
	}
}