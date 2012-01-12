/*
 * @(#)SubsystemFactoryProxy.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
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
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory;
import org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient;


/**
 * @author Peter
 */
public class SubsystemFactoryProxy extends MeemSetProxy implements SubsystemFactory {
	private LocalSubsystemFactoryClient subsystemFactoryClient = null;
	
	private Map meemDefinitions = new HashMap();

	private static FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(SubsystemFactory.class, "subsystemFactory"), 
			new FacetOutboundSpecification(SubsystemFactoryClient.class, "subsystemFactoryClient")
		);

	public static class LocalSubsystemFactoryClient implements SubsystemFactoryClient {
		public LocalSubsystemFactoryClient(SubsystemFactoryProxy subsystemFactoryProxy) {
			this.subsystemFactoryProxy = subsystemFactoryProxy;
		}

		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#subsystemCreated(org.openmaji.meem.Meem,
		 *      org.openmaji.meem.definition.MeemDefinition)
		 */
		public void subsystemCreated(final Meem meem, final MeemDefinition meemDefinition) {
//					System.err.println("SubsystemFactoryProxy.subsystemCreated: " + meem);
			subsystemFactoryProxy.addMeem(meem);
			subsystemFactoryProxy.meemDefinitions.put(meem, meemDefinition);
			
			if (subsystemFactoryProxy.containsClient()) {
				Runnable task = new Runnable() {
					public void run() {
						subsystemFactoryProxy.fireSubsystemCreated(meem, meemDefinition);
					}
				};

				subsystemFactoryProxy.getSynchronizer().execute(task);
			}

		}

		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#subsystemDestroyed(org.openmaji.meem.Meem)
		 */
		public void subsystemDestroyed(final Meem meem) {
			//		System.err.println("SubsystemFactoryProxy.subsystemRemoved: " + subsystemMeem);
			subsystemFactoryProxy.removeMeem(meem);
			subsystemFactoryProxy.meemDefinitions.remove(meem);

			if (subsystemFactoryProxy.containsClient()) {
				Runnable task = new Runnable() {
					public void run() {
						subsystemFactoryProxy.fireSubsystemDestroyed(meem);
					}
				};

				subsystemFactoryProxy.getSynchronizer().execute(task);
			}
		}

		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#definitionsAdded(org.openmaji.meem.definition.MeemDefinition[])
		 */
		public void definitionsAdded(MeemDefinition[] meemDefinitions) {
			// -mg- Auto-generated method stub
		}

		/**
		 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactoryClient#definitionsRemoved(org.openmaji.meem.definition.MeemDefinition[])
		 */
		public void definitionsRemoved(MeemDefinition[] meemDefinitions) {
			// -mg- Auto-generated method stub
		}

		private final SubsystemFactoryProxy subsystemFactoryProxy;
	}

	/**
	 * Constructs an instance of <code>SubsystemFactoryProxy</code>.
	 * <p>
	 * 
	 * @param meemClientProxy
	 */
	protected SubsystemFactoryProxy(MeemClientProxy meemClientProxy) {
		super(meemClientProxy, defaultSpecs);
	}

	/**
	 * Constructs an instance of <code>SubsystemFactoryProxy</code>.
	 * <p>
	 * 
	 * @param meemClientProxy
	 * @param specs
	 */
	protected SubsystemFactoryProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}

	/**
	 * Gets all the subsystems from this SubsystemFactoryProxy.
	 * <p>
	 * 
	 * @return All the subsystems
	 */
	public Meem[] getSubsystems() {
		return getMeems();
	}

	protected Facet getOutboundTarget() {
		if (subsystemFactoryClient == null) {
			subsystemFactoryClient = new LocalSubsystemFactoryClient(this);
		}
		return subsystemFactoryClient;
	}

	//=== Client Management ======================================================
	private void fireSubsystemCreated(Meem subsystemMeem, MeemDefinition meemDefinition) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			SubsystemFactoryClient client = (SubsystemFactoryClient) clients[i];
			client.subsystemCreated(subsystemMeem, meemDefinition);
		}
	}

	private void fireSubsystemDestroyed(Meem subsystemMeem) {
		Object[] clients = getClients();
		for (int i = 0; i < clients.length; i++) {
			SubsystemFactoryClient client = (SubsystemFactoryClient) clients[i];
			client.subsystemDestroyed(subsystemMeem);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */
	protected void realizeClientContent(Object client) {
		SubsystemFactoryClient subsystemManagerClient = (SubsystemFactoryClient) client;

		Meem[] meems = getMeems();
		for (int i = 0; i < meems.length; i++) {
			subsystemManagerClient.subsystemCreated(meems[i], (MeemDefinition)meemDefinitions.get(meems[i]));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */
	protected void clearClientContent(Object client) {
		SubsystemFactoryClient subsystemManagerClient = (SubsystemFactoryClient) client;

		Meem[] meems = getMeems();
		for (int i = 0; i < meems.length; i++) {
			subsystemManagerClient.subsystemDestroyed(meems[i]);
		}
	}

	//=== External SubsystemManager Implementation ===============================

	/**
	 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory#createSubsystem(org.openmaji.meem.definition.MeemDefinition)
	 */
	public void createSubsystem(MeemDefinition meemDefinition) {
		if (isReadOnly())
			return;

		SubsystemFactory subsystemFactory = (SubsystemFactory) getInboundReference();
		subsystemFactory.createSubsystem(meemDefinition);
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory#destroySubsystem(org.openmaji.meem.Meem)
	 */
	public void destroySubsystem(Meem meem) {
		if (isReadOnly())
			return;

		SubsystemFactory subsystemFactory = (SubsystemFactory) getInboundReference();
		subsystemFactory.destroySubsystem(meem);
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory#addSubsystemDefinition(org.openmaji.meem.definition.MeemDefinition)
	 */
	public void addSubsystemDefinition(MeemDefinition meemDefinition) {
		// -mg- Auto-generated method stub
	}

	/**
	 * @see org.openmaji.system.manager.lifecycle.subsystem.SubsystemFactory#removeSubsystemDefinition(org.openmaji.meem.definition.MeemDefinition)
	 */
	public void removeSubsystemDefinition(MeemDefinition meemDefinition) {
		// -mg- Auto-generated method stub
	}

}