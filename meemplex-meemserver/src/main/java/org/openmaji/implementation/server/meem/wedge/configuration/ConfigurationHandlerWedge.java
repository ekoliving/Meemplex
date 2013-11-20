/*
 * @(#)ConfigurationHandlerWedge.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.configuration;

import java.io.Serializable;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationProvider;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.system.meem.wedge.reference.ContentException;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


/**
 * General property processing wedge
 */
public class ConfigurationHandlerWedge implements ConfigurationHandler, Wedge {

	/* ------------------------- outbound facets --------------------------- */

	/**
	 * Outbound facet for configuration
	 */
	public ConfigurationClient configClient;

	public final ContentProvider configClientProvider = new ContentProvider() {
		public void sendContent(Object target, Filter filter) throws ContentException {
			configurationProviderConduit.provideConfiguration((ConfigurationClient) target, filter);
		}
	};

	/* ------------------------------- conduits --------------------------------- */

	/**
	 * Conduit on which to receive changes of configuration values configuration of this meem.
	 */
	public ConfigurationClient configurationClientConduit = new ConfigurationClient() {
		public void specificationChanged(ConfigurationSpecification[] oldSpecifications, ConfigurationSpecification[] newSpecifications) {
			configClient.specificationChanged(oldSpecifications, newSpecifications);
		}

		public void valueAccepted(ConfigurationIdentifier id, Serializable value) {
			configClient.valueAccepted(id, value);
		}

		public void valueRejected(ConfigurationIdentifier id, Serializable value, Serializable reason) {
			configClient.valueRejected(id, value, reason);
		}
	};

	/**
	 * Conduit on which to send configuration update requests.
	 */
	public ConfigurationHandler configurationHandlerConduit;

	/**
	 * Conduit on which to send requests for providing configuration of this meem.
	 */
	public ConfigurationProvider configurationProviderConduit;


	/* ----------------------- ConfigurationHandler interface -------------------------- */
	
	/**
	 * 
	 */
	public void valueChanged(ConfigurationIdentifier id, Serializable obj) {
		configurationHandlerConduit.valueChanged(id, obj);
	}
}
