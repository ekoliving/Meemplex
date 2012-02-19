/*
 * @(#)ConfigurationClientStub.java
 * Created on 9/06/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.client;

import java.io.Serializable;

import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationIdentifier;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;

/**
 * <code>ConfigurationClientStub</code> provides an empty implementation of 
 * <code>ConfigurationClient</code>.<p>
 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient
 * @author Kin Wong
 */
public class ConfigurationClientStub implements ConfigurationClient {
	/* (non-Javadoc)
	 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient#specificationChanged(org.openmaji.meem.wedge.configuration.ConfigurationSpecification[], org.openmaji.meem.wedge.configuration.ConfigurationSpecification[])
	 */
	public void specificationChanged(
		ConfigurationSpecification[] oldSpecifications,
		ConfigurationSpecification[] newSpecifications) {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient#valueAccepted(org.openmaji.meem.wedge.configuration.ConfigurationIdentifier, java.io.Serializable)
	 */
	public void valueAccepted(ConfigurationIdentifier id, Serializable value) {
	}

	/* (non-Javadoc)
	 * @see org.openmaji.meem.wedge.configuration.ConfigurationClient#valueRejected(org.openmaji.meem.wedge.configuration.ConfigurationIdentifier, java.io.Serializable, java.io.Serializable)
	 */
	public void valueRejected(
		ConfigurationIdentifier id,
		Serializable value,
		Serializable reason) {
	}
}
