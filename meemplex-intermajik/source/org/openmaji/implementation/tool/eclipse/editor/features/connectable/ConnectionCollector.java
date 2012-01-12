/*
 * @(#)ConnectionContainer.java
 * Created on 15/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

import java.util.Iterator;
import java.util.List;

/**
 * <code>ConnectionCollector</code> traverses the model heirarchy recursively
 * and collect connections in the children that implements 
 * <code>IConnectionContainer</code>.
 * <p>
 * @author Kin Wong
 */
abstract public class ConnectionCollector {
	/**
	 * Collects source connections recursively.
	 * @param root The root of the heirarchy to traverse.
	 * @param connections The map that contains source collectionss.
	 */
	public void collectSourceConnections(Object root, List connections) {
		List children = resolveChildren(root);
		if(children == null) return;
		
		Iterator it = children.iterator();
		while(it.hasNext()) {
			Object child = it.next();
			
			if(child instanceof IConnectionContainer) {
				IConnectionContainer container = (IConnectionContainer)child;
				connections.addAll(container.getSourceConnections());
			}
			else
			collectSourceConnections(child, connections);
		}
	}
	
	/**
	 * Collects target connections recursively.
	 * @param root The root of the heirarchy to traverse.
	 * @param connections The map that contains target collections.
	 */
	public void collectTargetConnections(Object root, List connections) {
		List children = resolveChildren(root);
		if(children == null) return;
		Iterator it = children.iterator();
		while(it.hasNext()) {
			Object child = it.next();
			if(child instanceof IConnectionContainer) {
				IConnectionContainer container = (IConnectionContainer)child;
				connections.addAll(container.getTargetConnections());
			}
			else
			collectTargetConnections(child, connections);
		}
	}
	
	/**
	 * Given a parent model , resolves it to a list of children.
	 * @param model The root of the model.
	 * @return List A list containing all children of the model, null if unable 
	 * to resolve.
	 */
	abstract protected List resolveChildren(Object model);
}
