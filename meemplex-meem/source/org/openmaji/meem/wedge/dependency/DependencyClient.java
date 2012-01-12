/*
 * @(#)DependencyClient.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.wedge.dependency;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.DependencyAttribute;


/**
 * <p>
 * A facet for reporting the addition, removal, connection and disconnection of dependencies.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public interface DependencyClient extends Facet {

	/**
	 * Reports that a dependency described by <code>dependencyAttribute</code>
	 * has been added.
	 */
	public void dependencyAdded(String facetId, DependencyAttribute dependencyAttribute);

	/**
	 * Reports that a dependency described by <code>dependencyAttribute</code>
	 * has been removed.
	 */
	public void dependencyRemoved(DependencyAttribute dependencyAttribute);

	/**
	 * Reports that the dependency has been updated.
	 * 
	 * @param dependencyAttribute
	 */
	public void dependencyUpdated(DependencyAttribute dependencyAttribute);

	/**
	 * Reports that a dependency described by <code>dependencyAttribute</code>
	 * has had its underlying <code>Reference</code> connected.
	 */
	public void dependencyConnected(DependencyAttribute dependencyAttribute);

	/**
	 * Reports that a dependency described by <code>dependencyAttribute</code>
	 * has had its underlying <code>Reference</code> disconnected.
	 */
	public void dependencyDisconnected(DependencyAttribute dependencyAttribute);
}
