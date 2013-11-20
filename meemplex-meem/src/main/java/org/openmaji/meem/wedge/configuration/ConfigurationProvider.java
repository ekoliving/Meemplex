/*
 * @(#)ConfigurationProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.wedge.configuration;

import org.openmaji.meem.filter.Filter;

/**
 * The ConfigurationProvider is used as the basis for the configurationProviderConduit, 
 * essentially the passed in client needs to have configurationChanged() and
 * configurationAccepted() called on it for each property that has been defined by the 
 * wedge calling the conduit.
 * <p>
 * This class is implemented internally to the ConfigurationClientAdapter so if you are
 * using the ConfigurationClientAdapter there is no need to implement this by hand.
 */
public interface ConfigurationProvider
{
	/**
	 * Provide the passed in client with details about our configuration.
	 * <p>
	 * The implementation should call configurationChanged() and then configurationAccepted()
	 * for each configuration property the implementation is associated with.
	 * 
	 * @param client the client to be told of our configuration properties.
	 * @param filter the filter object for any filtering to be applied.
	 */
	public void provideConfiguration(ConfigurationClient client, Filter filter);
}
