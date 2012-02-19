/*
 * @(#)MajiURLClassLoader.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class MajiURLClassLoader extends URLClassLoader {

	public MajiURLClassLoader(URL[] urls) {
		super(urls);
	}
	public MajiURLClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	public MajiURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	/**
	 * Changed from protected to public
	 * @see java.net.URLClassLoader#addURL(java.net.URL)
	 */
	public void addURL(URL url) {
		super.addURL(url);
	}
	
	/**
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	protected Class findClass(String name) throws ClassNotFoundException {
		System.err.println("findClass: " + name );
		return super.findClass(name);
	}

	/**
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		System.err.println("finalizing");
		super.finalize();
	}

}
