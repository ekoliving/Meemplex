/*
 * @(#)WedgeDefinitionFactoryImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 *
 * - Cache WedgeDefinitions created by WedgeIntrospector, for performance.
 *
 * - Put support into AbstractFactory, so that you can get an instance of
 *   this class as a singleton.  To reduce superfluous object creation.
 *
 * - Consider validating WedgeDefinition created by the WedgeIntrospector.
 */

package org.openmaji.implementation.server.meem.definition;

import java.lang.reflect.Field;

import org.meemplex.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.spi.MajiSPI;
import org.openmaji.system.spi.MajiSystemProvider;

/**
 * <p>
 * WedgeDefinitionFactoryImpl provides a convenient means of creating complete
 * definitions for Meems or partial definitions for Wedges.
 * </p>
 * <p>
 * The techniques for determining the contents of a MeemDefinition or a
 * WedgeDefinition utilized here are based on introspection of the Wedge class
 * implementation. The easy approach for the developer is to allow the
 * WedgeIntrospector to use reflection to provide a simple definition. If that
 * isn't sufficient, then a more manually intensive approach of hand-coding the
 * required Meem, Wedge, Facet and Dependency Definitions is supported, if
 * needed.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-07-29)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class WedgeDefinitionFactoryImpl implements WedgeDefinitionFactory {

	private static MajiSystemProvider majiSystemProvider = null;

	public WedgeDefinitionFactoryImpl() {
		if (majiSystemProvider == null) {
			majiSystemProvider = (MajiSystemProvider) MajiSPI.provider();
		}
	}

	/**
	 * Create a WedgeDefinition that is based on the Wedge implementation class.
	 * 
	 * @param wedgeSpecification
	 *            Class to be inspected for the WedgeDefinition
	 * @return WedgeDefinition for the Wedge specified by the Class
	 */

	@Override
	public WedgeDefinition createWedgeDefinition(Class<?> wedgeSpecification) {

		WedgeDefinition wedgeDefinition = null;

		wedgeSpecification = majiSystemProvider.getImplementation(wedgeSpecification);

		if (WedgeDefinitionProvider.class.isAssignableFrom(wedgeSpecification)) {
			try {
				WedgeDefinitionProvider wedgeDefinitionProvider = (WedgeDefinitionProvider) wedgeSpecification.newInstance();
				wedgeDefinition = wedgeDefinitionProvider.getWedgeDefinition();
			}
			catch (Exception exception) {
				throw new IllegalArgumentException("Unable to instantiate WedgeDefinitionProvider: " + wedgeSpecification);
			}
		}
		else {
			wedgeDefinition = inspectWedgeDefinition(wedgeSpecification);
		}

		return (wedgeDefinition);
	}
	
	@Override
	public WedgeDefinition createWedgeDefinition(Field wedgeField) {
		WedgeDefinition wedgeDefinition = null;
		Class<?> wedgeSpecification = majiSystemProvider.getImplementation(wedgeField.getType());
		if (WedgeDefinitionProvider.class.isAssignableFrom(wedgeSpecification)) {
			try {
				WedgeDefinitionProvider wedgeDefinitionProvider = (WedgeDefinitionProvider) wedgeSpecification.newInstance();
				wedgeDefinition = wedgeDefinitionProvider.getWedgeDefinition();
			}
			catch (Exception exception) {
				throw new IllegalArgumentException("Unable to instantiate WedgeDefinitionProvider: " + wedgeSpecification);
			}
		}
		else {
			wedgeDefinition = inspectWedgeDefinition(wedgeSpecification);
			Wedge wedgeAnnotation = wedgeField.getAnnotation(Wedge.class);
			if (wedgeAnnotation != null) {
				String identifier = wedgeAnnotation.name();
				if (identifier == null || identifier.length() == 0) {
					identifier = wedgeField.getName();
				}
				wedgeDefinition.getWedgeAttribute().setIdentifier(identifier);
			}
		}

		return (wedgeDefinition);
	}		

	@Override
	public WedgeDefinition inspectWedgeDefinition(Class<?> wedgeSpecification) {

		try {
			return (WedgeIntrospector.getWedgeDefinition(wedgeSpecification));
		}
		catch (WedgeIntrospectorException wedgeIntrospectorException) {
			throw new RuntimeException("Failed to create wedge definition", wedgeIntrospectorException);
		}
	}
}