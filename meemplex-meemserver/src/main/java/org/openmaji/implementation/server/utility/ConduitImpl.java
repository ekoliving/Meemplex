/*
 * @(#)ConduitImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - In invoke(), pass Exceptions to the Error Facet, so that all Conduit
 *   targets are invoked.
 */

package org.openmaji.implementation.server.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.classloader.MajiClassLoader;
import org.openmaji.implementation.server.classloader.SystemExportList;
import org.openmaji.meem.wedge.error.ErrorHandler;

/**
 * <p>
 * Conduit ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-10-08)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class ConduitImpl<T> implements Conduit<T> {
	
	private static final Logger logger = Logger.getAnonymousLogger();

	private final String identifier;

	private final Class<T> specification;

	private final HashSet<T> targets = new HashSet<T>();

	/**
	 * A map of objects to their conduit target objects relating to this conduit.
	 */
	private final HashMap<Object, T> targetDeclarers = new HashMap<Object, T>();

	private final ErrorHandler errorHandler;

	private T proxy = null;

	public static <T> Conduit<T> create(String identifier, Class<T> specification) {
		return new ConduitImpl<T>(identifier, specification, null);
	}
	
	public static <T> Conduit<T> create(String identifier, Class<T> specification, ErrorHandler errorHandler) {
		return new ConduitImpl<T>(identifier, specification, errorHandler);
	}
	
//	public ConduitImpl(String identifier, Class<?> specification) {
//		this(identifier, specification, null);
//	}
//
	private ConduitImpl(String identifier, Class<T> specification, ErrorHandler errorHandler) {

		this.identifier = identifier;
		this.specification = specification;
		this.errorHandler = errorHandler;
	}

	public void addTarget(T target) throws IllegalArgumentException {

		if (!specification.isInstance(target)) {
			throw new IllegalArgumentException("Conduit target " + target + " does not match specification type: " + specification);
		}

		targets.add(target);
	}

	/**
	 * The target is a field value of the owner.
	 * 
	 * @param target
	 * @param owner
	 * @throws IllegalArgumentException
	 */
	public void addTarget(T target, Object owner) throws IllegalArgumentException {
		addTarget(target);
		targetDeclarers.put(owner, target);
	}

	public void removeTarget(T target) {
		targets.remove(target);
	}

	public void removeDeclaringClassTarget(Object owner) {
		Object target = targetDeclarers.get(owner);
		targets.remove(target);
	}

	public String getIdentifier() {
		return (identifier);
	}

	public T getProxy() {
		if (proxy == null) {
			Class<?>[] interfaces = new Class[] { specification };

			ClassLoader classLoader;
			if (System.getProperty(MajiClassLoader.CLASSPATH_FILE) == null) {
				classLoader = getClass().getClassLoader();
			}
			else {
				classLoader = SystemExportList.getInstance().getClassLoaderFor(specification.getName());
			}

			try {
				proxy = (T) Proxy.newProxyInstance(classLoader, interfaces, this);
			}
			catch (ClassCastException e) {
				logger.log(Level.INFO, "problem casting with " + specification.getName(), e);
			}
		}

		return (proxy);
	}

	public Class<T> getSpecification() {
		return (specification);
	}

	public Object invoke(Object proxy, Method method, Object[] args) {

		if (method.getDeclaringClass() == Object.class) {
			if (args == null) {
				if (method.getReturnType() == Integer.TYPE) {
					// Object.hashCode
					return new Integer(this.hashCode());
				}

				// Object.toString
				return this.toString();
			}

			return Boolean.valueOf(args[0] == proxy);
		}

		for (T target : targets) {
			try {
				method.invoke(target, args);
			}
			catch (InvocationTargetException e) {
				Throwable ex = e;

				if (e.getCause() != null) {
					ex = e.getCause();
				}

				if (errorHandler == null) {
					ex.printStackTrace(System.err);
				}
				else {
					errorHandler.thrown(ex);
				}
			}
			catch (Exception e) {
				if (errorHandler == null) {
					e.printStackTrace(System.err);
				}
				else {
					errorHandler.thrown(e);
				}
			}
		}

		return null;
	}

	/**
	 * Provides a String representation of Conduit.
	 * 
	 * @return String representation of Conduit
	 */

	public String toString() {
		return (getClass().getName() + "[" + "identifier=" + identifier + ", specification=" + specification + ", targets=" + targets + "]");
	}
}
