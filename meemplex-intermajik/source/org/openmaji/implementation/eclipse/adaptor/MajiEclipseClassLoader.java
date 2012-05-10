/*
 * @(#)MajiEclipseClassLoader.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.eclipse.adaptor;

import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.framework.adaptor.BundleProtectionDomain;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegate;
import org.openmaji.implementation.server.classloader.MajiClassLoader;
import org.openmaji.implementation.server.classloader.MeemkitClassLoader;
import org.openmaji.implementation.server.classloader.SystemExportList;


/**
 * 
 * @author mg
 * @author stormboy
 */
public class MajiEclipseClassLoader extends DefaultClassLoader {
	private int id = (int) Math.round(Math.random() * 1000);
	
	public MajiEclipseClassLoader(ClassLoader parent, ClassLoaderDelegate delegate, BundleProtectionDomain domain, BaseData data, String[] bundleclasspath) {
		super(parent, delegate, domain, data, bundleclasspath);
		
		// make sure the MajiClassLoader is constructed
		new MajiClassLoader(this);
		
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class c = findLoadedClass(name);
		if (c != null) {
			return c;
		}
		
 		return super.loadClass(name);
	}

	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class c = findLoadedClass(name);
		if (c != null) {
			if (resolve) {
				resolveClass(c);
			}
			return c;
		}
		
 		return super.loadClass(name, resolve);
	}

	public Class<?> findLocalClass(String className) throws ClassNotFoundException {
		if (className.equals("org.openmaji.implementation.server.classloader.SystemExportList")) {
			return SystemExportList.class;
		}
		else if (className.equals("org.openmaji.implementation.server.classloader.MajiClassLoader")) {
			return MajiClassLoader.class;
		}
		else if (className.equals("org.openmaji.implementation.server.classloader.MeemkitClassLoader")) {
			return MeemkitClassLoader.class;
		}

		try {
			return super.findLocalClass(className);
		}
		catch (ClassNotFoundException e) {
		}
		
		ClassLoader classLoader = SystemExportList.getInstance().getClassLoaderFor(className);
		if (classLoader != null) {
			if (classLoader instanceof MajiClassLoader) {
				return ((MajiClassLoader) classLoader).findClass(className);
			}
			else if (classLoader instanceof MeemkitClassLoader) {
				return ((MeemkitClassLoader) classLoader).findClass(className);
			}
			else {
				//System.err.println("classloader for " + className + " is not a maji classloader");
			}

		}
		else {
			//System.err.println("not classloader for " + className);				
		}
		
		throw new ClassNotFoundException(className);
	}
	
	public String toString() {
		return "MajiEclipseClassLoader[" + id + "]";
	}
}