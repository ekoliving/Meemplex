/*
 * @(#)MajiServerProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 *
 * - Turn this into a singleton Meem ...
 *   - Inbound Facet:  create(), add/removeSpecificationEntry()
 *   - Outbound Facet: specificationEntryAdded/Removed(), specificationCount()
 *
 * - Make searching for SpecificationEntries by SpecificationType or by
 *   identifier better than using a linear search.
 */

package org.openmaji.implementation.server.spi;

import java.lang.reflect.*;
import java.util.*;

import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationEntry;
import org.openmaji.system.spi.SpecificationType;

/**
 * <p>
 * ...
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * 
 * @author Andy Gelme
 * @version 1.0
 */

public class MajiServerProvider extends MajiSystemProvider {

	private static Hashtable<Class<?>, SpecificationEntry> specificationEntries = new Hashtable<Class<?>, SpecificationEntry>();

	private static boolean mutable = true;

	public MajiServerProvider() {
		MajiServerInitializer.initialize(this); // TODO: Use Property here
		mutable = false;
	}

	/**
	 * Construct a new instance of the implementation for the "specification"
	 */
	public <T> T create(Class<T> specification, Object[] args) {

		SpecificationEntry<T> specificationEntry = (SpecificationEntry<T>) specificationEntries.get(specification);

		if (specificationEntry == null) {
			throw new IllegalArgumentException("Couldn't create unknown Specification: " + specification);
		}

		T instance = null;

		try {
			Class<T> implementation = specificationEntry.getImplementation();

			if (args == null) {
				instance = implementation.newInstance();
			}
			else {
				Class<?>[] argsClasses = null;

				if (args.length > 0) {
					argsClasses = new Class[args.length];

					for (int index = 0; index < args.length; index++) {
						if (args[index] == null) {
							argsClasses[index] = null;
						}
						else {
							argsClasses[index] = args[index].getClass();
						}
					}
				}

				Constructor<T> matchConstructor = null;
				Constructor<T>[] constructors = (Constructor<T>[]) implementation.getConstructors();

				for (int i = 0; i < constructors.length; i++) {
					Constructor<T> constructor = constructors[i];
					Class<?>[] parameters = constructor.getParameterTypes();
					if (parameters.length != argsClasses.length) {
						continue;
					}
					boolean found = true;
					for (int j = 0; j < parameters.length; j++) {
						Class<?> class1 = parameters[j];
						if (argsClasses[j] != null && !class1.isAssignableFrom(argsClasses[j])) {
							found = false;
							break;
						}
					}
					if (found) {
						matchConstructor = constructor;
						break;
					}
				}

				try {
					if (matchConstructor == null) {
						matchConstructor = implementation.getConstructor(argsClasses);
					}
					instance = matchConstructor.newInstance(args);
				}
				catch (NoSuchMethodException e) {
					//
					// look for a getInstance method
					//
					Method method = implementation.getMethod("getInstance", argsClasses);
					instance = (T) method.invoke(implementation, args);
				}
				catch (InvocationTargetException ex) {
					ex.getCause().printStackTrace();
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
			throw new IllegalArgumentException("Couldn't instantiate " + specification + ": " + exception, exception);
		}

		return (instance);
	}

	/**
	 * 
	 */
	public synchronized void addSpecificationEntry(SpecificationEntry specificationEntry) throws IllegalArgumentException, IllegalStateException {

		if (mutable == false) {
			throw new IllegalStateException("MajiServerProvider is not mutable, when adding " + specificationEntry);
		}

		Class<?> specification = specificationEntry.getSpecification();

		if (specificationEntries.containsKey(specification)) {
			throw new IllegalArgumentException("MajiServerProvider won't override existing: " + specificationEntry);
		}

		specificationEntries.put(specification, specificationEntry);
	}
	
	/**
	 * 
	 */
	public Class<?> getImplementation(Class<?> specification) {

		Class<?> implementation = specification;

		if (specification.isInterface()) {
			SpecificationEntry specificationEntry = getSpecificationEntry(specification);

			if (specificationEntry != null) {
				implementation = specificationEntry.getImplementation();
			}
			else {
				throw new IllegalArgumentException("Does not have an SPI defined implementation:" + specification);
			}
		}

		return (implementation);
	}

	/**
	 * 
	 */
	public Class<?> getSpecification(String identifier) {
		if (identifier == null) {
			throw new IllegalArgumentException("SpecificationEntry identifier must not be 'null'");
		}

		for (SpecificationEntry specificationEntry : specificationEntries.values()) {
			if (identifier.equals(specificationEntry.getIdentifier())) {
				return (specificationEntry.getSpecification());
			}
		}

		throw new IllegalArgumentException("SpecificationEntry identifier not found: " + identifier);
	}

	/**
	 * 
	 */
	public Collection<Class<?>> getSpecifications() {
		return (specificationEntries.keySet());
	}

	/**
	 * 
	 */
	public Collection<Class<?>> getSpecifications(SpecificationType specificationType) {
		List<Class<?>> specifications = new ArrayList<Class<?>>();
		for (SpecificationEntry specificationEntry : specificationEntries.values()) {
			if (specificationEntry.getSpecificationType().equals(specificationType)) {
				specifications.add(specificationEntry.getSpecification());
			}
		}

		return (specifications);
	}

	/**
	 * 
	 */
	public synchronized SpecificationEntry getSpecificationEntry(Class<?> specification) {
		return ((SpecificationEntry) specificationEntries.get(specification));
	}

	/**
	 * 
	 */
	public synchronized void removeSpecificationEntry(Class<?> specification) throws IllegalArgumentException, IllegalStateException {
		if (mutable == false) {
			throw new IllegalStateException("MajiServerProvider is not mutable, when removing " + specification);
		}
		if (specificationEntries.containsKey(specification) == false) {
			throw new IllegalArgumentException("MajiServerProvider can't remote unknown: " + specification);
		}
		specificationEntries.remove(specification);
	}

	/**
	 * 
	 */
	public String toString() {
		return ("MajiServerProvider[Specifications=" + specificationEntries + "]");
	}
}
