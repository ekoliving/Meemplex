/*
 * @(#)PropertyUtility.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Improve mergeProperties() to handle URLs as well as filenames.
 */

package org.openmaji.implementation.server.utility;

import java.io.*;
import java.util.*;

import org.swzoo.log2.core.*;

/**
 * <p>
 * PropertyUtility is a collection of methods for managing properties.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class PropertyUtility {

	private static final Logger logger = LogFactory.getLogger();

	public static final String PROPERTY_INCLUDE_ABSOLUTE = "org.openmaji.includeFileAbsolute";

	public static final String PROPERTY_INCLUDE_RELATIVE = "org.openmaji.includeFileRelative";

	/**
	 * Display all of the specified Properties.
	 *
	 * @param properties Properties to be displayed
	 */

	public static void dumpProperties(Properties properties) {

		if (properties == null) {
			LogTools.info(logger, "Properties is <null>");
			return;
		}

		Enumeration enumeration = properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			String value = properties.getProperty(name);

			LogTools.info(logger, "Property " + name + " = " + value);
		}
	}

	/**
	 * <p>
	 * Merge a new set of properties, which are located in the specified
	 * properties file, with an existing set of properties.  The merged properties end up
	 * in the existing set of properties. This method invokes loadRecursively() to load
	 * the properties file.
	 * </p>
	 * 
	 * <p>
	 * Note that properties contained in 'sourcePropertiesFilename' will NOT override identically
	 * named properties in 'destinationProperties'.
	 * </p>
	 *
	 * @param destinationProperties An existing set of properties
	 * @param sourcePropertiesFilename Properties to merge into the existing ones
	 * @exception IOException Problem reading the sourcePropertiesFilename
	 */

	public static void mergeProperties(Properties destinationProperties, String sourcePropertiesFilename) throws IOException {

		Properties properties = loadRecursively(sourcePropertiesFilename);
		mergeProperties(destinationProperties, properties);
	}

	/**
	 * Load the specified properties file and recursively merge all included properties files.
	 * If the properties file contains the property PROPERTY_INCLUDE_ABSOLUTE this method
	 * will attempt to load the properties from that second properties file and merge them with
	 * the properties from the first property file. If the properties file contains the
	 * property PROPERTY_INCLUDE_RELATIVE this method will attempt to load the properties
	 * from a file whose path is relative to the first file.
	 * 
	 * <p>
	 * Note that properties contained in included properties file
	 * will NOT override identically named properties in the first properties file.
	 * </p>
	 *
	 * @param propertiesFilename The name of the properies file
	 * @return A Properties instance containing all of the properties
	 * @throws IOException When an exception occurs while attempting to read a properties file
	 */

	public static Properties loadRecursively(String propertiesFilename) throws IOException {

		Properties properties = new Properties();
		File propertiesFile = new File(propertiesFilename);
		FileInputStream fis = new FileInputStream(propertiesFile);
		properties.load(fis);
		fis.close();

		String secondPropertiesFilename = properties.getProperty(PROPERTY_INCLUDE_ABSOLUTE);
		if (secondPropertiesFilename != null) {
			properties.remove(PROPERTY_INCLUDE_ABSOLUTE);
			Properties include = loadRecursively(secondPropertiesFilename);
			mergeProperties(properties, include);
		}

		secondPropertiesFilename = properties.getProperty(PROPERTY_INCLUDE_RELATIVE);
		if (secondPropertiesFilename != null) {
			String directoryName = propertiesFile.getParent();
			File secondPropertiesFile = new File(directoryName, secondPropertiesFilename);
			properties.remove(PROPERTY_INCLUDE_RELATIVE);
			Properties include = loadRecursively(secondPropertiesFile.getAbsolutePath());
			mergeProperties(properties, include);
		}

		return properties;
	}

	/**
	 * Merge a new set of properties with an existing set of properties.
	 * The merger ends up in the existing set of properties. Note that properties
	 * contained in 'sourceProperties' will NOT override identically named properties in
	 * 'destinationProperties'.
	 *
	 * @param destinationProperties An existing set of properties
	 * @param sourceProperties Properties to merge into the existing ones
	 */

	public static void mergeProperties(Properties destinationProperties, Properties sourceProperties) throws IOException {

		if (sourceProperties == null)
			return;

		Enumeration enumeration = sourceProperties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			if (destinationProperties.containsKey(name) == false) {
				String value = sourceProperties.getProperty(name);
				destinationProperties.setProperty(name, value);
			}
		}
	}

	/**
	 * Add properties to the system properties. Note that existing
	 * system properties <u>WILL NOT</u> be overwritten by this method.
	 *  
	 * @param properties Contains the properties to add to the list of system properties
	 */

	public static void mergeSystemProperties(Properties properties) {
		if (properties == null)
			return;

		Enumeration enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			if (System.getProperty(name) == null) {
				String value = properties.getProperty(name);
				System.setProperty(name, value);
			}
		}
	}

	/**
	 * Add properties to the system properties. Note that existing
	 * system properties <u>WILL</u> be overwritten by this method.
	 *  
	 * @param properties Contains the properties to add to the list of system properties
	 */

	public static void setAsSystemProperties(Properties properties) {
		if (properties == null)
			return;

		Enumeration enumeration = properties.propertyNames();
		while (enumeration.hasMoreElements()) {
			String name = (String) enumeration.nextElement();
			String value = properties.getProperty(name);
			System.setProperty(name, value);
		}
	}
}
