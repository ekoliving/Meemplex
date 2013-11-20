/*
 * @(#)ExporterHelper.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.manager.registry.jini;

import java.rmi.Remote;
import java.rmi.server.ExportException;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.manager.registry.MeemRegistryJiniUtility;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.export.Exporter;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0 
 */
public class ExporterHelper {

	private static final String MEEM_REGISTRY_JINI_EXPORT_WEDGE_NAME =
		"org.openmaji.implementation.server.nursery.jini.registry.RemoteRegistryWedge";

	private static final String REMOTE_MEEM_EXPORTER_NAME = "remoteMeemExporter";

	private static Configuration configuration = null;

	static {

		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);

		if (majitekDirectory == null) {
			throw new RuntimeException("Empty Majitek directory property: " + Common.PROPERTY_MAJI_HOME);
		}

		try {
			configuration =
				ConfigurationProvider.getInstance(
					new String[] { majitekDirectory + System.getProperty(MeemRegistryJiniUtility.JINI_CONFIGURATION_FILE)});

		} catch (ConfigurationException configurationException) {
			throw new RuntimeException("ConfigurationProviderException:" + configurationException);
		}
	}

	protected static Exporter getExporter() throws ConfigurationException { 
		return (Exporter) configuration.getEntry(MEEM_REGISTRY_JINI_EXPORT_WEDGE_NAME, REMOTE_MEEM_EXPORTER_NAME, Exporter.class);
	}

	public static Object export(Remote obj) {
		try {
			Exporter remoteMeemExporter = getExporter();

			return remoteMeemExporter.export(obj);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (ExportException e) {
			e.printStackTrace();
		}

		return null;

	}
}
