/*
 * @(#)LRUCacheWedge.java
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

import java.util.LinkedHashMap;
import java.util.Map;


import java.util.Map.Entry;

import org.openmaji.implementation.server.Common;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;

import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class LRUCacheWedge implements Wedge, LRUCache, Cache {

	private Logger logger = Logger.getAnonymousLogger();	
		
	LRU cache = new LRU(100);
	
    public CacheClient cacheClient;
    
    public final ContentProvider<CacheClient> cacheClientProvider = new ContentProvider<CacheClient>() {
        public void sendContent(CacheClient target, Filter filter) {
            for (Entry<Object, Object> entry : cache.entrySet()) {
                target.hit(entry.getKey(), entry.getValue());
            }
        }
    };
	
	public synchronized void updateSize(int size) {
		cache = new LRU(size);
	}

	public synchronized void check(Object key) {
		if (cache.containsKey(key)) {
			cacheClient.hit(key, cache.get(key));
		} else {
			cacheClient.miss(key);
		}
	}

	public synchronized void put(Object key, Object value) {
		cache.put(key, value);
		logger.log(Common.getLogLevelVerbose(), "Cache.put() : key=" + key + ", value=" + value);
	}

	public synchronized void remove(Object key) {
		if (cache.containsKey(key)) {
			Object value = cache.remove(key);
			cacheClient.removed(key, value);
			logger.log(Common.getLogLevelVerbose(), "Cache.remove() : key=" + key + ", value=" + value);
		} else {
			cacheClient.removed(key, null);
		}
	}
	
	private synchronized void removeEntry(Object key) {
		remove(key);
	}

	
	private class LRU extends LinkedHashMap<Object, Object> {
		private static final long serialVersionUID = 2634575427909499639L;
		
		int maxEntries = 10;

		public LRU(int maxEntries) {
			super(maxEntries, 0.75f, true);
			this.maxEntries = maxEntries;
		}

		protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
			if (size() > maxEntries) {
				removeEntry((String)eldest.getKey());
			}
			return false;
		}
	}

}
