/*
 * @(#)MeemClientProxyCache.java
 * Created on 27/08/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.tool.eclipse.client;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.openmaji.implementation.tool.eclipse.client.security.SecurityListener;
import org.openmaji.implementation.tool.eclipse.client.security.SecurityManager;
import org.openmaji.meem.MeemPath;



/**
 * <code>MeemClientProxyCache</code>.
 * <p>
 * @author Kin Wong
 */
class MeemClientProxyCache {
	/**
	 * <code>ProxyCacheEntry</code> represents an entry in the meem client 
	 * proxy cache.<p>
	 * @author Kin Wong
	 */
	class ProxyCacheEntry {
		MeemClientProxyCache cache;
		MeemClientProxy proxy;
		/**
		 * Constructs a <code>ProxyCacheEntry</code>.<p>
		 * @param cache The cache of the entry.
		 * @param proxy The proxy to be cached.
		 */
		ProxyCacheEntry(MeemClientProxyCache cache, MeemClientProxy proxy) {
			this.cache = cache;
			this.proxy = proxy;
		}
		/**
		 * Gets the cached proxy.<p>
		 */
		MeemClientProxy getProxy() {
			return proxy;
		}
		/**
		 * Discards the cached proxy.<p>
		 */
		void discard() {
			proxy.disconnectAll();
		}
	}
	
	/**
	 * <code>ProxyCacheEntryLinkedHashMap</code> is a linked hash map for 
	 * storing ProxyCacheEntry with a limit for size and will discard
	 * entry by calling discard() on the entry.<p>
	 * @author Kin Wong
	 */
	private final class ProxyCacheEntryLinkedHashMap extends LinkedHashMap {
		private static final long serialVersionUID = 6424227717462161145L;

		static private final int MIN_LIMIT = 512;
		private int limit = 1024;
		/**
		 * Gets the limit of the map.<p>
		 * @return The limit of the map.
		 */
		public int getLimit() {
			return limit;
		}
		/**
		 * Sets the limit of the map.<p>
		 * @param limit The new limit of the map.
		 */
		public void setLimit(int limit) {
			if(this.limit == limit) return; // Nothing to change
			this.limit = limit;
		}
		/* (non-Javadoc)
		 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
		 */
		protected boolean removeEldestEntry(Entry eldest) {
			if(size() > Math.max(MIN_LIMIT, limit)) {
				ProxyCacheEntry entry = (ProxyCacheEntry)eldest.getValue();
				entry.discard();
				return true;
			}
			return false;
		}
	}
	
	private ProxyCacheEntryLinkedHashMap pathToProxyMap = new ProxyCacheEntryLinkedHashMap();
	
	public MeemClientProxyCache() {
		SecurityManager.getInstance().addSecurityListener(securityListener);
	}
	
	/**
	 * Gets the limit of the cache.<p>
	 * @return The limit of the cache.
	 */
	public int getLimit() {
		return pathToProxyMap.getLimit();
	}
	/**
	 * Sets the limit of the cache.<p>
	 * @param limit The new limit of the cache.
	 */
	public void setLimit(int limit) {
		pathToProxyMap.setLimit(limit);
	}
	/**
	 * Finds a cached <code>MeemClientProxy</code> for the meem at meemPath.
	 * @param meemPath The MeemPath of the meem.
	 * @return A <code>MeemClientProxy</code> if a cached 
	 * <code>MeemClientProxy</code> is found, null otherwise.
	 */
	public MeemClientProxy find(MeemPath meemPath) {
		ProxyCacheEntry entry = (ProxyCacheEntry)pathToProxyMap.get(meemPath);
		if(entry == null) return null;
		return entry.proxy;
	}
	/**
	 * Adds a proxy to the cache.<p>
	 * @param proxy The proxy to be cached.
	 */
	public void add(MeemClientProxy proxy) {
		ProxyCacheEntry entry = new ProxyCacheEntry(this, proxy);
		pathToProxyMap.put(proxy.getMeemPath(), entry);
	}
	/**
	 * Removes a cached proxy from the cache.<p>
	 * @param proxy The proxy to be removed.
	 * @return true if it has been removed, false otherwise.
	 */
	public boolean remove(MeemClientProxy proxy) {
		ProxyCacheEntry entry = (ProxyCacheEntry)pathToProxyMap.get(proxy.getMeemPath());
		if(entry == null) return false;
		
		pathToProxyMap.remove(proxy.getMeemPath());
		entry.discard();
		return true;
	}
	
	public void clearCache() {
		Iterator iter = pathToProxyMap.values().iterator();
		while (iter.hasNext()) {
			ProxyCacheEntry entry = (ProxyCacheEntry)iter.next();
			iter.remove();
			entry.discard();
		}
	}
	
	SecurityListener securityListener = new SecurityListener() {
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.client.security.SecurityListener#onLogin(org.openmaji.implementation.tool.eclipse.client.security.SecurityManager)
		 */
		public void onLogin(SecurityManager manager) {
		}
		/* (non-Javadoc)
		 * @see org.openmaji.implementation.tool.eclipse.client.security.SecurityListener#onLogout(org.openmaji.implementation.tool.eclipse.client.security.SecurityManager)
		 */
		public void onLogout(SecurityManager manager) {
			clearCache();
		}
	};
}
