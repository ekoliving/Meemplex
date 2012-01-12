/*
 * @(#)MeemkitClassLoader.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.classloader;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.openmaji.meem.MeemPath;

/**
 * @author mg
 */
public class MeemkitClassLoader extends URLClassLoader {

	private Set<String> exportSet = new HashSet<String>();
	private Set<MeemPath> referencedMeemPathsSet = new HashSet<MeemPath>();
	
	private boolean debug = false;
	
	private final String meemkitName;

	public MeemkitClassLoader(String meemkitName, URL[] urls, ClassLoader parent) {
		super(urls, parent);
		
		String debug = System.getProperty(MajiClassLoader.CLASSLOADER_DEBUG);
		if (debug != null && debug.equalsIgnoreCase("true")) {
			this.debug = true;
		}

		if (this.debug) {
			System.err.println("+++ Creating Intermajik MeekitClassLoader");
		}
		
		this.meemkitName = meemkitName;
		
		SystemExportList.getInstance().addClassLoader(this);
	}

	/**
	 * 
	 * @param jarFileURL
	 * @param exportPaths if null, treated as export everything in jar file
	 */
	public void addExportedLibrary(URL jarFileURL, String[] exportPaths) {
		List<String> exportPathsList = parseExportPaths(exportPaths);
		
		String urlString = jarFileURL.toExternalForm();
		
		URL url = null;
		try {
			url = new URL("jar:" + urlString + "!/");
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		
		try {
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();

			JarFile jarFile = jarConnection.getJarFile();

			parseJarFile(jarFile, exportPathsList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Package getPackage(String packageName) {
		return super.getPackage(packageName);
	}
	
	public Package[] getPackages(String packageName) {
		return super.getPackages();
	}

	private List<String> parseExportPaths(String[] exportPaths) {
		if (exportPaths == null) {
			// treat as export everything
			return new ArrayList<String>();
		}
		for (int i = 0; i < exportPaths.length; i++) {
			String path = exportPaths[i];
			if (path.equals("*")) {
				// export everything
				return new ArrayList<String>();
			} else if (path.endsWith(".*")) {
				// export package
				exportPaths[i] = getPackageName(path);
			}
		}
		return Arrays.asList(exportPaths);
	}

	private void parseJarFile(JarFile jarFile, List<String> exportPaths) {
		boolean exportAll = false;
		
		if (exportPaths.size() == 0) {
			// export everything
			exportAll = true;
		}
		
		Enumeration<JarEntry> e = jarFile.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();
			
			if (!ze.isDirectory()) {
				String name = ze.getName().replace('/', '.');
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6);
					if (exportAll || exportPaths.contains(getPackageName(name))) {
						exportSet.add(name);
						if (debug)
							System.err.println("MeemkitClassLoader(" + meemkitName + ").parseJarFile: " + name);
					}
				}				
			}
		}

	}
	
	private String getPackageName(String className) {
		if (className.indexOf(".") > -1) {
			return className.substring(0, className.lastIndexOf(".") - 1);
		}
		return "";
	}

	public boolean checkClassExported(String className) {
		return exportSet.contains(className);
	}
	
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (debug) System.err.println("MeemkitClassLoader(" + meemkitName + ").loadClass: " + name + " : " + resolve);
		
		Class<?> c = findLoadedClass(name);
		if (c != null && SystemExportList.getInstance().getClassLoaderFor(name) == this && c.getClassLoader() != this) {
			synchronized (name.intern()) {
				c = findClass(name);
			}
		}
		
		if (c == null && checkClassExported(name)) {
			synchronized (name.intern()) {
				c = findClass(name);
			}
		}
		if (c == null) {
			// haven't found it yet
			// try the MajiClassLoader
			ClassLoader cl = SystemExportList.getInstance().getClassLoaderFor(name);
			if (cl != null) {
				synchronized (name.intern()) {
					c = cl.loadClass(name);
				}
			} else {
				synchronized (name.intern()) {
					c = super.loadClass(name, false);
				}
			}
		}
		
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	/**
	 * @see java.net.URLClassLoader#findClass(java.lang.String)
	 */
	public Class<?> findClass(String name) throws ClassNotFoundException {
//		if (debug) {
//			System.err.println("MeemkitClassLoader(" + meemkitName + ").findClass: " + name);
//		}
//		
		Class<?> clazz = findLoadedClass(name);
//		
		if (clazz == null) {
//			synchronized (name.intern()) {
				clazz = super.findClass(name);
//			}
		}
		return clazz;		
	}
	
	public String getMeemkitName() {
		return meemkitName;
	}
	
	public void addReferencedMeemPath(MeemPath meemPath) {
		referencedMeemPathsSet.add(meemPath);
	}
	
	public Set<MeemPath> getReferencedMeemPathsSet() {
		return referencedMeemPathsSet;
	}
}