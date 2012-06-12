/*
 * @(#)WedgeIntrospector.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Implement a mechanism for declaring persistent fields in a Wedge.
 *   At the moment, any public field that is not an Interface is persisted.
 *   However, a Wedge developer may not want to persist all those fields.
 *   Also, there may be fields have been declared as Interfaces and they
 *   should be persisted.
 */

package org.openmaji.implementation.server.meem.definition;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.meemplex.meem.Content;
import org.openmaji.implementation.server.Common;

import org.openmaji.meem.Facet;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.core.MeemCore;
import org.openmaji.system.request.RequestContext;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Peter
 */
public class WedgeIntrospector {
	private WedgeIntrospector() {
	}

	public static final String CORE_FIELD_NAME = "meemCore";

	public static final String CONTEXT_FIELD_NAME = "meemContext";

	public static final String REQUEST_CONTEXT_FIELD_NAME = "requestContext";

	public static final Class<?> CORE_FIELD_CLASS = MeemCore.class;

	public static final Class<?> CONTEXT_FIELD_CLASS = MeemContext.class;

	public static final Class<?> REQUEST_CONTEXT_FIELD_CLASS = RequestContext.class;

	public static final HashSet<String> IGNORED_FIELDS = new HashSet<String>();
	static {
		IGNORED_FIELDS.add(CORE_FIELD_NAME);
		IGNORED_FIELDS.add(CONTEXT_FIELD_NAME);
		IGNORED_FIELDS.add(REQUEST_CONTEXT_FIELD_NAME);
	}

	public static void addFacetDefToWedgeDef(WedgeDefinition wedgeDefinition, Direction facetDirection, String facetIdentifier, String fieldName, Class<?> facetClass) {
		FacetAttribute facetAttribute;
		if (facetDirection == Direction.INBOUND) {
			facetAttribute = new FacetInboundAttribute(facetIdentifier, facetClass.getName());
		}
		else {
			facetAttribute = new FacetOutboundAttribute(facetIdentifier, facetClass.getName(), fieldName);
		}

		FacetDefinition facetDefinition = new FacetDefinition(facetAttribute);

		wedgeDefinition.addFacetDefinition(facetDefinition);
	}

	/**
	 * Get simple name of class
	 * 
	 * @param facetClass
	 * @return
	 */
	public static String shortClassName(Class<?> facetClass) {
		int truncatePosition = facetClass.getName().lastIndexOf('.') + 1;
		String shortClassName = facetClass.getName().substring(truncatePosition);
		shortClassName = Character.toLowerCase(shortClassName.charAt(0)) + shortClassName.substring(1);
		return shortClassName;
	}

	/**
	 * 
	 * @param wedgeClass
	 * @return
	 * @throws WedgeIntrospectorException
	 */
	public static WedgeDefinition getWedgeDefinition(Class<?> wedgeClass) throws WedgeIntrospectorException {
		if (Common.TRACE_ENABLED && Common.TRACE_WEDGE_INTROSPECTOR) {
			logger.log(Common.getLogLevelVerbose(), "Build WedgeDefinition: " + wedgeClass.getName());
		}

//		HashSet<String> facetsAdded = new HashSet<String>(); // Avoid adding duplicate Facets

		WedgeDefinition wedgeDefinition = getCache(wedgeClass);

		if (wedgeDefinition != null) {
			// TODO return copy of cached wedge definition
			return wedgeDefinition;
		}

		// Check that the wedge class is public and non-abstract

		int wedgeModifiers = wedgeClass.getModifiers();
		if (!Modifier.isPublic(wedgeModifiers) || Modifier.isAbstract(wedgeModifiers)) {
			String message = "Wedge must be a 'public', non-'abstract' class: " + wedgeClass.getName();
			logger.log(Level.WARNING, message);
			throw new WedgeIntrospectorException(message);
		}

		// Check that the wedge class has a public, no-arg constructor

		try {
			wedgeClass.getConstructor((Class[]) null);
		}
		catch (NoSuchMethodException e) {
			String message = "Wedge must implement a 'public' default constructor: " + wedgeClass.getName();
			logger.log(Level.WARNING, message);
			throw new WedgeIntrospectorException(message, e);
		}

		// Check that the wedge class does not override finalize()

		try {
			Method finalizeMethod = wedgeClass.getDeclaredMethod("finalize", (Class[]) null);

			if (finalizeMethod.getDeclaringClass() != Object.class) {
				String message = "Wedge must not override finalize() from " + Object.class.getName() + ": " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}
		}
		catch (NoSuchMethodException e) {
		}

		// Issue a warning if the wedge class does not implement the Wedge marker interface

		if (!Wedge.class.isAssignableFrom(wedgeClass)) {
			logger.log(Level.WARNING, "Wedge should implement " + Wedge.class.getName() + ": " + wedgeClass.getName());
		}

		// Check that any context field is correctly declared

		try {
			Field contextField = wedgeClass.getDeclaredField(CORE_FIELD_NAME);
			int modifiers = contextField.getModifiers();

			if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				String message = "The context field '" + CORE_FIELD_NAME + "' must be 'public', non-'static' and non-'final': " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			if (contextField.getType() != CORE_FIELD_CLASS) {
				String message = "The context field '" + CORE_FIELD_NAME + "' must be of type " + CORE_FIELD_CLASS.getName() + ": " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}
		}
		catch (NoSuchFieldException e) {
			// This is fine, it just means there is no context field
		}

		try {
			Field contextField = wedgeClass.getDeclaredField(CONTEXT_FIELD_NAME);
			int modifiers = contextField.getModifiers();

			if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				String message = "The context field '" + CONTEXT_FIELD_NAME + "' must be 'public', non-'static' and non-'final': " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			if (contextField.getType() != CONTEXT_FIELD_CLASS) {
				String message = "The context field '" + CONTEXT_FIELD_NAME + "' must be of type " + CONTEXT_FIELD_CLASS.getName() + ": " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}
		}
		catch (NoSuchFieldException e) {
			// This is fine, it just means there is no context field
		}

		try {
			Field contextField = wedgeClass.getDeclaredField(REQUEST_CONTEXT_FIELD_NAME);
			int modifiers = contextField.getModifiers();

			if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				String message = "The context field '" + REQUEST_CONTEXT_FIELD_NAME + "' must be 'public', non-'static' and non-'final': " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			if (contextField.getType() != REQUEST_CONTEXT_FIELD_CLASS) {
				String message = "The context field '" + REQUEST_CONTEXT_FIELD_NAME + "' must be of type " + REQUEST_CONTEXT_FIELD_CLASS.getName() + ": " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}
		}
		catch (NoSuchFieldException e) {
			// This is fine, it just means there is no context field
		}

		//
		// check that lifeCycleConduit is present if commence/conclude are defined
		//

		try {
			wedgeClass.getMethod("commence", (Class<?>) null);

			wedgeClass.getField("lifeCycleClientConduit");
		}
		catch (NoSuchFieldException e) {
			logger.log(Level.WARNING, "commence found but no lifeCycleConduit in " + wedgeClass.getName());
		}
		catch (NoSuchMethodException e) {
			// ignore.
		}

		try {
			wedgeClass.getMethod("conclude", (Class<?>[]) null);

			wedgeClass.getField("lifeCycleClientConduit");
		}
		catch (NoSuchFieldException e) {
			logger.log(Level.WARNING, "conclude found but no lifeCycleConduit in " + wedgeClass.getName());
		}
		catch (NoSuchMethodException e) {
			// ignore.
		}

		// Search for a manual wedge definition in a public static field named WEDGE_DEFINITION

		try {
			Field manualWedgeDefinition = wedgeClass.getField("WEDGE_DEFINITION");
			int modifiers = manualWedgeDefinition.getModifiers();

			if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
				String message = "Manual WEDGE_DEFINITION must be 'public', 'static' and 'final': " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			if (!WedgeDefinition.class.isAssignableFrom(manualWedgeDefinition.getType())) {
				String message = "Manual WEDGE_DEFINITION must be of type " + WedgeDefinition.class.getName() + ": " + wedgeClass.getName();
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			wedgeDefinition = (WedgeDefinition) manualWedgeDefinition.get(wedgeClass);

			try {
				verifyWedgeDefinition(wedgeClass, wedgeDefinition);
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Manual WEDGE_DEFINITION failed verification: " + wedgeClass.getName(), e);
			}

			if (Common.TRACE_ENABLED && Common.TRACE_WEDGE_INTROSPECTOR) {
				logger.log(Common.getLogLevelVerbose(), wedgeClass.getName() + ": Using manually supplied wedge definition");
			}

			putCache(wedgeClass, wedgeDefinition);

			return wedgeDefinition;
		}
		catch (NoSuchFieldException e) {
			// This is fine, it just means there is no manual definition
		}
		catch (IllegalAccessException e) {
			String message = "Unexpected error trying to read manual wedge definition: " + wedgeClass.getName();
			logger.log(Level.WARNING, message);
			throw new WedgeIntrospectorException(message, e);
		}

		// Discover the definition from the class definition

		WedgeAttribute wedgeAttribute = new WedgeAttribute();
		wedgeAttribute.setImplementationClassName(wedgeClass.getName());

		wedgeDefinition = new WedgeDefinition(wedgeAttribute);

		// Look at each field in the class and infer facets, content and configuration properties of the wedge
		processFields(wedgeClass, wedgeDefinition);

		putCache(wedgeClass, wedgeDefinition);

		return wedgeDefinition;
	}

	/**
	 * Verify a WedgeDefinition
	 * 
	 * @param wedgeClass
	 * @param wedgeDefinition
	 * @throws WedgeIntrospectorException
	 */
	public static void verifyWedgeDefinition(Class<?> wedgeClass, WedgeDefinition wedgeDefinition) throws WedgeIntrospectorException {
		WedgeAttribute wedgeAttribute = wedgeDefinition.getWedgeAttribute();

		verifyPersistentFields(wedgeClass, wedgeAttribute.getPersistentFields());
		verifyFacetDefinitions(wedgeClass, wedgeDefinition.getFacetDefinitions());
	}

	private static void verifyPersistentFields(Class<?> wedgeClass, Collection<String> fields) throws WedgeIntrospectorException {
		Iterator<String> fieldsIter = fields.iterator();
		while (fieldsIter.hasNext()) {
			String fieldName = fieldsIter.next();
			verifyPersistentField(wedgeClass, fieldName);
		}
	}

	private static void verifyPersistentField(Class<?> wedgeClass, String fieldName) throws WedgeIntrospectorException {
		try {
			Field wedgeField = wedgeClass.getField(fieldName);
			int modifiers = wedgeField.getModifiers();

			if (Modifier.isStatic(modifiers)) {
				String message = "Verification Failure: Persistent Field [" + fieldName + "] in " + wedgeClass.getName() + " must be non-static";

				throw new WedgeIntrospectorException(message);
			}

			if (Modifier.isFinal(modifiers)) {
				String message = "Verification Failure: Persistent Field [" + fieldName + "] in " + wedgeClass.getName() + " must be non-final";

				throw new WedgeIntrospectorException(message);
			}

			Class<?> wedgeFieldClass = wedgeField.getType();

			if (wedgeFieldClass.isInterface() && Facet.class.isAssignableFrom(wedgeFieldClass)) {
				String message = "Verification Failure: Persistent Field [" + fieldName + "] in " + wedgeClass.getName() + " must not be a Facet";

				throw new WedgeIntrospectorException(message);
			}
		}
		catch (NoSuchFieldException nsfe) {
			String message = "Verification Failure: " + wedgeClass.getName() + " has no field [" + fieldName + "]";

			throw new WedgeIntrospectorException(message);
		}
	}

	private static void verifyFacetDefinitions(Class<?> wedgeClass, Collection<FacetDefinition> facets) throws WedgeIntrospectorException {
		for (FacetDefinition facetDefinition : facets) {
			verifyFacetDefinition(wedgeClass, facetDefinition);
		}
	}

	private static void verifyFacetDefinition(Class<?> wedgeClass, FacetDefinition facetDefinition) throws WedgeIntrospectorException {
		FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();

		if (facetAttribute.isDirection(Direction.INBOUND)) {
			verifyInboundFacetAttribute(wedgeClass, (FacetInboundAttribute) facetAttribute);
		}
		else {
			verifyOutboundFacetAttribute(wedgeClass, (FacetOutboundAttribute) facetAttribute);
		}
	}

	private static void verifyInboundFacetAttribute(Class<?> wedgeClass, FacetInboundAttribute inboundFacetAttribute) throws WedgeIntrospectorException {
		String facetClassName = inboundFacetAttribute.getInterfaceName();

		try {
			Class<?> facetClass = Class.forName(facetClassName);

			if (!facetClass.isAssignableFrom(wedgeClass)) {
				String message = "Verification Failure: " + wedgeClass.getName() + " must implement [" + facetClass.getName() + "]";

				throw new WedgeIntrospectorException(message);
			}
		}
		catch (ClassNotFoundException cnfe) {
			String message = "Verification Failure: Inbound Facet [" + inboundFacetAttribute.getIdentifier() + "] has an unresolvable interface name";

			throw new WedgeIntrospectorException(message, cnfe);
		}
	}

	private static void verifyOutboundFacetAttribute(Class<?> wedgeClass, FacetOutboundAttribute outboundFacetAttribute) throws WedgeIntrospectorException {
		String facetClassName = outboundFacetAttribute.getInterfaceName();
		String facetIdentifier = outboundFacetAttribute.getIdentifier();
		String facetPublicName = outboundFacetAttribute.getWedgePublicFieldName();

		try {
			Class<?> facetClass = Class.forName(facetClassName);

			Field facetField = wedgeClass.getField(facetPublicName);
			int modifiers = facetField.getModifiers();

			if (Modifier.isStatic(modifiers)) {
				String message = "Verification Failure: Outbound Facet field [" + facetPublicName + "] in " + wedgeClass.getName() + " must be non-static";

				throw new WedgeIntrospectorException(message);
			}

			if (Modifier.isFinal(modifiers)) {
				String message = "Verification Failure: Outbound Facet field [" + facetPublicName + "] in " + wedgeClass.getName() + " must be non-final";

				throw new WedgeIntrospectorException(message);
			}

			Class<?> facetFieldClass = facetField.getType();

			if (!facetFieldClass.isAssignableFrom(facetClass)) {
				String message = "Verification Failure: Outbound Facet field [" + facetPublicName + "] in " + wedgeClass.getName() + " must be assignable from [" + facetClass.getName() + "]";

				throw new WedgeIntrospectorException(message);
			}
		}
		catch (ClassNotFoundException cnfe) {
			String message = "Verification Failure: Outbound Facet [" + facetIdentifier + "] has an unresolvable interface name";

			throw new WedgeIntrospectorException(message, cnfe);
		}
		catch (NoSuchFieldException nsfe) {
			String message = "Verification Failure: " + wedgeClass.getName() + " has no field [" + facetIdentifier + "]";

			throw new WedgeIntrospectorException(message);
		}
	}
	
	/**
	 * 
	 * @param wedgeDefinition
	 * @param wedgeFields
	 * @param facets
	 * @throws WedgeIntrospectorException
	 */
	private static void processFields(Class<?> wedgeClass, WedgeDefinition wedgeDefinition) 
		throws WedgeIntrospectorException 
	{
		HashSet<String> facets = new HashSet<String>();
		Field[] wedgeFields = wedgeClass.getFields();
		
		WedgeAttribute wedgeAttribute = wedgeDefinition.getWedgeAttribute();
		String wedgeClassName = wedgeAttribute.getImplementationClassName();
	
		// Search the Interfaces implemented by this Class for in-bound Facets

		// TODO Allow inbound facets to be fields annotated with @Facet.
		{
			Set<Class<?>> facetClasses = FacetIntrospector.getFacetClasses(wedgeClass);
			for (Class<?> facetClass : facetClasses) {
				String facetIdentifier = shortClassName(facetClass);

				if (facets.contains(facetIdentifier)) {
					String message = "Wedge '" + wedgeClassName + "' has 2 facets with the identifier '" + facetIdentifier + "'";
					logger.log(Level.WARNING, message);
					throw new WedgeIntrospectorException(message);
				}
				else {
					if (Common.TRACE_ENABLED && Common.TRACE_WEDGE_INTROSPECTOR) {
						logger.log(Common.getLogLevelVerbose(), wedgeClassName + ": Found facet: " + Direction.INBOUND + "/" + facetIdentifier + "/" + facetClass.getName());
					}

					addFacetDefToWedgeDef(wedgeDefinition, Direction.INBOUND, facetIdentifier, facetIdentifier, facetClass);

					facets.add(facetIdentifier);
				}
			}
		}

		for (int i = 0; i < wedgeFields.length; ++i) {
			Field wedgeField = wedgeFields[i];

			// Ignore fields in IGNORED_FIELDS list and conduit fields
			String fieldName = wedgeField.getName();
			if (IGNORED_FIELDS.contains(fieldName) || fieldName.endsWith("Conduit") || fieldName.endsWith("Provider"))
				continue;

			// Ignore static fields
			int modifiers = wedgeField.getModifiers();
			if (Modifier.isStatic(modifiers))
				continue;

			// Ignore transient fields
			if (Modifier.isTransient(modifiers))
				continue;

			// Fields cannot be final, since we need to manipulate them
			if (Modifier.isFinal(modifiers)) {
				String message = wedgeClassName + ": Facets and persistent fields may not be 'final': " + fieldName;
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			// If the field type is not a Facet interface, this is a persistent field

			Content contentAnnotation = wedgeField.getAnnotation(Content.class);
			if (contentAnnotation != null) {
				// is  a persisted field
				if (Common.TRACE_ENABLED && Common.TRACE_WEDGE_INTROSPECTOR) {
					logger.log(Common.getLogLevelVerbose(), wedgeClassName + ": Found persistent field: " + fieldName);
				}
				wedgeAttribute.addPersistentField(fieldName);
				continue;
			}
			
			Class<?> wedgeFieldClass = wedgeField.getType();
			if (!Facet.class.isAssignableFrom(wedgeFieldClass)) {
				if (Common.TRACE_ENABLED && Common.TRACE_WEDGE_INTROSPECTOR) {
					logger.log(Common.getLogLevelVerbose(), wedgeClassName + ": Found persistent field: " + fieldName);
				}
				wedgeAttribute.addPersistentField(fieldName);
				continue;
			}

			// If not a persistent field, must be an outbound facet
			String facetName = fieldName;

			org.meemplex.meem.Facet facetAnnotation = wedgeField.getAnnotation(org.meemplex.meem.Facet.class);
			if (facetAnnotation != null) {
				if (facetAnnotation.direction() == org.meemplex.service.model.Direction.IN) {
					String message = "Wedge '" + wedgeClassName + "' has incoming facet '" + fieldName + "' when expecting an outbound Facet";
					logger.log(Level.WARNING, message);
					throw new WedgeIntrospectorException(message);
				}
				if (facetAnnotation.name() != null) {
					facetName = facetAnnotation.name();
				}
			}
			
			if (Common.TRACE_ENABLED && Common.TRACE_WEDGE_INTROSPECTOR) {
				logger.log(Common.getLogLevelVerbose(), wedgeClassName + ": Found facet: " + Direction.OUTBOUND + "/" + fieldName + "/" + wedgeFieldClass.getName());
			}

			if (facets.contains(fieldName)) {
				String message = "Wedge '" + wedgeClassName + "' has outgoing facet '" + fieldName + "' with same identifier as an incoming facet";
				logger.log(Level.WARNING, message);
				throw new WedgeIntrospectorException(message);
			}

			addFacetDefToWedgeDef(wedgeDefinition, Direction.OUTBOUND, facetName, fieldName, wedgeFieldClass);
		}
	}


	private static synchronized void putCache(Class<?> wedgeClass, WedgeDefinition wedgeDefinition) {
		if (cache != null) {
			WeakReference<WedgeDefinition> entry = new WeakReference<WedgeDefinition>(wedgeDefinition);

			cache.put(wedgeClass, entry);
		}
	}

	private static synchronized WedgeDefinition getCache(Class<?> wedgeClass) {
		if (cache != null) {
			WeakReference<WedgeDefinition> entry = cache.get(wedgeClass);

			if (entry != null) {
				WedgeDefinition wedgeDefinition = entry.get();

				if (wedgeDefinition != null) {
					return wedgeDefinition;
				}

				cache.remove(wedgeClass);
			}
		}

		return null;
	}

	/** cache for wedge definitions for classes */
	private static final Map<Class<?>, WeakReference<WedgeDefinition>> cache = null;// new WeakHashMap();

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static final Logger logger = Logger.getAnonymousLogger();
}
