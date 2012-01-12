/*
 * @(#)ClassLibrary.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.library.classlibrary;

import org.openmaji.meem.Facet;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface ClassLibrary extends Facet {

	/**
	 * Scan all classes in classpath
	 * @param classPath Path to scan
	 */
	public void scan(String classPath);


	// public void remove(String classPath);
}
