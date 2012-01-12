/*
 * @(#)InvocationListGenerator.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;

import java.util.Iterator;

import org.openmaji.spi.MajiSPI;

/**
 * Interface that a generator for invocation lists must conform to.
 */
public interface InvocationListIdentifierProvider {
	/**
	 * Return an Iterator containing the appropriate hook class names for the given invocation attributes.
	 * 
	 * @return the appropriate hook list iterator.
	 */
	public Iterator<String> generate();

	/* ---------- Nested class for SPI ----------------------------------------- */

	public class spi {
		public static InvocationListIdentifierProvider create() {
			return ((InvocationListIdentifierProvider) MajiSPI.provider().create(InvocationListIdentifierProvider.class));
		}
	}
}
