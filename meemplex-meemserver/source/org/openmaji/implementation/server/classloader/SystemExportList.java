/*
 * @(#)SystemExportList.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.classloader;

import java.util.*;

/**
 * @author mg
 * @author stormboy
 */
public final class SystemExportList {
	
	private static SystemExportList instance = null;

	private List<MeemkitClassLoader> meemkitClassLoaders = new LinkedList<MeemkitClassLoader>();

	private MajiClassLoader majiClassLoader = null;

	/**
	 * The classes exported by the MajiClassLoader
	 */
	private Set<String> majiExportSet = new HashSet<String>();
	
	private HashSet<MeemkitClassLoaderListener> classLoaderListeners = new HashSet<MeemkitClassLoaderListener>();

	private static final boolean debug = false;
	
	/**
	 * Hidden constructor.
	 */
	private SystemExportList() {
		if (debug) {
			System.err.println("+++ Creating server version of SystemExportList");
			new Exception().printStackTrace();
		}
		if (instance == null) {
			instance = this;
		}
		
//		new MajiClassLoader(this.getClass().getClassLoader());		
	}
	
	public static SystemExportList getInstance() {
		if (instance == null) {
			instance = new SystemExportList();			
		}

		return instance;
	}

	/**
	 * Add a MeemkitClassloader to the export list.
	 * 
	 * @param classLoader
	 */
	public void addClassLoader(MeemkitClassLoader classLoader) {
        
        synchronized (meemkitClassLoaders)
        {
            meemkitClassLoaders.add(classLoader);
            for (MeemkitClassLoaderListener listener: classLoaderListeners) {
            	listener.classloaderAdded(classLoader.getMeemkitName());
            }
        }
	}
	
	public void removeClassLoader(MeemkitClassLoader classLoader) {
        synchronized (meemkitClassLoaders)
        {
            meemkitClassLoaders.remove(classLoader);
            for (MeemkitClassLoaderListener listener: classLoaderListeners) {
            	listener.classloaderRemoved(classLoader.getMeemkitName());
            }
        }
	}
	
	public ClassLoader getClassLoaderFor(String className) {
		
		// TODO added this - Warren 8/8/2011
		if (true) {
			return SystemExportList.class.getClassLoader();
		}

		if (majiExportSet.contains(className)) {
			return majiClassLoader;
		}
		
        List<MeemkitClassLoader> currentLoaders = null;
        
        synchronized (meemkitClassLoaders)
        {
            currentLoaders = new ArrayList<MeemkitClassLoader>(meemkitClassLoaders);
        }

		for (MeemkitClassLoader classLoader : currentLoaders) {
			if (classLoader.checkClassExported(className)) {
				return classLoader;
			}
		}
		
		return null;
	}
	
	public void addListener(MeemkitClassLoaderListener listener) {
        synchronized (meemkitClassLoaders) {
        	classLoaderListeners.add(listener);
        	
        	// send initial content
        	for (MeemkitClassLoader classLoader : meemkitClassLoaders) {
        		listener.classloaderAdded(classLoader.getMeemkitName());
        	}
        }
	}
	
	public void removeListener(MeemkitClassLoaderListener listener) {
        synchronized (meemkitClassLoaders) {
        	classLoaderListeners.remove(listener);
        }		
	}
	
	/**
	 * Set the Maji ClassLoader
	 * @param majiClassLoader
	 */
	final void setMajiClassLoader(MajiClassLoader majiClassLoader) {
		if (this.majiClassLoader == null) {
			this.majiClassLoader = majiClassLoader;
		}		
	}
	
	void addMajiClassLoaderExport(Set<String> exports) {
		majiExportSet.addAll(exports);
	}
}
