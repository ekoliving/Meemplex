/*
 * @(#)Cache.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.cache;

import org.openmaji.meem.Facet;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface Cache extends Facet {
	
	public void check(Object key);
	public void put(Object key, Object value);
	public void remove(Object key);
	
}