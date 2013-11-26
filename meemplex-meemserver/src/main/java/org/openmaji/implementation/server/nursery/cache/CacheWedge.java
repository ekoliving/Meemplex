/*
 * @(#)CacheWedge.java
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

import java.util.HashMap;
import java.util.Map.Entry;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class CacheWedge implements Wedge, Cache {

	private HashMap<Object, Object> cacheMap = new HashMap<Object, Object>();

	public CacheClient cacheClient;
	
	public final ContentProvider<CacheClient> cacheClientProvider = new ContentProvider<CacheClient>() {
		public void sendContent(CacheClient target, Filter filter) {
			for (Entry<?, ?> entry : cacheMap.entrySet()) {
				target.hit(entry.getKey(), entry.getValue());
			}
		}
	};

	public synchronized void check(Object key) {
		if (cacheMap.containsKey(key)) {
			cacheClient.hit(key, cacheMap.get(key));
		}
		else {
			cacheClient.miss(key);
		}
	}

	public synchronized void put(Object key, Object value) {
		cacheMap.put(key, value);
	}

	public synchronized void remove(Object key) {
		if (cacheMap.containsKey(key)) {
			Object value = cacheMap.remove(key);
			cacheClient.removed(key, value);
		}
		else {
			cacheClient.removed(key, null);
		}
	}

}
