/*
 * @(#)SpecificationEntry.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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

public class SpecificationEntry <T> {

	private static String SPI_CLASSNAME = "$spi";

	private String identifier = null; // It's okay for this to remain "null"

	/**
	 * Specification class.  An interface or abstract class.
	 */
	private Class<T> specification;

	private SpecificationType specificationType;

	/**
	 * Implementation class.  A class that extends or implements the specification class.
	 */
	private Class<T> implementation;

	@SuppressWarnings("unchecked")
	public SpecificationEntry(Class<T> specification, SpecificationType specificationType, Class<? extends T> implementation) {

		this.specification = specification;
		this.specificationType = specificationType;
		this.implementation = (Class<T>) implementation;

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

	public Class<T> getSpecification() {
		return (specification);
	}

	public SpecificationType getSpecificationType() {
		return (specificationType);
	}

	public Class<T> getImplementation() {
		return (implementation);
	}

	public String toString() {
		return ("SpecificationEntry[" + "identifier=" + identifier + ", specification=" + specification + ", specificationType=" + specificationType + ", implementation=" + implementation + "]");
	}
}