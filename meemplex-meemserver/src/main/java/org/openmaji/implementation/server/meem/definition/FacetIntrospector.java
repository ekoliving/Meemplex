/*
 * @(#)FacetIntrospector.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.definition;

import java.util.*;

import org.openmaji.meem.Facet;

/**
 * @author Peter
 */
public class FacetIntrospector {
	
	public static Set<Class<?>> getFacetClasses(String className) throws ClassNotFoundException {
		return getFacetClasses(Class.forName(className));
	}
	
	/**
	 * Search the Interfaces implemented by this Class for in-bound Facets
	 * 
	 * @param objectClass
	 * 
	 * @return
	 * 	Set of Facet classes for the class
	 */
	public static Set<Class<?>> getFacetClasses(Class<?> objectClass) {
		Set<Class<?>> facetClasses = new HashSet<Class<?>>();

		// TODO check for @Facet annotated Fields with IN direction

		if (objectClass.isInterface()) {
			addIfFacet(objectClass, facetClasses);
		}
		else {
			while (Facet.class.isAssignableFrom(objectClass)) {
				Class<?>[] interfaceClasses = objectClass.getInterfaces();

				for (int index = 0; index < interfaceClasses.length; ++index) {
					addIfFacet(interfaceClasses[index], facetClasses);
				}

				objectClass = objectClass.getSuperclass();
			}
		}

		return facetClasses;
	}

	private static void addIfFacet(Class<?> interfaceClass, Set<Class<?>> facetClasses) {
		if (Facet.class.isAssignableFrom(interfaceClass)) {
			facetClasses.add(interfaceClass);
		}
	}
}
