/*
 * @(#)ConfigurationClient.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.wedge.configuration;

import java.io.Serializable;

import org.openmaji.meem.Facet;

/**
 * Client interface for configuration.
 * <p>
 * This class is used both to expose a facet on the meem that allows configuration
 * events to be communicated back from the meem and also that allows configuration
 * events between the meems wedges via the configurationClientConduit.
 * <p>
 * In the case of the facet an add outbound reference can be used to listen to how the
 * meem responds to configuration changes as they occur. 
 * <p>
 * In the case of the conduit
 * you can either implement their own listener on the conduit or use the 
 * ConfigurationClientAdapter which provides an introspecting adapter that will build a
 * configuration profile for the wedge based on what specifications are provided and what
 * methods are implemented.
 * <p>
 * Used this way the conduit is declared as:
 * <pre>
 *     public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);
 * </pre>
 * Otherwise an inner or anonymous class can be used to define the target.
 */
public interface ConfigurationClient
	extends Facet
{
	/**
	 * Announce that the specifications associated with the property being configured
	 * have now changed.
	 * 
	 * @param oldSpecifications the specifications, if any, associated with the meem previously.
	 * @param newSpecifications the specifications, if any, associated with the wedge now.
	 */
	public void specificationChanged(
	   ConfigurationSpecification[] oldSpecifications, ConfigurationSpecification[] newSpecifications);

	/**
	 * Announce that a change has been accepted.
	 * 
	 * @param id the identifier associated with the property that has changed.
	 * @param value the value the property now has.
	 */
	public void valueAccepted(ConfigurationIdentifier id, Serializable value);
	
	/**
	 * Announce that an attempted change has been rejected.
	 * <p>
	 * Note: toString() on the reason object should always produce a sensible error message.
	 * 
	 * @param id the identifier associated with the property that the change was attempted on.
	 * @param value the value that was attempted to be assigned to the property.
	 * @param reason the reason the change was rejected
	 */
	public void valueRejected(ConfigurationIdentifier id, Serializable value, Serializable reason);
}
