/*
 * @(#)SubsystemProxy.java
 * Created on 12/05/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.util.HashMap;
import java.util.Map;

import org.openmaji.meem.Facet;
import org.openmaji.meem.Meem;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.system.manager.lifecycle.subsystem.CommissionState;
import org.openmaji.system.manager.lifecycle.subsystem.MeemDescription;
import org.openmaji.system.manager.lifecycle.subsystem.Subsystem;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemClient;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemState;


/**
 * <code>SubsystemProxy</code>.
 * <p>
 * 
 * @author Kin Wong
 */
public class SubsystemProxy extends MeemSetProxy implements Subsystem {

	private Map meemDefinitions = new HashMap();

	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(Subsystem.class, "subsystem"), 
			new FacetOutboundSpecification(SubsystemClient.class, "subsystemClient")
		);

	//=== Internal SubsystemClient Implementation ================================
	public static class LocalSubsystemClient implements SubsystemClient {

		private SubsystemProxy outer;

		public LocalSubsystemClient(SubsystemProxy outer) {
			this.outer = outer;
		}

		public void subsystemStateChanged(final SubsystemState subsystemState) {
			outer.subsystemState = subsystemState;

			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireSubsystemStateChanged(subsystemState);
					}
				});
		}

		public void commissionStateChanged(final CommissionState commissionState) {
			outer.commissionState = commissionState;

			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireCommissionStateChanged(commissionState);
					}
				});
		}

		public void meemCreated(final Meem meem, final MeemDefinition meemDefinition) {
			outer.addMeem(meem);
			outer.meemDefinitions.put(meem, meemDefinition);

			if (outer.containsClient())
				outer.getSynchronizer().execute(new Runnable() {
					public void run() {
						outer.fireMeemCreated(meem, meemDefinition);
					}
				});
		}

		public void meemsAvailable(MeemDefinition[] meemDefinitions, MeemDescription[] meemDescriptions) {
		}

	}

	private SubsystemClient subsystemClient = null;

	private SubsystemState subsystemState = SubsystemState.STOPPED;
	private CommissionState commissionState = CommissionState.NOT_COMMISSIONED;

	/**
	 * Constructs an instance of <code>SubsystemProxy</code>.
	 * <p>
	 * 
	 * @param meemClientProxy
	 * @param specs
	 */
	public SubsystemProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}

	public SubsystemProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}

	private Subsystem getSubsystem() {
		return (Subsystem) getInboundReference();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		if (subsystemClient == null)
			subsystemClient = new LocalSubsystemClient(this);
		return subsystemClient;
	}

	public boolean isStarted() {
		return subsystemState.equals(SubsystemState.STARTED);
	}
	
	public boolean isCommissioned() {
		return commissionState.equals(CommissionState.COMMISSIONED);
	}

	//=== Client Management ======================================================

	private void fireMeemCreated(Meem meem, MeemDefinition meemDefinition) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			SubsystemClient client = (SubsystemClient) clients[i];
			client.meemCreated(meem, meemDefinition);
		}
	}

	private void fireSubsystemStateChanged(SubsystemState subsystemState) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			SubsystemClient client = (SubsystemClient) clients[i];
			client.subsystemStateChanged(subsystemState);
		}
	}
	
	private void fireCommissionStateChanged(CommissionState commissionState) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			SubsystemClient client = (SubsystemClient) clients[i];
			client.commissionStateChanged(commissionState);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */
	protected void realizeClientContent(Object client) {
		SubsystemClient subsystemClient = (SubsystemClient) client;
		Meem[] meems = getMeems();
		for (int i = 0; i < meems.length; i++) {
			subsystemClient.meemCreated(meems[i], (MeemDefinition) meemDefinitions.get(meems[i]));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */
	protected void clearClientContent(Object client) {
		//SubsystemClient subsystemClient = (SubsystemClient) client;
		Meem[] meems = getMeems();
		for (int i = 0; i < meems.length; i++) {
			//			subsystemClient.meemRemoved(meems[i]);
		}
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.MeemSetProxy#clearContent()
	 */
	protected void clearContent() {
		subsystemClient = null;
		super.clearContent();
	}

	//=== External Subsystem Implementation ======================================

	public void changeSubsystemState(SubsystemState state) {
		if (isReadOnly())
			return;
		getSubsystem().changeSubsystemState(state);
	}

	public void changeCommissionState(CommissionState state) {
		if (isReadOnly())
			return;
		getSubsystem().changeCommissionState(state);
	}

	public void createMeem(MeemDefinition meemDefinition, MeemDescription meemDescription) {
		if (isReadOnly())
			return;
		getSubsystem().createMeem(meemDefinition, meemDescription);
	}

}