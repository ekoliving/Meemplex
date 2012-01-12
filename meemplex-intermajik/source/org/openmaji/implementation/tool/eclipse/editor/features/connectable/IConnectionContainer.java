/*
 * @(#)IConnectionContainer.java
 * Created on 6/11/2003
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.tool.eclipse.editor.features.connectable;

import java.util.List;

/**
 * <code>IConnectionContainer</code>.
 * <p>
 * @author Kin Wong
 */
public interface IConnectionContainer {
	
	/**
	 * Gets all the source connections from the connection container implementation.
	 */
	List getSourceConnections();
	
	/**
	 * Gets the target connections from the connection container implementation.
	 */
	List getTargetConnections();
}
