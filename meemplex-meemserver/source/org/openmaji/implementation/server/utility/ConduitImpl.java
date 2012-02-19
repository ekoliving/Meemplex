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

public class ConduitImpl implements Conduit {

	private final String identifier;

	private final Class<?> specification;

	private final HashSet<Object> targets = new HashSet<Object>();

	/**
	 * A map of objects to their conduit target objects relating to this conduit.
	 */
	private final HashMap<Object, Object> targetDeclarers = new HashMap<Object, Object>();

	private final ErrorHandler errorHandler;

	private Object proxy = null;

	public ConduitImpl(String identifier, Class<?> specification) {
		this(identifier, specification, null);
	}

	public ConduitImpl(String identifier, Class<?> specification, ErrorHandler errorHandler) {

		this.identifier = identifier;
		this.specification = specification;
		this.errorHandler = errorHandler;
	}

	public void addTarget(Object target) throws IllegalArgumentException {

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
	public void addTarget(Object target, Object owner) throws IllegalArgumentException {
		addTarget(target);
		targetDeclarers.put(owner, target);
	}

	public void removeTarget(Object target) {
		targets.remove(target);
	}

	public void removeDeclaringClassTarget(Object owner) {
		Object target = targetDeclarers.get(owner);
		targets.remove(target);
	}

	public String getIdentifier() {
		return (identifier);
	}

	public Object getProxy() {
		if (proxy == null) {
			Class<?>[] interfaces = new Class[] { specification };

			ClassLoader classLoader;
			if (System.getProperty(MajiClassLoader.CLASSPATH_FILE) == null) {
				classLoader = getClass().getClassLoader();
			}
			else {
				classLoader = SystemExportList.getInstance().getClassLoaderFor(specification.getName());
			}

			proxy = Proxy.newProxyInstance(classLoader, interfaces, this);
		}

		return (proxy);
	}

	public Class<?> getSpecification() {
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

		for (Object target : targets) {
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
