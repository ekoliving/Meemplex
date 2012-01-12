/*
 * @(#)DefinitionFactoryImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Cache WedgeDefinitions created by WedgeIntrospector, for performance.
 *
 * - Put support into AbstractFactory, so that you can get an instance of
 *   this class as a singleton.  To reduce superfluous object creation.
 *
 * - Consider validating WedgeDefinition created by the WedgeIntrospector.
 */

package org.openmaji.implementation.server.meem.definition;

import java.util.Iterator;
import java.util.logging.Logger;

import org.openmaji.implementation.server.utility.*;


import org.openmaji.meem.definition.*;
import org.openmaji.system.meem.definition.DefinitionFactory;
import org.openmaji.system.spi.MajiSystemProvider;

/**
 * <p>
 * DefinitionFactoryImpl provides a convenient means of creating complete
 * definitions for Meems or partial definitions for Wedges.
 * </p>
 * <p>
 * The techniques for determining the contents of a MeemDefinition or a
 * WedgeDefinition utilized here are based on introspection of the Wedge
 * class implementation.  The easy approach for the developer is to
 * allow the WedgeIntrospector to use reflection to provide a simple
 * definition.  If that isn't sufficient, then a more manually intensive
 * approach of hand-coding the required Meem, Wedge, Facet and Dependency
 * Definitions is supported, if needed.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-07-29)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.MeemDefinition
 * @see org.openmaji.meem.definition.WedgeAttribute
 * @see org.openmaji.meem.definition.WedgeDefinition
 * @see org.openmaji.meem.definition.FacetAttribute
 * @see org.openmaji.meem.definition.FacetDefinition
 */

public class DefinitionFactoryImpl implements DefinitionFactory {
	
	private static final Logger logger = Logger.getAnonymousLogger();

  /**
   * Create a MeemDefinition that is based on the Wedge implementation class
   * that is associated with the Meem's identifier.
   *
   * @param     meemIdentifier Identifier for the Meem
   * @return    MeemDefinition for the Meem specified by the identifier
   * @exception IllegalArgumentException Unknown Meem identifier
   */

  public MeemDefinition createMeemDefinition(
    String meemIdentifier) {

    MeemDefinition meemDefinition = new MeemDefinition(
      new MeemAttribute(meemIdentifier)
    );

    WedgeDefinition wedgeDefinition = createWedgeDefinition(meemIdentifier);
    meemDefinition.addWedgeDefinition(wedgeDefinition);

    return(meemDefinition);
  }

  /**
   * Create a MeemDefinition that is based on the Wedge implementation class.
   *
   * @param  meemIdentifier     Identifier for the Meem
   * @param  wedgeSpecification Class to be inspected for the MeemDefinition
   * @return MeemDefinition for the Meem specified by the Class
   */

  public MeemDefinition createMeemDefinition(
    String meemIdentifier,
    Class  wedgeSpecification) {

    MeemDefinition meemDefinition = new MeemDefinition(
      new MeemAttribute(meemIdentifier)
    );

    WedgeDefinition wedgeDefinition = createWedgeDefinition(wedgeSpecification);
    meemDefinition.addWedgeDefinition(wedgeDefinition);

    return(meemDefinition);
  }

  /**
   * <p>
   * Create a MeemDefinition that is composed of all the Wedges that are
   * specified by a list of either known Wedge identifiers or Wedge
   * implementation classes.
   * </p>
   * <p>
   * The Wedge iterator can provide a mixture of Wedge identifiers and/or
   * Wedge implementation classes.  This provides a reasonable amount of
   * flexibility for creating MeemDefinitions and, hopefully, isn't too
   * messy.
   * </p>
   * @param     wedgeIterator Specifies which Wedges to define
   * @return    MeemDefinition composed of the specified Wedge identifiers
   * @exception IllegalArgumentException Unknown Wedge identifier
   */

  public MeemDefinition createMeemDefinition(
    Iterator<?> wedgeIterator) {

    MeemDefinition meemDefinition = new MeemDefinition();

    while (wedgeIterator.hasNext()) {
      Object wedgeSpecifier = wedgeIterator.next();

      WedgeDefinition wedgeDefinition = null;

      if (wedgeSpecifier instanceof String) {
        wedgeDefinition = createWedgeDefinition((String) wedgeSpecifier);
      }
      else if (wedgeSpecifier instanceof Class) {
        wedgeDefinition = createWedgeDefinition((Class) wedgeSpecifier);
      }
      else {
        throw new IllegalArgumentException(
          "WedgeIterator item: " + wedgeSpecifier +
          " must be either an identifier String or a specification Class"
        );
      }

      meemDefinition.addWedgeDefinition(wedgeDefinition);
    }

    return(meemDefinition);
  }

  /**
   * Create a WedgeDefinition that is based on the Wedge implementation class
   * that is associated with the Wedge's identifier.
   */
  
  private static MajiSystemProvider majiSystemProvider = null;

  public WedgeDefinition createWedgeDefinition(String wedgeIdentifier) {
      
    if (majiSystemProvider == null) {
      majiSystemProvider = MajiSystemProvider.systemProvider();
    }

    Class<?> specification = majiSystemProvider.getSpecification(wedgeIdentifier);

    String wedgeImplementationName = majiSystemProvider.getSpecificationEntry(specification).getImplementation().getName();

    Class<?> wedgeSpecification = null;
    
    try {
    	wedgeSpecification = ObjectUtility.getClass(Object.class, wedgeImplementationName);
    }
    catch (ClassNotFoundException e) {
    	logger.info("could not locate class: " + wedgeImplementationName);
    }

    return(createWedgeDefinition(wedgeSpecification));
  }

  /**
   * Create a WedgeDefinition that is based on the Wedge implementation class.
   *
   * @param  wedgeSpecification Class to be inspected for the WedgeDefinition
   * @return WedgeDefinition for the Wedge specified by the Class
   */

  public WedgeDefinition createWedgeDefinition(Class<?> wedgeSpecification) {

    WedgeDefinition wedgeDefinition = null;

    try {
      wedgeDefinition =
        WedgeIntrospector.getWedgeDefinition(wedgeSpecification);
    }
    catch (WedgeIntrospectorException wedgeIntrospectorException) {
      throw new RuntimeException(
        "Failed to create wedge definition", wedgeIntrospectorException
      );
    }

    return(wedgeDefinition);
  }
}
