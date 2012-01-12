/*
 * @(#)ConnectionGroup.java
 * Created on 15/04/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.collapsible;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.util.Assert;

/**
 * <code>ConnectionGroup</code>.
 * <p>
 * @author Kin Wong
 */
public class ConnectionGroup implements Serializable, Cloneable {
	private static final long serialVersionUID = 6424227717462161145L;

	private Object model;
	private HashSet connections;
	/**
	 * Constructs an instance of <code>ConnectionGroup</code> for the model.
	 * <p>
	 * @param model The model associates with this collection group.
	 */
	ConnectionGroup(Object model) {
		this.model = model;
	}
	public boolean empty() {
		return (connections == null);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return model.hashCode();
	}
	public Iterator iterator() {
		if(connections == null) return Collections.EMPTY_LIST.iterator();
		return connections.iterator();
	}
	public void add(Object connection) {
		Assert.isNotNull(connection);
		if(connections == null) connections = new HashSet();
		connections.add(connection);
	}
	public boolean remove(Object connection) {
		Assert.isNotNull(connection);
		boolean removed = connections.remove(connection);
		if(connections.size() == 0) connections = null;
		return removed;
	}
	public void clear() {
		connections = null;
	}
}
