/*
 * @(#)WegdeDefinitionFactory.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;

import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * A WegdeDefinitionFactory provides a convenient means of creating definitions for Wegdes.
 * </p>
 * <p>
 * This is particularly useful during the MeemServer bootstrap, because the essential Meems need to be defined and created, and yet, there is no access to a MeemStore (also a
 * Meem), which is the usual place for locating MeemDefinitions. The WedgeDefinitionFactory can also be used to define the system WedgeDefinitions.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public interface WedgeDefinitionFactory {

	/**
	 * Create a WedgeDefinition that is based on the Wedge implementation class.
	 * 
	 * @param wedgeSpecification
	 *            Class to be inspected for the WedgeDefinition
	 * @return WedgeDefinition for the Wedge specified by the Class
	 */

	public WedgeDefinition createWedgeDefinition(Class<?> wedgeSpecification);

	public WedgeDefinition inspectWedgeDefinition(Class<?> wedgeSpecification);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {

		// Note: This is created as a Singleton for performance optimization.

		private static WedgeDefinitionFactory wedgeDefinitionFactory = null;

		/**
		 * Return the WedgeDefinitionFactory for this VM.
		 * 
		 * @return the WedgeDefinitionFactory
		 */
		public static WedgeDefinitionFactory create() {
			if (wedgeDefinitionFactory == null) {
				wedgeDefinitionFactory = (WedgeDefinitionFactory) MajiSPI.provider().create(WedgeDefinitionFactory.class);
			}

			return (wedgeDefinitionFactory);
		}
	}
}
