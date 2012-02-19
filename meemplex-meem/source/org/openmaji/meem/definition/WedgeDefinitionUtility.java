/*
 * @(#)WedgeDefinitionUtility.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

import java.util.logging.Logger;

/**
 * <p>
 * A utility class for manipulating wedge definitions.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public class WedgeDefinitionUtility {

  private static final Logger logger = Logger.getAnonymousLogger();    

  /**
   * Rename the facet identifier on the passed in wedge.
   * 
   * @param wedgeDefinition the wedge definition to be modified.
   * @param oldFacetIdentifier the old facet identifier.
   * @param newFacetIdentifier the new facet identifier.
   */
  public static void renameFacetIdentifier(
    WedgeDefinition wedgeDefinition,
    String          oldFacetIdentifier,
    String          newFacetIdentifier) {

	FacetDefinition facetDefinition = wedgeDefinition.getFacetDefinition(oldFacetIdentifier);
	if (facetDefinition == null) {
		logger.info("renameFacetIdentifier() - No facet with identifier of '"+oldFacetIdentifier+"'");		
	}
	else {
	      FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();          
	      facetAttribute.setIdentifier(newFacetIdentifier);
	}
  }
}