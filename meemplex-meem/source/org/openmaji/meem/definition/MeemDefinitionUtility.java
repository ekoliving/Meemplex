/*
 * @(#)MeemDefinitionUtility.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

import java.util.logging.Logger;

/**
 * <p>
 * General utility class for manipulating meem definitions.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public class MeemDefinitionUtility {

  private static final Logger logger = Logger.getAnonymousLogger();    

  /**
   * Rename a wedge in the passed in meem definition. Note that if the meem
   * definition contains multiple wedges with the same identifier only the
   * first one will be renamed.
   * 
   * @param meemDefinition     The meem definition containing the identified wedge.
   * @param oldWedgeIdentifier The original wedge identifier
   * @param newWedgeIdentifier The new wedge identifier
   */
  public static void renameWedgeIdentifier(
    MeemDefinition meemDefinition,
    String oldWedgeIdentifier,
    String newWedgeIdentifier) {

	WedgeDefinition wedgeDefinition = meemDefinition.getWedgeDefinition(oldWedgeIdentifier);
    if (wedgeDefinition == null) {
    	logger.info("renameWedgeIdentifier() - No wedge with identifier of '"+oldWedgeIdentifier+"'");
    }
    else {
        wedgeDefinition.getWedgeAttribute().setIdentifier(newWedgeIdentifier);
    }
  }

  /**
   * Rename a facet appearing in the passed in meem definition which is associated
   * with the given wedge identified by wedgeIdentifier. Note that if there are
   * multiple wedges with the same identifier only the first one will be renamed.
   * Also note that the oldFacetIdentifier argument should not contain the full
   * package name of the facet class but just the class name. For example if you
   * wish to rename a Binary inbound facet you should just use <b>Binary</b>
   * instead of <b>org.openmaji.common.Binary</b>.
   * 
   * @param meemDefinition the meem definition containing the identified wedge.
   * @param wedgeIdentifier the identifier for the wedge the facet is in.
   * @param oldFacetIdentifier the original facet identifier value.
   * @param newFacetIdentifier the new facet identifier value.
   */
  public static void renameFacetIdentifier(
    MeemDefinition meemDefinition,
    String         wedgeIdentifier,
    String         oldFacetIdentifier,
    String         newFacetIdentifier) {

	WedgeDefinition wedgeDefinition = meemDefinition.getWedgeDefinition(wedgeIdentifier);	
    if (wedgeDefinition == null) {
    	logger.info("renameFacetIdentifier() - No wedge with identifier of '"+wedgeIdentifier+"'");
    }
    else {
        WedgeDefinitionUtility.renameFacetIdentifier(
          wedgeDefinition, oldFacetIdentifier, newFacetIdentifier
        );
    }
  }
}