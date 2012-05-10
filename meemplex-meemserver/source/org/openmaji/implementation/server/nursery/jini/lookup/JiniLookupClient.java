/*
 * @(#)JiniLookupClient.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.jini.lookup;

import org.openmaji.meem.Meem;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface JiniLookupClient {
	
	/**
	 * Implement this method to be notified when a meem that matches the ServiceItemFilter given to startLookup() is
	 * found in the Jini Lookup Service
	 * @param meem Meem added to JLS
	 */
	public void meemAdded(Meem meem);

	/**
	 * Implement this method to be notified when a meem that matches the ServiceItemFilter given to startLookup() is
	 * removed from the Jini Lookup Service
	 * @param meem Meem added to JLS
	 */
	public void meemRemoved(Meem meem);
	
}
