/*
 * @(#)ConfigurationHandlerProxy.java
 * Created on 11/12/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.openmaji.meem.Facet;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;


/**
 * <code>ConfigurationHandlerProxy</code> represents the client side 
 * <code>ConfigurationHandler</code> facet proxy.
 * <p>
 * @author Kin Wong
 */
public class ConfigurationHandlerProxy extends FacetProxy implements ConfigurationHandler {
	static public String ID_CONFIGURATION = ConfigurationHandlerProxy.class + ".configuration";
	static public String ID_LAST_REJECTED_REASON = ConfigurationHandlerProxy.class + ".last rejected reason";

	static public ConfigurationIdentifier ID_MEEM_IDENTIFIER = 
			new ConfigurationIdentifier("MeemSystemWedge", "meemIdentifier");
	
	static private FacetSpecificationPair defaultSpecs = 
		new FacetSpecificationPair(
			new FacetInboundSpecification(ConfigurationHandler.class, "configurationHandler"),
			new FacetOutboundSpecification(ConfigurationClient.class, "configClient"));

	private Map specificationMap;
	private Map valueMap;
	private Object lastRejectedReason = null;

	//=== Internal ConfigurationHandler Client Implementation ====================
	public static class LocalConfigurationClient implements ConfigurationClient {

		ConfigurationHandlerProxy	p;
		
		public LocalConfigurationClient(
			ConfigurationHandlerProxy	p)
		{
			this.p = p;
		}
		
		public void specificationChanged(final ConfigurationSpecification[] oldSpecifications, final ConfigurationSpecification[] newSpecifications) {
			
//			System.out.println("configurationAvailableChanged(" + p.getMeemClientProxy().getMeemPath().toString() + ")");

			if(p.specificationMap == null) p.specificationMap = new HashMap();
			if(p.valueMap == null) p.valueMap = new HashMap();
			
			// Removes old property specifications.
			if(oldSpecifications != null)
			for(int i=0; i < oldSpecifications.length; i++) {
				Assert.isNotNull(oldSpecifications[i]);
				ConfigurationSpecification propertySpecification = oldSpecifications[i];
				p.specificationMap.remove(propertySpecification.getIdentifier());
				p.valueMap.remove(propertySpecification.getIdentifier());
			}
			
			// Inserts new property specifications.
			if(newSpecifications != null)
			for(int i=0; i < newSpecifications.length; i++) {
				Assert.isNotNull(newSpecifications[i]);
				ConfigurationSpecification propertySpecification = 
					(ConfigurationSpecification)newSpecifications[i].clone();
				
				p.specificationMap.put(propertySpecification.getIdentifier(), propertySpecification);
				p.valueMap.put(propertySpecification.getIdentifier(), propertySpecification.getDefaultValue());
				
				//System.out.println("New Configuration Property Inserted: " + propertySpecification);
			}

			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireConfigurationAvaliableChanged(oldSpecifications, newSpecifications);
				}
			});
		}

		public void valueAccepted(final ConfigurationIdentifier id, final Serializable value) {
			if(!p.isDefined(id)) {
				//System.out.println("Specification NOT defined (" + id + "," + value + ")");
				return;
			} 
			
			//System.out.println("Specification is defined (" + id + "," + value + ")");
			if(value == null) return;
			
			p.valueMap.put(id, value);
			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireConfigurationAccepted(id, value);
				}
			});
		}

		public void valueRejected(final ConfigurationIdentifier id, final Serializable value, final Serializable reason) {
			p.lastRejectedReason = reason;

			if(p.containsClient())
			p.getSynchronizer().execute(new Runnable() {
				public void run() {
					p.fireConfigurationRejected(id, value, reason);
				}
			});
		}
	};
	
	private ConfigurationClient configurationHandlerClient;
	
	/**
	 * Constructs an instance of <code>ConfigurationHandlerProxy</code>.
	 * <p>
	 * @param meemClientProxy
	 */
	public ConfigurationHandlerProxy(MeemClientProxy meemClientProxy, FacetSpecificationPair specs) {
		super(meemClientProxy, specs);
	}
	
	/**
	 * Constructs an instance of <code>ConfigurationHandlerProxy</code>.<p>
	 * @param meemClientProxy
	 */
	public ConfigurationHandlerProxy(MeemClientProxy meemClientProxy) {
		this(meemClientProxy, defaultSpecs);
	}
	
	private ConfigurationHandler getConfigurationHandler() {
		return (ConfigurationHandler)getInboundReference();
	}
	
	public Object getLastRejectedReason() {
		return lastRejectedReason;
	}
		
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#getOutboundTarget()
	 */
	protected Facet getOutboundTarget() {
		if (configurationHandlerClient == null) {
			configurationHandlerClient = new LocalConfigurationClient(this);
		}

		return configurationHandlerClient;
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearContent()
	 */
	synchronized protected void clearContent() {
		if(valueMap != null) {
			valueMap.clear();
			valueMap = null;
		}
		if(specificationMap != null) {
			specificationMap.clear();
			specificationMap = null;
		}
		
		configurationHandlerClient = null;
	}

	
	/**
	 * Gets the specification (Type) of the configurable property from its name.
	 * <p>
	 * @return Class The class of the value of the configurable property.
	 */	
	public ConfigurationSpecification[] getSpecifications() {
		if(specificationMap == null) return new ConfigurationSpecification[0];
		return (ConfigurationSpecification[])specificationMap.values().toArray(new ConfigurationSpecification[0]);
	}
	
	/**
	 * Finds the specification by id.
	 * @param id A <code>ConfigurationIdentifier</code> the uniquely identifies
	 * the specification.
	 * @return <code>ConfigurationSpecification</code> uniquely identifies by the
	 * the id, or null if it is not found.
	 */	
	public ConfigurationSpecification findSpecification(ConfigurationIdentifier id) {
		if(specificationMap == null) return null;
		return (ConfigurationSpecification)specificationMap.get(id);
	}
	
	public ConfigurationSpecification getSpecification(Object id) {
		return (ConfigurationSpecification)specificationMap.get(id);
	}
	
	/**
	 * Gets the value of the configurable property from its name.
	 * <p>
	 * @param id The id of the configurable property.
	 * @return Object The value of the configurable property if it is defined,
	 * null otherwise.
	 */
	public Object getValue(Object id) {
		if(valueMap == null) {
			return null;
		}
		return valueMap.get(id);
	}
	
	/**
	 * Get whether a configurable property is defined.
	 * <p>
	 * @param id The id of the configurable property to check.
	 * @return true if the configurable property is defined, false otherwise.
	 */
	public boolean isDefined(Object id) {
		return specificationMap.containsKey(id);
	}
	
	/**
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#isEssential()
	 */
	protected boolean isEssential() {
		return true;
	}
	
	//=== Outbound methods client delegations ====================================
	private void fireConfigurationAvaliableChanged(
	ConfigurationSpecification[] oldSpecifications, ConfigurationSpecification[] newSpecifications) {
			Object[] clients = getClients();
			for(int i=0; i < clients.length; i++) {
				ConfigurationClient client = (ConfigurationClient)clients[i];
				client.specificationChanged(oldSpecifications, newSpecifications);
			}
	}
	
	private void fireConfigurationAccepted(ConfigurationIdentifier id, Serializable value) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			ConfigurationClient client = (ConfigurationClient)clients[i];
			client.valueAccepted(id, value);
		}
	}
		
	private void fireConfigurationRejected(ConfigurationIdentifier id, Serializable value, Serializable reason) {
		Object[] clients = getClients();
		for(int i=0; i < clients.length; i++) {
			ConfigurationClient client = (ConfigurationClient)clients[i];
			client.valueRejected(id, value, reason);
		}
	}
	//=== Client Management ======================================================
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#realizeClientContent(java.lang.Object)
	 */	
	synchronized protected void realizeClientContent(Object client) {
		ConfigurationClient configurationClient = (ConfigurationClient)client;
		configurationClient.specificationChanged(null, getSpecifications());
	}
	
	/* (non-Javadoc)
	 * @see org.openmaji.implementation.tool.eclipse.client.FacetProxy#clearClientContent(java.lang.Object)
	 */
	synchronized protected void clearClientContent(Object client) {
		ConfigurationClient configurationClient = (ConfigurationClient)client;
		configurationClient.specificationChanged(getSpecifications(), null);
	}


	//=== External ConfigurationHandler Implementation ===========================
	/* (non-Javadoc)
	 * @see org.openmaji.meem.aspect.wedge.configuration.ConfigurationHandler#configurationChanged(org.openmaji.meem.aspect.wedge.configuration.ConfigurationSpecification, java.lang.Object)
	 */
	public void valueChanged(final ConfigurationIdentifier id, final Serializable value) {
		if(isReadOnly()) return;

		getConfigurationHandler().valueChanged(id, value);
	}
	
}
