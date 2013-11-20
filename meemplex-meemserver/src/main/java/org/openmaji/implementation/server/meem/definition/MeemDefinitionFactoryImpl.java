/*
 * @(#)MeemDefinitionFactoryImpl.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 *
 * - Put support into SPI, so that you can get an instance of
 *   this class as a singleton.  To reduce superfluous object creation.
 */

package org.openmaji.implementation.server.meem.definition;

import java.lang.reflect.Field;
import java.util.Collection;

import org.meemplex.meem.Meem;
import org.meemplex.meem.Wedge;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.MeemDefinition;
import org.openmaji.meem.definition.MeemDefinitionFactory;
import org.openmaji.meem.definition.MeemDefinitionProvider;
import org.openmaji.meem.definition.Scope;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.meem.definition.WedgeDefinition;
import org.openmaji.meem.definition.WedgeDefinitionFactory;
import org.openmaji.spi.MajiSPI;
import org.openmaji.system.spi.MajiSystemProvider;

/**
 * <p>
 * MeemDefinitionFactoryImpl provides a convenient means of creating complete definitions for Meems or partial definitions for Wedges.
 * </p>
 * <p>
 * The techniques for determining the contents of a MeemDefinition or a WedgeDefinition utilized here are based on introspection of the Wedge class implementation. The easy
 * approach for the developer is to allow the WedgeIntrospector to use reflection to provide a simple definition. If that isn't sufficient, then a more manually intensive approach
 * of hand-coding the required Meem, Wedge, Facet and Dependency Definitions is supported, if needed.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-07-29)
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class MeemDefinitionFactoryImpl implements MeemDefinitionFactory {

	private static MajiSystemProvider majiSystemProvider = null;

	private static WedgeDefinitionFactory wedgeDefinitionFactory = null;

	public MeemDefinitionFactoryImpl() {
		if (majiSystemProvider == null) {
			majiSystemProvider = (MajiSystemProvider) MajiSPI.provider();
		}

		if (wedgeDefinitionFactory == null) {
			wedgeDefinitionFactory = WedgeDefinitionFactory.spi.create();
		}
	}

	/**
	 * Note: If it is a class, then it will be used directly. Otherwise, if it is an interface, then the nested spi.create() method will be looked for. If the resulting class is a
	 * MeemDefinitionProvider, then simply use it to get the MeemDefinition (it'll include any required customizations). Otherwise, use the WedgeIntrospector.
	 * 
	 * @param meemSpecification
	 * @return MeemDefinition for the Meem specified by the identifier
	 * @exception IllegalArgumentException
	 *                Unknown Meem identifier
	 */

	public MeemDefinition createMeemDefinition(Class<?> meemSpecification) {
		MeemDefinition meemDefinition = null;
		
		meemSpecification = majiSystemProvider.getImplementation(meemSpecification);
		if (MeemDefinitionProvider.class.isAssignableFrom(meemSpecification)) {
			try {
				MeemDefinitionProvider meemDefinitionProvider = (MeemDefinitionProvider) meemSpecification.newInstance();
				meemDefinition = meemDefinitionProvider.getMeemDefinition();
			}
			catch (Exception exception) {
				throw new IllegalArgumentException("Unable to instantiate MeemDefinitionProvider: " + meemSpecification, exception);
			}
		}
		else if (meemSpecification.getAnnotation(Meem.class) != null) {
			Meem meemAnnotation = meemSpecification.getAnnotation(Meem.class);
			
			// use annotation name, or class name if it doesn't exist.
			String id = meemAnnotation.name().length() == 0 ? meemSpecification.getName() : meemAnnotation.name(); 
			
			meemDefinition = new MeemDefinition();
			meemDefinition.getMeemAttribute().setIdentifier(id);
			
			Field[] fields = meemSpecification.getFields();
			for (Field field : fields) {
				Wedge wedgeAnnotation = field.getAnnotation(Wedge.class);
				if (wedgeAnnotation != null) {
					WedgeDefinition wedgeDefinition = wedgeDefinitionFactory.createWedgeDefinition(field);
					WedgeAttribute wedgeAttribute = new WedgeAttribute(field.getType().getName(), wedgeAnnotation.name());
					wedgeDefinition.setWedgeAttribute(wedgeAttribute);
					meemDefinition.addWedgeDefinition(wedgeDefinition);
				}
			}
		}
		else {
			WedgeDefinition wedgeDefinition = wedgeDefinitionFactory.createWedgeDefinition(meemSpecification);
			meemDefinition = new MeemDefinition();
			meemDefinition.addWedgeDefinition(wedgeDefinition);
		}

		if (meemDefinition == null) {
			throw new IllegalArgumentException("The MeemDefinitionProvider '" + meemSpecification + "' returned a null MeemDefinition");
		}

		return (meemDefinition);
	}

	/**
	 * Note: If a "specification" is a class, then it will be used directly. Otherwise, if it is an interface, then the nested spi.create() method will be looked for.
	 */

	public MeemDefinition createMeemDefinition(Class<?>[] wedgeSpecifications) {

		// HashMap usedClasses = new HashMap();
		MeemDefinition meemDefinition = new MeemDefinition();

		for (int index = 0; index < wedgeSpecifications.length; index++) {
			meemDefinition.addWedgeDefinition(wedgeDefinitionFactory.createWedgeDefinition(wedgeSpecifications[index]));

			// automatic wedge identifier clash removal
			// Class spec = wedgeSpecifications[index];
			// WedgeDefinition wedgeDefinition = wedgeDefinitionFactory.createWedgeDefinition(spec);
			//
			// Integer count = (Integer) usedClasses.get(spec);
			// if (count == null) {
			// count = new Integer(1);
			// }
			// else {
			// WedgeAttribute wedgeAttribute = wedgeDefinition.getWedgeAttribute();
			// count = new Integer(count.intValue()+1);
			// wedgeAttribute.setIdentifier(wedgeAttribute.getIdentifier() + count);
			// }
			// meemDefinition.addWedgeDefinition(wedgeDefinition);
			// usedClasses.put(spec, count);
		}

		return (meemDefinition);
	}

	/**
	 * Note: If a "specification" is a class, then it will be used directly. Otherwise, if it is an interface, then the nested spi.create() method will be looked for.
	 */

	public MeemDefinition createMeemDefinition(Collection<Class<?>> wedgeSpecifications) {

		MeemDefinition meemDefinition = new MeemDefinition();

		for (Class<?> cls : wedgeSpecifications) {
			meemDefinition.addWedgeDefinition(wedgeDefinitionFactory.createWedgeDefinition(cls));
		}

		return (meemDefinition);
	}

	/**
	 * Create a MeemDefinition that is based on the Wedge implementation class that is associated with the Meem's identifier.
	 * 
	 * @param meemTypeIdentifier
	 *            Identifier for the Meem
	 * @return MeemDefinition for the Meem specified by the identifier
	 * @exception IllegalArgumentException
	 *                Unknown Meem identifier
	 * 
	 *                Note: Java programmers should use createMeemDefinition(class) in preference to this method. This method exists primarily for allowing identification of Meems
	 *                outside of the Java code. For example in the properties files that specify the required essential Meems for a given Genesis profile.
	 */

	public MeemDefinition createMeemDefinition(String meemTypeIdentifier) {

		Class<?> meemSpecification = majiSystemProvider.getSpecification(meemTypeIdentifier);

		if (meemSpecification == null) {
			throw new IllegalArgumentException("Unknown Meem type: " + meemTypeIdentifier);
		}

		MeemDefinition meemDefinition = createMeemDefinition(meemSpecification);

		meemDefinition.getMeemAttribute().setIdentifier(meemTypeIdentifier);

		return (meemDefinition);
	}
}