/*
 * @(#)SystemExportList.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.classloader;

import java.util.*;

/**
 * @author mg
 */
public final class SystemExportList {
	
	private static SystemExportList instance = null;
	
	private List<MeemkitClassLoader> classLoaders = new LinkedList<MeemkitClassLoader>();
	
	private MajiClassLoader majiClassLoader = null;
	
	private static final boolean debug = false;
	
	private Set<String> majiExportSet = new HashSet<String>();
	
	/**
	 * Hidden constructor.
	 */
	private SystemExportList() {
		if (instance == null) {
			instance = this;			
		}
		
		if (debug) {
			System.out.println("++++ new Intermajik SystemExportList");
		}
//		new MajiClassLoader(this.getClass().getClassLoader());		
	}
	
	public static SystemExportList getInstance() {
		if (instance == null) {
			instance = new SystemExportList();			
		}

		return instance;
	}

	public void addClassLoader(MeemkitClassLoader classLoader) {
        
        synchronized (classLoaders)
        {
            classLoaders.add(classLoader);
        }
	}
	
	public void removeClassLoader(MeemkitClassLoader classLoader) {
        synchronized (classLoaders)
        {
            classLoaders.remove(classLoader);
        }
	}
	
	public ClassLoader getClassLoaderFor(String className) {
		if (majiExportSet.contains(className)) {
			return majiClassLoader;
		}
		
        List<MeemkitClassLoader> currentLoaders = null;
        
        synchronized (classLoaders)
        {
            currentLoaders = new ArrayList<MeemkitClassLoader>(classLoaders);
        }
        
		for (MeemkitClassLoader classLoader : currentLoaders) {
			if (classLoader.checkClassExported(className)) {
				return classLoader;
			}
		}
		
		return null;
	}
	
	final void setMajiClassLoader(MajiClassLoader majiClassLoader) {
		if (this.majiClassLoader == null) {
			if (debug) {
				System.out.println(getInfo() + " - Setting MajiClassLoader: " + majiClassLoader);
			}
			this.majiClassLoader = majiClassLoader;
		}		
	}
	
	public MajiClassLoader getMajiClassLoader() {
		return majiClassLoader;
	}

	void addMajiClassLoaderExport(Set<String> exports) {
		majiExportSet.addAll(exports);
	}
	
	private String getInfo() {
		return "ClassLoaderExporter" + " (" + getClass().getClassLoader() + ")";
	}
}
