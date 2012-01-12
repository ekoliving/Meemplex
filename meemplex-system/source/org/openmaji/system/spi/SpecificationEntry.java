/*
 * @(#)SpecificationEntry.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 *
 * - Implement equals() and hashCode() methods.
 */

package org.openmaji.system.spi;

import java.lang.reflect.Method;

/**
 * <p>
 * Specification entry for a Maji service.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class SpecificationEntry {

	private static String SPI_CLASSNAME = "$spi";

	private String identifier = null; // It's okay for this to remain "null"

	private Class<?> specification;

	private SpecificationType specificationType;

	private Class<?> implementation;

	public SpecificationEntry(Class<?> specification, SpecificationType specificationType, Class<?> implementation) {

		this.specification = specification;
		this.specificationType = specificationType;
		this.implementation = implementation;

		// ------------------------------------------------------------
		// If <something>.spi.getIdentifier() is defined, then use it !

		try {
			String nestedSpiClassname = specification.getName() + SPI_CLASSNAME;

			Class<?>[] nestedClasses = specification.getClasses();

			for (int index = 0; index < nestedClasses.length; index++) {
				if (nestedClasses[index].getName().equals(nestedSpiClassname)) {
					Class<?> nestedSpiClass = nestedClasses[index];

					Method getIdentifierMethod = nestedSpiClass.getMethod("getIdentifier", (Class[]) null);

					if (getIdentifierMethod != null) {
						identifier = (String) getIdentifierMethod.invoke(nestedSpiClass, (Object[]) null);
					}

					break;
				}
			}
		}
		catch (Exception exception) {
			// It's okay to ignore all Exceptions.
			// The specification class either has a correctly
			// defined <something>.spi.getIdentifier() method or not.
		}
	}

	public String getIdentifier() {
		return (identifier);
	}

	public Class<?> getSpecification() {
		return (specification);
	}

	public SpecificationType getSpecificationType() {
		return (specificationType);
	}

	public Class<?> getImplementation() {
		return (implementation);
	}

	public String toString() {
		return ("SpecificationEntry[" + "identifier=" + identifier + ", specification=" + specification + ", specificationType=" + specificationType + ", implementation=" + implementation + "]");
	}
}