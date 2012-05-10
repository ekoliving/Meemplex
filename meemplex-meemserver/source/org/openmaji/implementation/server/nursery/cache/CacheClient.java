/*
 * @(#)CacheClient.java
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
public interface CacheClient extends Facet {
	
	public void hit(Object key, Object value);
	public void miss(Object key);
	public void removed(Object key, Object value);
	
}
