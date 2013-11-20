/*
 * @(#)MeemUtility.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import org.openmaji.meem.definition.*;
import org.openmaji.utility.CollectionUtility;

/**
 * <p>
 * MeemUtility is a collection of methods for managing Meems.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-04-10)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public class MeemUtility {

  /**
   * Template for Collection.toArray() conversion
   */

  private static final Class[] classArrayTemplate = new Class[0];

  /**
   * Get all the Java interfaces (as an array) from a MeemDefinition.
   * Optionally, a preferred Facet Direction can be specified.
   *
   * @param meemDefinition Description of a Meem
   * @param direction      Optional Direction of Facets to use
   * @return Array of Java interfaces for a given MeemDefinition and Direction
   * @exception IllegalArgumentException Problem loading an interface
   */

  public static final synchronized Class<?>[] getMeemInterfaces(
    MeemDefinition meemDefinition,
    Direction      direction)
    throws         ClassNotFoundException, IllegalArgumentException {

    Collection interfaces = CollectionUtility.createVector();

    Iterator wedgeDefinitions = meemDefinition.getWedgeDefinitions().iterator();

    while (wedgeDefinitions.hasNext()) {
      WedgeDefinition wedgeDefinition =
        (WedgeDefinition) wedgeDefinitions.next();

      interfaces.addAll(getInterfaces(wedgeDefinition, direction));
    }

    return((Class[]) interfaces.toArray(classArrayTemplate));
  }

  /**
   * Get all the Java interfaces (as an array) from a WedgeDefinition.
   * Optionally, a preferred Facet Direction can be specified.
   *
   * @param wedgeDefinition Description of a Wedge
   * @param direction       Optional Direction of Facets to use
   * @return Array of Java interfaces for a given WedgeDefinition and Direction
   * @exception IllegalArgumentException Problem loading an interface
   */

  public static final synchronized Class<?>[] getWedgeInterfaces(
    WedgeDefinition wedgeDefinition,
    Direction       direction)
    throws          ClassNotFoundException, IllegalArgumentException {

    Collection<Class<?>> interfaces = getInterfaces(wedgeDefinition, direction);

    return((Class[]) interfaces.toArray(classArrayTemplate));
  }

  /**
   * Get all the Java interfaces (as a Collection) from a WedgeDefinition.
   * Optionally, a preferred Facet Direction can be specified.
   *
   * @param wedgeDefinition Description of a Wedge
   * @param direction       Optional Direction of Facets to use
   * @return Collection of interfaces for a given WedgeDefinition and Direction
   * @exception IllegalArgumentException Problem loading an interface
   */

  private static final synchronized Collection<Class<?>> getInterfaces(
    WedgeDefinition wedgeDefinition,
    Direction       direction)
    throws          ClassNotFoundException, IllegalArgumentException {

	 List<Class<?>> interfaces = new ArrayList<Class<?>>();

    for (FacetDefinition facetDefinition :  wedgeDefinition.getFacetDefinitions()) {
      FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();

      if (direction != null) {
        if (facetAttribute.isDirection(direction) == false) continue;
      }

      String interfaceName = facetAttribute.getInterfaceName();

      interfaces.add(ObjectUtility.getClass(Object.class, interfaceName));
    }

    return(interfaces);
  }

  /**
   * Copy all the FacetDefinitions from one WedgeDefinition to another.
   *
   * @param sourceWedgeDefinition WedgeDefinition containing Facets to copy
   * @param targetWedgeDefinition WedgeDefinition that will have Facets added
   */

  public static void copyFacetDefinitions(
    WedgeDefinition sourceWedgeDefinition,
    WedgeDefinition targetWedgeDefinition) {

    Iterator facetIterator = sourceWedgeDefinition.getFacetDefinitions().iterator();

    while (facetIterator.hasNext()) {
      targetWedgeDefinition.addFacetDefinition(
        (FacetDefinition) facetIterator.next()
      );
    }
  }
}
