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
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CategoryMonitorMapPair {
	
	static private Logger logger = LogFactory.getLogger();
	
	private CategoryMonitor categoryMonitor;
	private Map children = Collections.synchronizedMap(new HashMap());

	public CategoryMonitorMapPair(CategoryMonitor categoryMonitor) {
		this.categoryMonitor = categoryMonitor;
	}

	public void addChild(String entryName, CategoryMonitor categoryMonitor) {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "addChild(): " + entryName + " : " + categoryMonitor);
		}
		
		children.put(entryName, new CategoryMonitorMapPair(categoryMonitor));
	}

	public void removeChild(String entryName) {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "removeChild(): " + entryName + " : " + this);
		}
		
		synchronized(children) {
			CategoryMonitorMapPair categoryMonitorMapPair = (CategoryMonitorMapPair) children.get(entryName);
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

	public Map getChildren() {
		return children;
	}

	public void removeChildren() {
		if (Common.TRACE_ENABLED && Common.TRACE_MEEMPATHRESOLVER) {
			LogTools.trace(logger, MeemResolverWedge.LOG_LEVEL, "removeChildren(): " + this);
		}
		Set keys;
		synchronized (children) {
			keys = children.keySet();
		}

		Iterator i = keys.iterator();
		while (i.hasNext()) {
			removeChild((String) i.next());
		}

	}
}
