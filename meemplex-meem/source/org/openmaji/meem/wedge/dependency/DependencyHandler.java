/*
 * @(#)DependencyHandler.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.wedge.dependency;

import org.openmaji.meem.Facet;
import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.LifeTime;

/**
 * <p>
 * Allows dependencies to be added and removed.
 * </p>
 * <p>
 * Listen on the corresponding <code>DependencyClient</code> facet to track the results
 * </p>
 * @author  mg
 * @version 1.0
 * @see DependencyClient
 */
public interface DependencyHandler extends Facet {

	/**
	 * Add a new dependency to this <code>Meem</code>'s list of dependencies.
	 * 
	 * @param facetIdentifier The identifier of the facet on the Source <code>Meem</code> to
	 * which the dependency will be added.
	 * @param dependencyAttribute Details of the dependency to be added.
	 * @param lifeTime The desired <code>LifeTime</code> for the new dependency.
	 */
	void addDependency(
		String facetIdentifier,
		DependencyAttribute dependencyAttribute,
		LifeTime lifeTime
	);
	
	/**
	 * Add a new dependency to this <code>Meem</code>'s list of dependencies.
	 * 
	 * @param facet The facet on the Source <code>Meem</code> to which the dependency will be added.
	 * @param dependencyAttribute Details of the dependency to be added.
	 * @param lifeTime The desired <code>LifeTime</code> for the new dependency.
	 */
	void addDependency(
		Facet facet,
		DependencyAttribute dependencyAttribute,
		LifeTime lifeTime
	);

	/**
	 * Remove a previously-added dependency from this <code>Meem</code>'s list of dependencies.
	 * This will cause the dependency to be automatically disconnected first if necessary.
	 * 
	 * @param dependencyAttribute Details of the dependency to be removed.
	 */
	void removeDependency(DependencyAttribute dependencyAttribute);

	/**
	 * Update the dependency with the given dependencyAttribute's identifier.
	 * 
	 * @param dependencyAttribute Details of the dependency to be updated.
	 */
	void updateDependency(DependencyAttribute dependencyAttribute);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {
		public static String getIdentifier() {
			return ("dependencyHandler");
		};
	}
}