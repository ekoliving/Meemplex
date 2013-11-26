/*
 * @(#)ConfigurationHandler.java
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
 * General configuration handler facet.
 * <p>
 * As a facet it is used to request changes on a meem's configuration
 * properties.
 * <p>
 * In the conduit context, as the configurationHandlerConduit it used to request
 * changes to the actual properties stored on the wedge.
 */
public interface ConfigurationHandler extends Facet {

	/**
	 * Called to request that the property associated with the passed in
	 * identifier is changed to the value given by value.
	 * 
	 * @param configurationIdentifier
	 *            the identifier associated with the property being changed.
	 * @param value
	 *            the value the property is to be changed to.
	 */
	public void valueChanged(ConfigurationIdentifier configurationIdentifier, Serializable value);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		public static String getIdentifier() {
			return ("configurationHandler");
		};
	}
}