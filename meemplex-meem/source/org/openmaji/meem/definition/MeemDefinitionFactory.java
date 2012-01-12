/*
 * @(#)MeemDefinitionFactory.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

import java.util.Collection;

import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * A MeemDefinitionFactory provides a convenient means of creating definitions for Meems.
 * </p>
 * <p>
 * This is particularly useful during the MeemServer bootstrap, because the essential Meems need to be defined and created, and yet, there is no access to a MeemStore (also a
 * Meem), which is the usual place for locating MeemDefinitions. The DefinitionFactory can also be used to define the system WedgeDefinitions.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public interface MeemDefinitionFactory {

	/**
	 * Create a MeemDefinition from a single class defining a wedge.
	 * <p>
	 * Note: If it is a class, then it will be used directly. Otherwise, if it is an interface, then the nested spi.create() method will be looked for. If the resulting class is a
	 * MeemDefinitionProvider, then simply use it to get the MeemDefinition (it'll include any required customizations). Otherwise, use the WedgeIntrospector.
	 */

	public MeemDefinition createMeemDefinition(Class<?> meemSpecification);

	/**
	 * Create a meem definition from an array of classes defining wedges.
	 * <p>
	 * Note: If a "specification" is a class, then it will be used directly. Otherwise, if it is an interface, then the nested spi.create() method will be looked for.
	 */

	public MeemDefinition createMeemDefinition(Class<?>[] meemSpecifications);

	/**
	 * Create a meem definition from an iterator defining wedge implementations.
	 * <p>
	 * Note: If a "specification" is a class, then it will be used directly. Otherwise, if it is an interface, then the nested spi.create() method will be looked for.
	 */

	public MeemDefinition createMeemDefinition(Collection<Class<?>> meemSpecifications);

	/**
	 * Create a MeemDefinition that is based on the Wedge implementation class that is associated with the Meem's identifier.
	 * <p>
	 * Note: Java programmers should use createMeemDefinition(class) in preference to this method. This method exists primarily for allowing identification of Meems outside of the
	 * Java code. For example in the properties files that specify the required essential Meems for a given Genesis profile.
	 * 
	 * @param meemTypeIdentifier
	 *            Type identifier for the Meem
	 * @return MeemDefinition for the Meem specified by the identifier
	 * @exception IllegalArgumentException
	 *                Unknown Meem identifier
	 * 
	 */

	public MeemDefinition createMeemDefinition(String meemTypeIdentifier);

	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
	public class spi {

		// Note: This is created as a Singleton for performance optimization.

		private static MeemDefinitionFactory meemDefinitionFactory = null;

		/**
		 * Create a meem definition factory.
		 * 
		 * @return a MeemDefinitionFactory.
		 */
		public static MeemDefinitionFactory create() {
			if (meemDefinitionFactory == null) {
				meemDefinitionFactory = (MeemDefinitionFactory) MajiSPI.provider().create(MeemDefinitionFactory.class);
			}

			return (meemDefinitionFactory);
		}
	}
}
