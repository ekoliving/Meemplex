/*
 * @(#)Launch.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;

import javax.security.auth.Subject;

import org.openmaji.system.gateway.ServerGateway;

/**
 * @author mg
 */
public class Launch {

	private static final String PROPERTY_START_CLASSNAME = "org.openmaji.launch.class";

	private static final String DEFAULT_START_CLASSNAME = "org.openmaji.implementation.server.genesis.LaunchMeemServer";

	private static final String PROPERTY_USING_JNLP = "org.openmaji.using_jnlp";

	private static final String DEFAULT_USING_JNLP = "false";
	
	private static ClassLoader majiClassLoader;

	
	public static boolean isUsingJNLP() {
		String usingJNLPProperty = System.getProperty(PROPERTY_USING_JNLP, DEFAULT_USING_JNLP);

		return ("true".equalsIgnoreCase(usingJNLPProperty));
	}

	public void launch(String[] args) {
		String className = System.getProperty(PROPERTY_START_CLASSNAME);
		if (className == null) {
			className = DEFAULT_START_CLASSNAME;
			// System.err.println("Launch class not specified");
			// System.exit(1);
		}
		System.err.println("launching maji");

		/*
		 * Transition to using Java Web Start ... need to ensure that classes
		 * loaded using the MajiClassLoader don't get security exceptions. Until
		 * then, use a gross hack and disable the SecurityManager.
		 * 
		 * Unofficial Java Web Start/JNLP FAQ Q: Can I use my own custom
		 * ClassLoader? http://lopica.sourceforge.net/faq.html#customcl
		 */

		if (isUsingJNLP()) {
			System.setSecurityManager(null); // Hack for Java Web Start
		}

		majiClassLoader = new MajiClassLoader(this.getClass().getClassLoader());

		Thread.currentThread().setContextClassLoader(majiClassLoader);

		try {
			Class<?> c = Class.forName(className, true, majiClassLoader);
			Method m = c.getMethod("main", new Class[] { String[].class });
			m.invoke(null, new Object[] { args });
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public static void shutdown() {
		try {
			
			Runnable shutdown = new Runnable() {
				public void run() {
					Subject subject = Subject.getSubject(AccessController.getContext());
					ServerGateway gateway = ServerGateway.spi.create(subject);
					gateway.shutdown();
				}
			};

			// execute LoginHelper.doAs with majiClassLoader
			String className = "org.openmaji.implementation.security.auth.LoginHelper";
			String methodName = "doAs";
			
			Class<?> c = Class.forName(className, true, majiClassLoader);
			Method m = c.getMethod(methodName, Runnable.class, String.class, String.class);
			m.invoke(null, shutdown, "system", "system99");

			//LoginHelper.doAs(shutdown, "system", "system99");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Launch().launch(args);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
