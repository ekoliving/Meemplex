/*
 * @(#)FacetIntrospector.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
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
	
	public static Set<Class<?>> getFacetClasses(Class<?> objectClass) {
		Set<Class<?>> facetClasses = new HashSet<Class<?>>();

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
