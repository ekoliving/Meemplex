/*
 * @(#)CategoryMonitorMapPair.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.space.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openmaji.implementation.server.Common;

import java.util.logging.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CategoryMonitorMapPair {
	
	static private Logger logger = Logger.getAnonymousLogger();
	
	private CategoryMonitor categoryMonitor;
	private Map<String, CategoryMonitorMapPair> children = Collections.synchronizedMap(new HashMap<String, CategoryMonitorMapPair>());

	public CategoryMonitorMapPair(CategoryMonitor categoryMonitor) {
		this.categoryMonitor = categoryMonitor;
	}

	public void addChild(String entryName, CategoryMonitor categoryMonitor) {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			logger.log(MeemResolverWedge.LOG_LEVEL, "addChild(): " + entryName + " : " + categoryMonitor);
		}
		
		children.put(entryName, new CategoryMonitorMapPair(categoryMonitor));
	}

	public void removeChild(String entryName) {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			logger.log(MeemResolverWedge.LOG_LEVEL, "removeChild(): " + entryName + " : " + this);
		}
		
		synchronized(children) {
			CategoryMonitorMapPair categoryMonitorMapPair = children.get(entryName);
			if (categoryMonitorMapPair != null) {
				categoryMonitorMapPair.getCategoryMonitor().stopWatching();
				categoryMonitorMapPair.removeChildren();
				children.remove(entryName);
			}
		}
	}

	public CategoryMonitor getCategoryMonitor() {
		return categoryMonitor;
	}

	public Map<String,CategoryMonitorMapPair> getChildren() {
		return children;
	}

	public void removeChildren() {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			logger.log(MeemResolverWedge.LOG_LEVEL, "removeChildren(): " + this);
		}
		Set<String> keys;
		synchronized (children) {
			keys = children.keySet();
		}

		Iterator<String> i = keys.iterator();
		while (i.hasNext()) {
			removeChild(i.next());
		}

	}
}
