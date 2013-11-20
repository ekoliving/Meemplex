/*
 * @(#)CacheHelper.java
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
import java.util.Map;

import org.openmaji.meem.Meem;
import org.openmaji.meem.wedge.reference.Reference;
import org.openmaji.server.helper.ReferenceHelper;
import org.openmaji.server.utility.PigeonHole;
import org.openmaji.server.utility.TimeoutException;
import org.openmaji.system.meem.wedge.reference.ContentClient;
import org.openmaji.system.meem.wedge.reference.ContentException;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CacheHelper {
	private Cache cache;
	private Meem cacheMeem;

	public CacheHelper(Meem cacheMeem) {
		cache = (Cache) ReferenceHelper.getTarget(cacheMeem, "cache", Cache.class);
		this.cacheMeem = cacheMeem;
	}

	public Object get(Object key) {

		CacheClientImpl cacheClient = new CacheClientImpl();

		Reference<CacheClient> cacheReference = Reference.spi.create("cacheClient", (CacheClient)cacheClient, false);

		cacheMeem.addOutboundReference(cacheReference, false);

		cache.check(key);

		Object value = cacheClient.getValue();

		cacheMeem.removeOutboundReference(cacheReference);

		return value;
	}

	public void put(Object key, Object value) {
		cache.put(key, value);
	}

	public Object remove(Object key) {
		CacheClient cacheClient = new CacheClientImpl();

		Reference<CacheClient> cacheReference = Reference.spi.create("cacheClient", cacheClient, false);

		cacheMeem.addOutboundReference(cacheReference, false);

		cache.remove(key);

		Object value = ((CacheClientImpl)cacheClient).getValue();

		cacheMeem.removeOutboundReference(cacheReference);

		return value;
	}

	public Map<?,?> getEntries() {

		CacheContentClient cacheContentClient = new CacheContentClient();

		Reference<CacheContentClient> cacheClientReference = Reference.spi.create("cacheClient", cacheContentClient, true);

		cacheMeem.addOutboundReference(cacheClientReference, false);

		Map<?,?> map = cacheContentClient.getMap();

		cacheMeem.removeOutboundReference(cacheClientReference);

		return map;
	}

	private final class CacheClientImpl implements CacheClient {

		private PigeonHole<Object> valueHole = new PigeonHole<Object>();
		private long timeout = 60000;
		private boolean miss = false;
		
		/**
		 */
		public void hit(Object key, Object value) {
			valueHole.put(value);
		}

		/**
		 */
		public void miss(Object key) {
			miss = true;
			valueHole.put(new Object());
		}

		/**
		 */
		public void removed(Object key, Object value) {
			if (value == null) {
				miss = true;
				valueHole.put(new Object());
			} else {
				valueHole.put(value);
			}
		}

		public Object getValue() {
			try {
				Object result = valueHole.get(timeout);
				if (miss)
					return null;
				else
					return result;
			}
			catch (ContentException ex) {
				return null;
			}
			catch (TimeoutException ex) {
				return null;
			}
		}

	}

	private final class CacheContentClient implements CacheClient, ContentClient {

		private Map<Object, Object> map = new HashMap<Object, Object>();
		private PigeonHole<Map<?,?>> mapHole = new PigeonHole<Map<?,?>>();
		private long timeout = 60000;

		/**
		 */
		public void hit(Object key, Object value) {
			map.put(key, value);
		}

		/**
		 */
		public void miss(Object key) {
			// don't care
		}

		/**
		 */
		public void removed(Object key, Object value) {
			// don't care
		}

		/**
		 */
		public void contentSent() {
			mapHole.put(map);
		}

		/**
		 */
		public void contentFailed(String reason) {
		}

		public Map<?,?> getMap() {
			try {
				return mapHole.get(timeout);
			}
			catch (ContentException ex) {
				return null;
			}
			catch (TimeoutException ex) {
				return null;
			}
		}
	}
}
