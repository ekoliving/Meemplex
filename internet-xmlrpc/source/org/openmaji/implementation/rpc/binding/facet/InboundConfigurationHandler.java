/*
 * @(#)InboundConfigurationHandler.java
 * 
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.rpc.binding.facet;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.rpc.binding.InboundBinding;

/**
 * @author Ravishankar Hiremath
 *
 */
public class InboundConfigurationHandler extends InboundBinding {

	/**
	 * constructor
	 *
	 */
	public InboundConfigurationHandler() {
		setFacetClass(ConfigurationHandler.class);
	}

	/**
	 * Add a ConfigurationHandler facet to send values to.
	 * @param listener ConfigurationHandler
	 * 
	 */
	public void addConfigurationHandlerFacet(ConfigurationHandler listener) {
		addListener(listener);
	}

	/**
	 * 
	 * @param listener ConfigurationHandler
	 */
	public void removeConfigurationHandlerFacet(ConfigurationHandler listener) {
		removeListener(listener);
	}

	protected void invoke(String method, Object[] params) {
		if ("valueChanged".equals(method)) {
			Map<String, String> configIdentifierTable = (Map<String, String>) params[0];
			String wedgeID = configIdentifierTable.get("wedgeID");
			String propertyName = configIdentifierTable.get("propertyName");

			// create ConfigurationIdentifier object
			ConfigurationIdentifier configIdentifier = new ConfigurationIdentifier(wedgeID, propertyName);

			// invoke the method
			((ConfigurationHandler) proxy).valueChanged(configIdentifier, (Serializable) params[1]);
		}
	}

}
