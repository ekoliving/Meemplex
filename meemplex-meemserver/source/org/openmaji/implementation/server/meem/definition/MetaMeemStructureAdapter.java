/*
 * @(#)MetaMeemStructureAdapter.java
 * Created on 18/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider using delegation to MetaMeemStructureAdapter, rather than an
 *   inheritance based approach.
 */

package org.openmaji.implementation.server.meem.definition;

import java.io.Serializable;

import org.openmaji.meem.definition.DependencyAttribute;
import org.openmaji.meem.definition.FacetAttribute;
import org.openmaji.meem.definition.MeemAttribute;
import org.openmaji.meem.definition.WedgeAttribute;
import org.openmaji.system.meem.definition.MeemStructure;
import org.openmaji.system.meem.definition.MetaMeem;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

/**
 * <code>MetaMeemStructureAdapter</code> provides an implementation of 
 * <code>MetaMeem</code> that works with a <code>MeemStructure</code>.
 * <p>
 * @author Kin Wong
 */
public class MetaMeemStructureAdapter implements MetaMeem {
	private static final Logger logger = LogFactory.getLogger();
	private static final boolean DEBUG = false;
	
  protected MeemStructure meemStructure;
	
	/**
	 * Constructs an instance of <code>MetaMeemStructureAdapter</code>.
	 * <p>
	 * @param structure The <code>MeemStructure</code> implementation for storing
	 * meem related attributes.
	 * @see MeemStructure
	 */
  public MetaMeemStructureAdapter(MeemStructure structure) {
    this.meemStructure = structure;
  }
	
	/**
	 * Called in all internal methods prior any operation. Override to provide 
	 * second stage initialization.
	 */	
  public void commence() {}

  /**
   * 
   */
  public void addDependencyAttribute(String facetId, DependencyAttribute dependencyAttribute) {
	  if (DEBUG) {
		  LogTools.info(logger, "addDependencyAttribute: " + facetId + " -> " + dependencyAttribute);
	  }
    commence();

    FacetAttribute facet = meemStructure.getFacetAttribute(facetId);
    if (facet == null) {
  	  if (DEBUG) {
		  LogTools.info(logger, "could not locate facetId: " + facetId );
	  }
      return;
    }
    meemStructure.add(facet, dependencyAttribute);
  }

  /**
   * 
   */
  public void addFacetAttribute(Serializable wedgeId, FacetAttribute facetAttribute) {
    commence();
    WedgeAttribute wedge = meemStructure.getWedgeAttribute(wedgeId);
    if (wedge == null) {
      return;
    }
    meemStructure.add(wedge, facetAttribute);
  }

  /**
   * 
   */
  public void addWedgeAttribute(WedgeAttribute wedgeAttribute) {
    commence();
    meemStructure.add(wedgeAttribute);
  }

  /**
   * 
   */
  public void removeDependencyAttribute(Serializable dependencyId) {
	  if (DEBUG) {
		  LogTools.info(logger, "removeDependencyAttribute: " + dependencyId);
	  }
    commence();
    DependencyAttribute dependency = meemStructure.getDependencyAttribute(dependencyId);
    if (dependency == null) {
      return;
    }
    meemStructure.remove(dependency);
  }

  public void removeDependency(DependencyAttribute dependency) {
	  if (DEBUG) {
		  LogTools.info(logger, "removeDependencyAttribute: " + dependency);
	  }
	    commence();
	    if (dependency == null) {
	      return;
	    }
	    meemStructure.remove(dependency);
	  }

  /**
   * 
   */
  public void removeFacetAttribute(String facetId) {
    commence();
    FacetAttribute facet = meemStructure.getFacetAttribute(facetId);
    if (facet == null) {
      return;
    }
    meemStructure.remove(facet);
  }

  /**
   * 
   */
  public void removeWedgeAttribute(Serializable wedgeId) {
    commence();
    WedgeAttribute wedge = meemStructure.getWedgeAttribute(wedgeId);
    if (wedge == null) {
      return;
    }
    meemStructure.remove(wedge);
  }

  /**
   * 
   */
  public void setMeemAttribute(MeemAttribute meemAttribute) {
    commence();
    meemStructure.set(meemAttribute);
  }

  /**
   * 
   */
  public void updateDependencyAttribute(DependencyAttribute dependencyAttribute) {
	  if (DEBUG) {
		  LogTools.info(logger, "updateDependencyAttribute: " + dependencyAttribute);
	  }
    commence();
    meemStructure.update(dependencyAttribute);
  }

  /**
   * 
   */
  public void updateFacetAttribute(FacetAttribute facetAttribute) {
    commence();
    meemStructure.update(facetAttribute);
  }

  /**
   * 
   */
  public void updateMeemAttribute(MeemAttribute meemAttribute) {
    commence();
    meemStructure.set(meemAttribute);
  }

  /**
   * 
   */
  public void updateWedgeAttribute(WedgeAttribute wedgeAttribute) {
    commence();
    meemStructure.update(wedgeAttribute);
  }
}
