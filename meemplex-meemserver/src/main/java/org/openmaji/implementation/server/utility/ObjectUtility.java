/*
 * @(#)ObjectUtility.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider LRU caching for Class.forName() results.
 */

package org.openmaji.implementation.server.utility;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.meemplex.server.MeemplexActivator;
import org.openmaji.implementation.server.classloader.MajiClassLoader;
import org.openmaji.implementation.server.classloader.SystemExportList;
import org.openmaji.implementation.server.nursery.classloader.MajiURLClassLoader;

/**
 * <p>
 * ObjectUtility is a collection of methods for managing classes and objects.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class ObjectUtility {

	private static MajiURLClassLoader majiClassLoader = null;

	private static ClassLoader standardClassLoader;

	public static final Class<?> getClass(String className) throws IllegalArgumentException, ClassNotFoundException {

		return (getClass(Object.class, className, false));
	}
	
	/**
	 * Get a class of the specified type and name.
	 *
	 * @param classType Type of class to instatiate
	 * @param className Fully qualified name of class to instantiate
	 * @return Instance of the specified class name and type
	 * @exception IllegalArgumentException Problem loading the class
	 */

	public static final <T extends Object> Class<T> getClass(Class<T> classType, String className) throws IllegalArgumentException, ClassNotFoundException {

		return (getClass(classType, className, false));
	}

	/**
	 * Get a class of the specified type and name.
	 *
	 * @param classType Type of class to instatiate
	 * @param className Fully qualified name of class to instantiate
	 * @param useMajiClassLoader Set true to use Maji's classloader 
	 * @return Instance of the specified class name and type
	 * @exception IllegalArgumentException Problem loading the class
	 */

	@SuppressWarnings("unchecked")
	public static final <T extends Object> Class<T> getClass(Class<T> classType, String className, boolean useMajiClassLoader) throws IllegalArgumentException, ClassNotFoundException {

		ClassLoader classLoader = null;
		if (System.getProperty(MajiClassLoader.CLASSPATH_FILE) == null) {
			//System.err.println("WARNING: " + MajiClassLoader.CLASSPATH_FILE + " not set");
			if (true) {
				classLoader = useMajiClassLoader ? getMajiClassLoader() : getStandardClassLoader();
			}
		}
		else {
			System.err.println("WARNING: " + MajiClassLoader.CLASSPATH_FILE + " IS set.  Do you want to use this old ExportList?");
			classLoader = SystemExportList.getInstance().getClassLoaderFor(className);
		}

		Class<?> newClass = null;
		try {
			if (classLoader == null) {
				newClass = Class.forName(className);
			}
			else {
				//newClass = classLoader.loadClass(className);
				newClass = Class.forName(className, true, classLoader);
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			//throw new IllegalArgumentException("Could not find class: " + className);
			throw classNotFoundException;
		}

		if (classType.isAssignableFrom(newClass) == false) {
			throw new IllegalArgumentException("Loaded class " + className + " isn't the correct type: " + classType);
		}

		return (Class<T>) newClass;
	}

	/**
	 * Instantiate a new object of the specified class type and name.
	 *
	 * @param classType Type of class to instatiate
	 * @param className Fully qualified name of class to instantiate
	 * @return Instance of the specified class name and type
	 * @exception IllegalArgumentException Problem loading the class
	 * @exception IllegalAccessException   Problem instantiating the class
	 * @exception InstantiationException   Problem instantiating the class
	 */

	public static final <T extends Object> T create(Class<T> classType, String className) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException {

		return (create(classType, className, false));
	}

	/**
	 * Instantiate a new object of the specified class type and name.
	 *
	 * @param classType Type of class to instatiate
	 * @param className Fully qualified name of class to instantiate
	 * @param useMajiClassLoader Set true to use Maji's classloader
	 * @return Instance of the specified class name and type
	 * @exception IllegalArgumentException Problem loading the class
	 * @exception IllegalAccessException   Problem instantiating the class
	 * @exception InstantiationException   Problem instantiating the class
	 */

	public static final <T extends Object> T create(Class<T> classType, String className, boolean useMajiClassLoader) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
			InstantiationException {

		Class<T> newClass = getClass(classType, className, useMajiClassLoader);

		/* Instantiation can cause a couple of different exceptions
		 * and there is nothing useful that can be done to recover.
		 *
		 * The mostly likely problem is trying to instantiate a class,
		 * which is missing a "no arguments" constructor.
		 */

		return (newClass.newInstance());
	}

	/**
	 * Generate an unique signature for a given method and it's parameters.
	 *
	 * @param method Method for which a signature is required
	 * @return Unique signature for the given method
	 */

	public static final Integer methodSignature(Method method) {

		int hashCode = method.getName().hashCode();

		Class[] parameterTypes = method.getParameterTypes();

		for (int index = 0; index < parameterTypes.length; index++) {
			hashCode ^= parameterTypes[index].hashCode() + index;
		}

		return (new Integer(hashCode));
	}

	public static MajiURLClassLoader getMajiClassLoader() {
		if (majiClassLoader == null) {
			majiClassLoader = new MajiURLClassLoader(new URL[] {}, getStandardClassLoader());
		}
		return majiClassLoader;
	}

	public static void addJar(String urlString) throws MalformedURLException {

		URL url = new URL(urlString);

		getMajiClassLoader().addURL(url);

		URL[] urls = getMajiClassLoader().getURLs();

		for (int i = 0; i < urls.length; i++) {
			System.err.println(urls[i]);
		}
	}

	public static void resetClassLoader() {

		majiClassLoader = null;
	}
	
	private static ClassLoader getStandardClassLoader() {
		if (standardClassLoader == null) {
			try {
				standardClassLoader = MeemplexActivator.class.getClassLoader();
			}
			catch (Error e) {
				standardClassLoader = ObjectUtility.class.getClassLoader();			
			}
		}
		return standardClassLoader;
	}
}
