/*
 * @(#)CategoryMonitorTree.java
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

import java.util.*;

import org.openmaji.meem.MeemPath;


/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class CategoryMonitorTree {
	
	private Map monitoredSpaces = Collections.synchronizedMap(new HashMap());

	public void addCategoryMonitor(MeemPath meemPath, CategoryMonitor categoryMonitor) {

		LinkedList paths = new LinkedList();

		String space = meemPath.getSpace().getType();

		String location = meemPath.getLocation();
		StringTokenizer tok = new StringTokenizer(location, "/");
		while (tok.hasMoreTokens()) {
			paths.add(tok.nextToken());
		}

		// start with the space
		CategoryMonitorMapPair spaceMonitorMapPair = null;
		synchronized (monitoredSpaces) {
			spaceMonitorMapPair = (CategoryMonitorMapPair) monitoredSpaces.get(space);

			if (spaceMonitorMapPair == null) {
				// The only time this is ok is if the paths list is empty
				if (paths.size() == 0) {
					spaceMonitorMapPair = new CategoryMonitorMapPair(categoryMonitor);
					monitoredSpaces.put(space, spaceMonitorMapPair);					
					// we are done
					return;
				} else {
					throw new RuntimeException("Tried to add a MeemPath with a previously unseen space type");
				}
			}
		}
		
		Map children = spaceMonitorMapPair.getChildren();
		CategoryMonitorMapPair pair = spaceMonitorMapPair;
		
		for (int i = 0; i < paths.size() - 1; i++) {
			String pathName = (String) paths.get(i);

			pair = (CategoryMonitorMapPair) children.get(pathName);

			if (pair != null) {
				children = (Map) pair.getChildren();
			} else {
				throw new RuntimeException("Tried to add a MeemPath whose parents are not being watched: " + meemPath);
			}
		}
		
		// we should now have the new category watchers parent pair
		String pathName = (String) paths.get(paths.size() - 1);
		
		pair.addChild(pathName, categoryMonitor);

	}

	public CategoryMonitorMapPair getCategoryMonitorMapPair(MeemPath meemPath) {
		
		LinkedList paths = new LinkedList();

		String space = meemPath.getSpace().getType();

		String location = meemPath.getLocation();
		StringTokenizer tok = new StringTokenizer(location, "/");
		while (tok.hasMoreTokens()) {
			paths.add(tok.nextToken());
		}

		CategoryMonitorMapPair spaceMonitorMapPair = (CategoryMonitorMapPair) monitoredSpaces.get(space);

		if (spaceMonitorMapPair == null) {
			// not watching this space type. return straight away
			return null;
		}
		
		if (paths.size() == 0) {
			// if this is the case, we are just looking at the space
			return spaceMonitorMapPair;
		}

		Map children = spaceMonitorMapPair.getChildren();
		for (int i = 0; i < paths.size() - 1; i++) {
			String pathName = (String) paths.get(i);

			CategoryMonitorMapPair pair = (CategoryMonitorMapPair) children.get(pathName);

			if (pair != null) {
				children = (Map) pair.getChildren();
			} else {
				return null;
			}
		}

		String pathName = (String) paths.get(paths.size() - 1);

		return (CategoryMonitorMapPair) children.get(pathName);
	}
	
	public void removeMeemPath(MeemPath meemPath) {
		LinkedList paths = new LinkedList();

		String space = meemPath.getSpace().getType();

		String location = meemPath.getLocation();
		StringTokenizer tok = new StringTokenizer(location, "/");
		while (tok.hasMoreTokens()) {
			paths.add(tok.nextToken());
		}
		
		// go thru all the category watchers in the meempath and remove this meempath from their list of paths to watch
		
		if (paths.size() > 0) {
		
			CategoryMonitorMapPair spaceMonitorMapPair = (CategoryMonitorMapPair) monitoredSpaces.get(space);
			
			spaceMonitorMapPair.getCategoryMonitor().removeWatchedMeemPath((String) paths.get(0), meemPath);
			
			Map children = spaceMonitorMapPair.getChildren();
			for (int i = 0; i < paths.size() - 1; i++) {
				String pathName = (String) paths.get(i);
				String nextPathName = (String) paths.get(i + 1);
	
				CategoryMonitorMapPair pair = (CategoryMonitorMapPair) children.get(pathName);
	
				if (pair != null) {
					pair.getCategoryMonitor().removeWatchedMeemPath(nextPathName, meemPath);
					children = (Map) pair.getChildren();
				} 
			}
		}
	}

}
