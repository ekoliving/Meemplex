/*
 * @(#)StandardContentStore.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.meemstore.content.standard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.space.meemstore.MeemStoreWedge;
import org.openmaji.implementation.server.space.meemstore.content.MeemStoreContentStore;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.space.Space;
import org.openmaji.system.meem.definition.MeemContent;
import org.openmaji.system.space.meemstore.MeemStore;


import java.util.logging.Level;
import java.util.logging.Logger;


public class StandardContentStore implements MeemStoreContentStore {

	static private final Logger logger = Logger.getAnonymousLogger();
	
	private String baseDir = null;

	public void configure(MeemStore meemStore, Properties properties) {
		
		baseDir = properties.getProperty(MeemStoreWedge.MEEMSTORE_LOCATION) + "/content/";
		File dir = new File(baseDir);
		if (!dir.exists()) {
			// try and make the dir
			if (!dir.mkdirs())
				logger.log(Level.WARNING, "MeemContent storage directory cannot be created: " + baseDir);
		}
	}

	public void close() {

	}

	public MeemContent load(MeemPath meemPath) {
		String fileName = baseDir + meemPath.getLocation();

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMSTORE) {
			logger.log(Common.getLogLevelVerbose(), "Loading content file: " + fileName);
		}
		
		MeemContent meemContent = null;

		// This classloader change is to allows classes loaded by eclipse to 
		// perform Class.forName()
		ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			try {
				FileInputStream fis = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(fis);

				meemContent = (MeemContent) ois.readObject();
				ois.close();
				
			} catch (ClassNotFoundException e) {
				logger.log(Level.WARNING, "Exception while loading MeemContent " + meemPath.toString(), e);
			} catch (FileNotFoundException e) {
				//	Send back an empty MeemContent
			 	meemContent = new MeemContent();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Exception while loading MeemContent " + meemPath.toString(), e);
			}

		} finally {
			Thread.currentThread().setContextClassLoader(previousClassLoader);
		}

		return meemContent;
	}

	public void store(MeemPath meemPath, MeemContent content) {
		if (content == null)
			return;

		String fileName = baseDir + meemPath.getLocation();

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMSTORE) {
			logger.log(Common.getLogLevelVerbose(), "Storing content file: " + fileName);
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(content);
			oos.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception while storing MeemContent " + meemPath.toString(), e);
		}
	}

	public void remove(MeemPath meemPath) {
		String fileName = baseDir + meemPath.getLocation();

		if (Common.TRACE_ENABLED && Common.TRACE_MEEMSTORE) {
			logger.log(Common.getLogLevelVerbose(), "Removing content file: " + fileName);
		}
		
		File file = new File(fileName);
		if (file.exists()) {
			if (!file.delete()) {
				logger.log(Level.WARNING, "Cannot delete content file: " + fileName);
			}
		}

	}

	public Set<MeemPath> getAllPaths() {
		Set<MeemPath> paths = new HashSet<MeemPath>();

		File dir = new File(baseDir);
		String[] files = dir.list();

		if (files == null) {
			// directory doesn't exist
			logger.log(Level.WARNING, "MeemContent storage directory does not exist: " + baseDir);
		} else {
			for (int i = 0; i < files.length; i++) {
				MeemPath meemPath = MeemPath.spi.create(Space.MEEMSTORE, files[i]);
				paths.add(meemPath);
			}
		}
		return paths;
	}

}
