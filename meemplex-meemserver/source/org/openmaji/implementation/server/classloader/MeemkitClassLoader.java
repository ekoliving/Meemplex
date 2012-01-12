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
 * TODO implement finding library paths
 * 
 * @author mg
 * @author stormboy
 */
public class MeemkitClassLoader extends URLClassLoader {
	
	/**
	 * The name of the MeemKit this ClassLoader is for.
	 */
	private final String meemkitName;
	
	/**
	 * Set of classnames of classes exported by this ClassLoader
	 */
	private Set<String> exportSet = new HashSet<String>();
	
	private Set<MeemPath> referencedMeemPaths = new HashSet<MeemPath>();
	
	private boolean debug = false;
	
	/**
	 * Constructor
	 * 
	 * @param meemkitName
	 * @param urls
	 * @param parent
	 */
	public MeemkitClassLoader(String meemkitName, URL[] urls, ClassLoader parent) {
		super(urls, parent);
		
		if (debug) {
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<urls.length; i++) {
				if (i>0) {
					sb.append(",");
				}
				sb.append(urls[i]);
			}
			System.err.println("MeemkitClassLoader urls: "  + sb);
		}
		
		String debug = System.getProperty(MajiClassLoader.CLASSLOADER_DEBUG);
		if (debug != null && debug.equalsIgnoreCase("true")) {
			this.debug = true;
		}
		
		if (this.debug) {
			System.err.println("+++ Creating server MeemkitClassLoader");
		}

		
		this.meemkitName = meemkitName;
		
		/**
		 * Add this classloader to the SystemExportList
		 */
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
			ZipEntry ze =  e.nextElement();
			
			if (!ze.isDirectory()) {
				String name = ze.getName().replace('/', '.');
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - 6);
					if (exportAll || exportPaths.contains(getPackageName(name))) {
						exportSet.add(name);
						if (debug) {
							System.err.println("MeemkitClassLoader(" + meemkitName + ").parseJarFile: " + name);
						}
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
		if (debug) {
			System.err.println("MeemkitClassLoader(" + meemkitName + ").loadClass: " + name + " : " + resolve);
		}
		
		Class<?> c = findLoadedClass(name);

		if (c != null && SystemExportList.getInstance().getClassLoaderFor(name) == this && c.getClassLoader() != this) {
			synchronized (name.intern()) {
				// TODO commented out by warren 2009-08-04 - adds redundant call to findLoadedClass
				//c = findClass(name);
				c = super.findClass(name);
			}
		}
		
		if (c == null && checkClassExported(name)) {
			synchronized (name.intern()) {
				// TODO commented out by warren 2009-08-04 - adds redundant call to findLoadedClass
				//c = findClass(name);
				c = super.findClass(name);
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
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);
		if (clazz == null) {
			clazz = super.findClass(name);
		}
		return clazz;
	}
	
	public String getMeemkitName() {
		return meemkitName;
	}
	
	public void addReferencedMeemPath(MeemPath meemPath) {
		referencedMeemPaths.add(meemPath);
	}
	
	public Set<MeemPath> getReferencedMeemPathsSet() {
		return referencedMeemPaths;
	}
	
	/**
	 * TODO include the libraries that are in the meemkit.
	 * TODO take into account the os.name and os.arch when determining the path
	 * 
	 * @param libname
	 */
	protected String findLibrary(String libname) {
		return super.findLibrary(libname);
	}
}