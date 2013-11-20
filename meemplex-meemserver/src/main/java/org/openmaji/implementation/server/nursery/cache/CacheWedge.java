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
import java.util.Iterator;
import java.util.Map;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.filter.Filter;
import org.openmaji.system.meem.wedge.reference.ContentProvider;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CacheWedge implements Wedge, Cache {

	private HashMap cacheMap = new HashMap();

    public CacheClient cacheClient;
    public final ContentProvider cacheClientProvider = new ContentProvider() {
        public void sendContent(Object target, Filter filter) {
            for (Iterator i = cacheMap.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();

                ((CacheClient) target).hit(entry.getKey(), entry.getValue());
            }
        }
    };

	public synchronized void check(Object key) {
		if (cacheMap.containsKey(key)) {
			cacheClient.hit(key, cacheMap.get(key));
		} else {
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
		} else {
			cacheClient.removed(key, null);
		}
	}

}
