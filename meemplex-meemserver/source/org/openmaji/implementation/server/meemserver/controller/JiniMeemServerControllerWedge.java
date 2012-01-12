/*
 * @(#)JiniMeemServerControllerWedge.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.meemserver.controller;

import java.io.Serializable;
import java.util.*;

import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookup;
import org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient;

import org.openmaji.meem.Meem;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.dependency.DependencyHandler;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClient;
import org.openmaji.meem.wedge.lifecycle.LifeCycleClientAdapter;
import org.openmaji.system.meem.FacetItem;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.meemserver.MeemServer;
import org.openmaji.system.space.Category;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class JiniMeemServerControllerWedge implements Wedge, JiniLookupClient {

	private static final Logger logger = LogFactory.getLogger();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public JiniLookup jiniLookupConduit;
	public JiniLookupClient jiniLookupClientConduit = this;
	
	public DependencyHandler dependencyHandlerConduit;

	public MeemCore meemCore;

	public Category categoryConduit;

	private Map<Meem, ConfigurationClientImpl> meems = Collections.synchronizedMap(new HashMap<Meem, ConfigurationClientImpl>());

	public void commence() {

		FacetItem facetItem = new FacetItem("meemServer", MeemServer.class.getName(), Direction.INBOUND);

		jiniLookupConduit.startLookup(facetItem, false);

		LogTools.info(logger, "MeemServer Jini lookup initiated ...");
	}

	public void conclude() {
		jiniLookupConduit.stopLookup();

		synchronized(meems) {
			for (Iterator iter = meems.values().iterator(); iter.hasNext();) {
				ConfigurationClientImpl configurationHandler = (ConfigurationClientImpl) iter.next();
				configurationHandler.stop();
			}
		}
	}

	/**
	 * @see org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient#meemAdded(org.openmaji.meem.Meem)
	 */
	public void meemAdded(Meem meem) {

		ConfigurationClientImpl configurationHandler = new ConfigurationClientImpl(meem);

		meems.put(meem, configurationHandler);

	}

	/**
	 * @see org.openmaji.implementation.server.nursery.jini.lookup.JiniLookupClient#meemRemoved(org.openmaji.meem.Meem)
	 */
	public void meemRemoved(Meem meem) {
		ConfigurationClientImpl configurationHandler = (ConfigurationClientImpl) meems.remove(meem);

		if (configurationHandler != null) {
			configurationHandler.stop();
		} 
	}

	public class ConfigurationClientImpl implements ConfigurationClient {

		private Meem meem;
		private String meemServerName;
		private boolean addedToCategory = false;
		private DependencyAttribute dependencyAttribute;

		public ConfigurationClientImpl(Meem meem) {
			this.meem = meem;

			ConfigurationClient proxy = (ConfigurationClient) meemCore.getTargetFor(this, ConfigurationClient.class);
			
			dependencyAttribute = new DependencyAttribute(DependencyType.WEAK, Scope.LOCAL, meem, "configClient");
			dependencyHandlerConduit.addDependency(proxy, dependencyAttribute, LifeTime.TRANSIENT);			
		}

		/**
		 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient#specificationChanged(org.openmaji.meem.wedge.configuration.ConfigurationSpecification[], org.openmaji.meem.wedge.configuration.ConfigurationSpecification[])
		 */
		public void specificationChanged(
			ConfigurationSpecification[] oldSpecifications,
			ConfigurationSpecification[] newSpecifications) {
			// don't care
		}
		
		/**
		 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient#valueAccepted(org.openmaji.meem.wedge.configuration.ConfigurationIdentifier, java.lang.Object)
		 */
		public void valueAccepted(ConfigurationIdentifier id, Serializable value) {
			if (id.getFieldName().equals("name")) {
				String newMeemServerName = (String) value;

				if (!addedToCategory) {
					LogTools.info(logger, "Adding " + newMeemServerName  + " : " + meem);
					categoryConduit.addEntry(newMeemServerName, meem);
				} else {
					LogTools.info(logger, "Renaming " + meemServerName  + " to  " + newMeemServerName);
					categoryConduit.renameEntry(meemServerName, newMeemServerName);
				}

				meemServerName = newMeemServerName;
			}
		
		}
		
		/**
		 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient#valueRejected(org.openmaji.meem.wedge.configuration.ConfigurationIdentifier, java.lang.Object, java.lang.Object)
		 */
		public void valueRejected(ConfigurationIdentifier id, Serializable value, Serializable reason) {
			// don't care		
		}


		public void stop() {
			LogTools.info(logger, "Removing " + meemServerName);
			categoryConduit.removeEntry(meemServerName);
			
			dependencyHandlerConduit.removeDependency(dependencyAttribute);
		}

	}

}
