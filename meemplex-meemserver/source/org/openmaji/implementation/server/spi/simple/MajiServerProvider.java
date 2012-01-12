/*
 * @(#)MajiServerProvider.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */


package org.openmaji.implementation.server.spi.simple;

import java.lang.reflect.*;
import java.util.*;

import org.openmaji.system.spi.MajiSystemProvider;
import org.openmaji.system.spi.SpecificationEntry;
import org.openmaji.system.spi.SpecificationType;

/**
 * <p>
 * This is  ascaled-down version of the MajiServerProvider for doing things like constructing MeemPaths
 * outside of a full-blown Maji Server.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @author  Warren Bloomer
 * @version 1.0
 */

public class MajiServerProvider extends MajiSystemProvider {

  private static Hashtable<Class<?>, SpecificationEntry> specificationEntries = new Hashtable<Class<?>, SpecificationEntry>();

  private static boolean mutable = true;

  public MajiServerProvider() {
    MajiServerInitializer.initialize(this);  // TODO: Use Property here
    mutable = false;
  }

  public Object create(
    Class<?>    specification,
    Object[] args) {

    SpecificationEntry specificationEntry =
      (SpecificationEntry) specificationEntries.get(specification);

    if (specificationEntry == null) {
      throw new IllegalArgumentException(
        "Couldn't create unknown Specification: " + specification
      );
    }

    Object instance = null;

    try {
      Class<?> implementation = specificationEntry.getImplementation();

      if (args == null) {
        instance = implementation.newInstance();
      }
      else {
        Class<?>[] argsClasses = null;

        if (args.length > 0) {
          argsClasses = new Class[args.length];

          for (int index = 0; index < args.length; index ++) {
          	if (args[index] == null) {
          		argsClasses[index] = null;
          	} else {
          		argsClasses[index] = args[index].getClass();
          	}
          }
        }
        
        Constructor matchConstructor = null;
        Constructor[] constructors = implementation.getConstructors();
        
        for (int i = 0; i < constructors.length; i++) {
        	Constructor constructor = constructors[i];
        	Class[] parameters = constructor.getParameterTypes();
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
        
        
				try
				{
					if (matchConstructor == null) {
						matchConstructor = implementation.getConstructor(argsClasses);
					}
					
		      instance = matchConstructor.newInstance(args);
				}
				catch (NoSuchMethodException e)
				{
					//
					// look for a getInstance method
					//
					Method	method = implementation.getMethod("getInstance", argsClasses);
					
					instance = method.invoke(implementation, args);
				}
				catch (InvocationTargetException ex) {
					ex.getCause().printStackTrace();
		    }
      }
    }
    catch (Exception exception) {
    	exception.printStackTrace();
      throw new IllegalArgumentException(
        "Couldn't instantiate " + specification + ": " + exception
      );
    }

    return(instance);
  }

 public synchronized void addSpecificationEntry(
    SpecificationEntry specificationEntry)
    throws IllegalArgumentException, IllegalStateException {

    if (mutable == false) {
      throw new IllegalStateException(
        "MajiServerProvider is not mutable, when adding " +  specificationEntry
      );
    }

    Class specification = specificationEntry.getSpecification();

    if (specificationEntries.containsKey(specification)) {
      throw new IllegalArgumentException(
        "MajiServerProvider won't override existing: " +  specificationEntry
      );
    }

    specificationEntries.put(specification, specificationEntry);
  }

  public Class getImplementation(
    Class specification) {

    Class implementation = specification;

    if (specification.isInterface()) {
      SpecificationEntry specificationEntry =
        getSpecificationEntry(specification);

      if (specificationEntry != null) {
        implementation = specificationEntry.getImplementation();
      }
      else {
        throw new IllegalArgumentException(
          "Does not have an SPI defined implementation:" + specification
        );
      }
    }
    
    return(implementation);
  }    
    
  public Class getSpecification(
    String identifier) {
      
    if (identifier == null) {
      throw new IllegalArgumentException(
        "SpecificationEntry identifier must not be 'null'"
      );
    }

    Iterator iterator = specificationEntries.values().iterator();

    while (iterator.hasNext()) {
      SpecificationEntry specificationEntry =
        (SpecificationEntry) iterator.next();

      if (identifier.equals(specificationEntry.getIdentifier())) {
        return(specificationEntry.getSpecification());
      }
    }

    throw new IllegalArgumentException(
      "SpecificationEntry identifier not found: " + identifier
    );
  }

  public Collection<Class<?>> getSpecifications() {
    return(specificationEntries.keySet());
  }

  public Collection<Class<?>>  getSpecifications(
    SpecificationType specificationType) {

    Vector<Class<?>> specifications = new Vector<Class<?>>();

    Iterator iterator = specificationEntries.values().iterator();

    while (iterator.hasNext()) {
      SpecificationEntry specificationEntry =
        (SpecificationEntry) iterator.next();

      if (specificationEntry.getSpecificationType().equals(specificationType)) {
        specifications.add(specificationEntry.getSpecification());
      }
    }

    return(specifications);
  }
  
  public synchronized SpecificationEntry getSpecificationEntry(
    Class specification) {
  
    return((SpecificationEntry) specificationEntries.get(specification));    
  }

  public synchronized void removeSpecificationEntry(
    Class specification)
    throws IllegalArgumentException, IllegalStateException {

    if (mutable == false) {
      throw new IllegalStateException(
        "MajiServerProvider is not mutable, when removing " + specification
      );
    }

    if (specificationEntries.containsKey(specification) == false) {
      throw new IllegalArgumentException(
        "MajiServerProvider can't remote unknown: " +  specification
      );
    }

    specificationEntries.remove(specification);
  }

  public String toString() {
    return(
      "MajiServerProvider[Specifications=" + specificationEntries + "]"
    );
  }
}
