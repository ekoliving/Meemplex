/*
 * @(#)StandardDefinitionStore.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.definition.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;
import org.openmaji.implementation.server.space.meemstore.definition.MeemStoreDefinitionStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.space.Space;
import org.openmaji.system.space.meemstore.MeemStore;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  stormboy
 * @version 0.0
 */
public class BerkeleyDefinitionStore implements MeemStoreDefinitionStore {

	static private final Logger logger = LogFactory.getLogger();

	private String baseDir = null;

	public void configure(MeemStore meemStore, Properties properties) {
		
		baseDir = properties.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION) + "/definition/";
		File dir = new File(baseDir);
		if (!dir.exists()) {
			// try and make the dir
			if (!dir.mkdirs())
				LogTools.error(logger, "MeemDefinition storage directory cannot be created: " + baseDir);
		}

	}

	public void close() {
	}
	
	/**
	 */
	public MeemDefinition load(MeemPath meemPath) {
		String fileName = baseDir + meemPath.getLocation();

		MeemDefinition definition = null;

		// This classloader change is to allows classes loaded by eclipse to 
		// perform Class.forName()
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			try {
				FileInputStream fis = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(fis);

				definition = (MeemDefinition) ois.readObject();
				ois.close();
			} catch (ClassNotFoundException e) {
				LogTools.error(logger, "Exception while loading MeemDefinition " + meemPath, e);
			} catch (FileNotFoundException e) {
				// this is allowed
			} catch (IOException e) {
				LogTools.error(logger, "Exception while loading MeemDefinition " + meemPath, e);
			}

		} finally {
			Thread.currentThread().setContextClassLoader(previousClassLoader);
		}

		return definition;
	}

	public void store(MeemPath meemPath, MeemDefinition definition) {

		String fileName = baseDir + meemPath.getLocation();

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMSTORE) {
			LogTools.trace(logger, Common.getLogLevelVerbose(), "Storing definition file: " + fileName);
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(definition);
			oos.close();
		} catch (IOException e) {
			LogTools.error(logger, "Exception while storing MeemDefinition for " + meemPath + ": " + definition, e);
		}

		storeVersion(meemPath, definition);

	}

	public void remove(MeemPath meemPath) {
		String fileName = baseDir + meemPath.getLocation();

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMSTORE) {
			LogTools.trace(logger, Common.getLogLevelVerbose(), "Removing definition file: " + fileName);
		}
		
		File file = new File(fileName);
		if (!file.delete() && file.exists()) {
			LogTools.warning(logger, "Cannot delete definition file " + fileName);
		}

		file = new File(fileName + "_version");
		if (!file.delete() && file.exists()) {
			LogTools.warning(logger, "Cannot delete definition version file " + fileName);
		}
	}

	public int getVersion(MeemPath meemPath) {
		String fileName = baseDir + meemPath.getLocation() + "_version";
		String version = "-1";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			version = reader.readLine();
			reader.close();
		} catch (IOException e) {
			//LogTools.error(logger, "Exception while loading MeemDefinition version " + meemPath.toString(), e);
		}

		return Integer.parseInt(version);
	}

	private void storeVersion(MeemPath meemPath, MeemDefinition definition) {
		String fileName = baseDir + meemPath.getLocation() + "_version";

		try {
			FileWriter writer = new FileWriter(fileName);
			writer.write(String.valueOf(definition.getMeemAttribute().getVersion()));
			writer.close();
		} catch (IOException e) {
			LogTools.error(logger, "Exception while storing MeemDefinition version " + meemPath.toString(), e);
		}

		// also need to trash the existing content for this meempath
		// -mg- put this back in 
		// MeemStoreHelper.getContentStore().remove(meemPath);

	}

	public Set<MeemPath> getAllPaths() {

		Set<MeemPath> paths = new HashSet<MeemPath>();

		File dir = new File(baseDir);
		String[] files = dir.list();

		if (files == null) {
			// directory doesn't exist
			LogTools.error(logger, "MeemContent storage directory does not exist: " + baseDir);
		} else {
			for (int i = 0; i < files.length; i++) {
				if (!files[i].endsWith("_version")) {
					MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, files[i]);

					paths.add(meemPath);
				}
			}
		}
		return paths;
	}

}
