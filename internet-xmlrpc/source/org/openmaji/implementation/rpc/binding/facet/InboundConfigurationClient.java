package org.openmaji.implementation.rpc.binding.facet;

import java.util.Hashtable;
import java.util.Vector;

import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationHandler;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.rpc.binding.InboundBinding;

/**
 * @author Warren Bloomer
 *
 */
public class InboundConfigurationClient extends InboundBinding {

	/**
	 * constructor
	 *
	 */
	public InboundConfigurationClient() {
		setFacetClass(ConfigurationClient.class);
	}

	/**
	 * Add a ConfigurationHandler facet to send values to.
	 * @param listener ConfigurationHandler
	 * 
	 */
	public void addConfigurationClientFacet(ConfigurationClient listener) {
		addListener(listener);
	}

	/**
	 * 
	 * @param listener ConfigurationHandler
	 */
	public void removeConfigurationClientFacet(ConfigurationClient listener) {
		removeListener(listener);
	}

	protected void invoke(String method, Object[] params) {
		if ("specificationChanged".equals(method)) {
			Vector<Hashtable<String, Object>> oldSpecs = (Vector<Hashtable<String, Object>>) params[0];
			Vector<Hashtable<String, Object>> newSpecs = (Vector<Hashtable<String, Object>>) params[1];
			
			//Vector oldSpecs = 
			/*
			Hashtable<String, Object> configIdentifierTable = oldSpecs.get(index)
			String wedgeID = configIdentifierTable.get("wedgeID");
			String propertyName = configIdentifierTable.get("propertyName");

			// create ConfigurationIdentifier object
			ConfigurationIdentifier configIdentifier = new ConfigurationIdentifier(wedgeID, propertyName);

			// invoke the method
			//((ConfigurationClient) proxy).specificationChanged( oldSpecs, newSpecs );
			 */
		}
		else if ("valueAccepted".equals(method)) {
			// TODO
		}
		else if ("valueRejected".equals(method)) {
			// TODO
		}
	}

}
