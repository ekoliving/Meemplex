/*
 * @(#)Test.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.resolver;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.system.space.resolver.MeemResolverClient;


/**
 * <p>
 * This is only for testing. Beanshell has a problem with inner classes that get passed a null value in a parameter
 * </p>
 * @author  mg
 * @version 1.0
 */
public class MeemPathResolverClientImpl implements MeemResolverClient {

	/**
	 */
	public void meemResolved(MeemPath meemPath, Meem meem) {
		if (meem != null) {
			//System.err.println("Resolved : " + meemPath + "  Meem: " + meem.getMeemPath());
		} else {
			//System.err.println("MeemPath cannot be resolved : " + meemPath);
		}

	}

}
