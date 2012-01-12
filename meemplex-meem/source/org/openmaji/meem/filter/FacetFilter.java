/*
 * @(#)FacetFilter.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Check "isAssignableFrom(specification)" is around the right way !
 * - Add Direction as something that can be filtered.
 * - Allow wildcard filtering, e.g. can leave Direction null.
 */

package org.openmaji.meem.filter;

import java.io.Serializable;

import org.openmaji.meem.definition.Direction;


/**
 * <p>
 * A FacetFilter simply checks that a specified Facet identifier and
 * specification class are the same as those expected.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-03-11)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public final class FacetFilter implements Filter, Serializable {

	private static final long serialVersionUID = -1178365040590887015L;

  /**
   * Facet identifier to be compared - optional.
   */
  private String facetIdentifier;

  /**
   * Facet specification class to be compared - optional.
   */
  private Class<?> specification;
  
  /**
   * Direction of facet - optional.
   */
  private Direction	direction;

  /**
   * Create FacetFilter that looks for facets of a given specification
   *
   * @param specification   Expected Facet specification class
   */
  public FacetFilter(
    Class<?>  specification) 
  {
    this.facetIdentifier = null;
    this.specification   = specification;
    this.direction = null;
  }

  /**
   * Create FacetFilter that looks for facets of a given specification
   *
   * @param specification   Expected Facet specification class
   * @param direction			Direction of the Facet for the class of interest.
   */
  public FacetFilter(
	Class<?>  	specification,
	Direction	direction) 
  {
	this.facetIdentifier = null;
	this.specification   = specification;
	this.direction = direction;
  }

  /**
   * <p>
   * Create FacetFilter.
   * </p>
   * @param facetIdentifier Expected Facet identifier
   * @param specification   Expected Facet specification class
   */
  public FacetFilter(
	String facetIdentifier,
	Class<?>  specification) {

	this.facetIdentifier = facetIdentifier;
	this.specification   = specification;
	this.direction = null;
  }
  
  /**
   * <p>
   * Create FacetFilter.
   * </p>
   * @param facetIdentifier Expected Facet identifier
   */
  public FacetFilter(
	String facetIdentifier) {

	this.facetIdentifier = facetIdentifier;
	this.specification   = null;
	this.direction = null;
  }
  
  /**
   * <p>
   * Create FacetFilter.
   * </p>
   * @param facetIdentifier	Expected Facet identifier
   * @param specification	Expected Facet specification class
   * @param direction		Direction of the Facet for the class of interest.
   */
  public FacetFilter(
	String facetIdentifier,
	Class  specification,
	Direction direction) {

	this.facetIdentifier = facetIdentifier;
	this.specification   = specification;
	this.direction = direction;
  }

  /**
   * <p>
   * Comparision with the specified Facet identifier and specification class.
   * The result is true, if and only if both the Facet identifier and
   * specification class are matched.
   * </p>
   * @param facetIdentifier Facet identifier to match
   * @param specification   Facet specification class to match
   * @return True, if Facet identifier and specification class match
   */
  public boolean match(
	String 		facetIdentifier,
	Class<?>  	specification)
  {

	if (this.facetIdentifier != null && !this.facetIdentifier.equals(facetIdentifier))
	{
		return false;
	}
    
	if (this.specification != null && !this.specification.isAssignableFrom(specification)) 
	{
	  return false;
	}
    
	return true;
  }
  
  /**
   * <p>
   * Comparision with the specified Facet identifier and specification class.
   * The result is true, if and only if both the Facet identifier and
   * specification class and direction are matched.
   * </p>
   * @param facetIdentifier Facet identifier to match
   * @param specification   Facet specification class to match
   * @param direction          the direction of the facet.
   * 
   * @return True, if Facet identifier and specification class match
   */
  public boolean match(
    String 		facetIdentifier,
    Class  	specification,
    Direction	direction) 
  {

    if (this.facetIdentifier != null && !this.facetIdentifier.equals(facetIdentifier))
    {
    	return false;
    }
    
    if (this.specification != null && !this.specification.isAssignableFrom(specification)) 
    {
      return false;
    }
    
    if (this.direction != null && !this.direction.equals(direction))
    {
    	return false;
    }

    return true;
  }

  /**
   * <p>
   * Provides a String representation of FacetFilter.
   * </p>
   * @return String representation of FacetFilter
   */

  public String toString() {
    return(
      getClass().getName() + "[" +
        "facetIdentifier=" + facetIdentifier +
        ", specification=" + specification   +
        ", direction=" + direction +
      "]"
    );
  }
  
  /**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hashCode = facetIdentifier.hashCode();
		if (specification != null) hashCode ^= specification.hashCode();
		if (direction != null) hashCode ^= direction.hashCode();

		return hashCode;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (object == this) return(true);

    if ((object instanceof FacetFilter) == false) return(false);

    FacetFilter thatFacetFilter = (FacetFilter) object;

    if (facetIdentifier.equals(thatFacetFilter.facetIdentifier) == false) {
      return(false);
    }

    if (specification != null) {
      if (specification.equals(thatFacetFilter.specification) == false) return(false);
    }
    
    if (direction != null) {
      if (direction.equals(thatFacetFilter.direction) == false) return(false);
    }
    
    return true;
	}
}
