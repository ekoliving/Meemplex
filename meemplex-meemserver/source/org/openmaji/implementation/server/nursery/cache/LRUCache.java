/*
 * @(#)LRUCache.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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
public interface LRUCache extends Cache, Facet {
	
	public void updateSize(int size);
	
}
