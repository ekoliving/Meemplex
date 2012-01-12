/*
 * @(#)NodeChangeListener.java
 * Created on 23/02/2004
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.hierarchy.nodes;

/**
 * <code>NodeChangeListener</code>.
 * <p>
 * @author Kin Wong
 */
public interface NodeChangeListener {
	public void childAdded(Node parent, Node node);
	public void childRemoved(Node parent, Node node);
	public void childRefreshed(Node node);
}
