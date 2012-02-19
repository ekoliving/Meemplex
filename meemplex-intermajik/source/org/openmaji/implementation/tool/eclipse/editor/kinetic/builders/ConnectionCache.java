/*
 * @(#)ConnectionCache.java
 * Created on 13/11/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.kinetic.builders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <code>ConnectionCache</code> provides an efficient connection cache for 
 * <code>ConfigurationBuilder</code> to maintain connection resolution. Given
 * a target id, it returns all the source id(s) of connections, and vice versa.
 * <p>
 * @author Kin Wong
 */
public class ConnectionCache {
	static private Object[] emptyIds = new Object[0];
	private Map targetSourceMap = new HashMap();	// targetId -> sourceId -> set
	private Map sourceTargetMap = new HashMap();	// sourceId -> targetId -> set
	
	/**
	 * Adds the specified connection to the cache.
	 * <p>
	 * @param sourceId The source id of the connection.
	 * @param targetId The target id of the connection.
	 * @param connectionId The id of the connection.
	 */
	public void add(Object sourceId, Object targetId, Object connectionId) {
		// Target to source map entry
		Map sourceMap = (Map)targetSourceMap.get(targetId);
		if(sourceMap == null) {
			sourceMap = new HashMap();
			targetSourceMap.put(targetId, sourceMap);
		}
		
		Set connectionSet = (Set)sourceMap.get(sourceId);
		if(connectionSet == null) {
			connectionSet = new HashSet();
			sourceMap.put(sourceId, connectionSet);
		}
		connectionSet.add(connectionId);
		
		Map targetMap = (Map)sourceTargetMap.get(sourceId);
		if(targetMap == null) {
			targetMap = new HashMap();
			sourceTargetMap.put(sourceId, targetMap);
		}
		
		targetMap.put(targetId, connectionSet);
	}
	
	/**
	 * Removes all the connections between source and target.
	 * <p>
	 * @param sourceId The source id of the connection(s).
	 * @param targetId The target id of the connection(s).
	 */
	public void removeAll(Object sourceId, Object targetId) {
		Map sourceMap = (Map)targetSourceMap.get(targetId); // All the targets
		if(sourceMap != null) sourceMap.remove(sourceId);

		Map targetMap = (Map)sourceTargetMap.get(sourceId); // All the sources
		if(targetMap != null) targetMap.remove(targetId);
	}
	
	/**
	 * Removes the specific connection between source and target.
	 * <p>
	 * @param sourceId The source id of the connection(s).
	 * @param targetId The target id of the connection(s).
	 * @param connectionId The id of the connection to be removed.
	 * @return boolean true if the connection is removed, false otherwise.
	 */
	public boolean remove(Object sourceId, Object targetId, Object connectionId) {
		Map sourceMap = (Map)targetSourceMap.get(targetId); // All the targets
		if(sourceMap == null)	return false;
		
		Set connectionSet = (Set)sourceMap.get(sourceId);
		if(connectionSet == null) return false;
		
		if(!connectionSet.remove(connectionId)) return false;
		if(connectionSet.isEmpty()) {
			 sourceMap.remove(sourceId);
			 
			 Map targetMap = (Map)sourceTargetMap.get(sourceId);
			 if(targetMap != null) targetMap.remove(targetId);
		}
		return true;
	}
	
	public void remove(Object objectId) {
			targetSourceMap.remove(objectId);
			sourceTargetMap.remove(objectId);
		}
	
	/**
	 * Gets all the source ids for the given target id.
	 * <p>
	 * @param targetId The target id of all the connections.
	 * @return Object[] The source ids of all the connections.
	 */
	public Object[] getSourcesFromTarget(Object targetId) {
		Map sourceMap = (Map)targetSourceMap.get(targetId);
		if(sourceMap == null) return emptyIds;
		return sourceMap.keySet().toArray();
	}
	
	/**
	 * Gets all the target ids for the given source id.
	 * <p>
	 * @param sourceId The source id of all the connections.
	 * @return Object[] The target ids of all the connections.
	 */
	public Object[] getTargetsFromSource(Object sourceId) {
		Map targetMap = (Map)sourceTargetMap.get(sourceId);
		if(targetMap == null) return emptyIds;
		return targetMap.keySet().toArray();
	}
}