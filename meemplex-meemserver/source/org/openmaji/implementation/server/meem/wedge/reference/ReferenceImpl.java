/*
 * @(#)ReferenceImpl.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.wedge.reference;

import java.lang.reflect.Proxy;

import org.openmaji.meem.Facet;
import org.openmaji.meem.filter.Filter;
import org.openmaji.meem.wedge.reference.Reference;


/**
 * @author Andy Gelme
 */
public class ReferenceImpl implements Reference {
	private static final long serialVersionUID = -88281987L;

  /**
   * Identifier for the Provider Meem's Facet to depend upon
   */

  private String facetIdentifier;

  /**
   * Target of the specified Facet
   */

  private Facet target;

  /**
   * Indicates that the Provider Meem must send it's current content
   */

  private boolean contentRequired;

  /**
   * Optional Filter that can be used by the Provider Meem to limit
   * or transform information flow.
   */

  private Filter filter;

  /**
   * Create Reference.
   *
   * @param facetIdentifier Identifier for the Facet to depend upon
   * @param target          Target of the specified Facet
   * @param contentRequired Provider Meem must send it's current content
   * @param filter          Filter used to limit information flow
   * @exception IllegalArgumentException facetIdentifier or target are null
   */

  public ReferenceImpl(
    String     facetIdentifier,
    Facet      target,
    Boolean    contentRequired,
    Filter     filter) {

    if (facetIdentifier == null) {
      throw new IllegalArgumentException("facetIdentifier must not be 'null'");
    }

    if (target == null) {
      throw new IllegalArgumentException("target must not be 'null'");
    }
    
    //
    // note: this will throw an IllegalArgumentException if the target is not a proxy as well.
    //
		if (Proxy.getInvocationHandler(target) == null)
		{
			throw new IllegalArgumentException("target has no invocation handler");
		}
	
    this.facetIdentifier = facetIdentifier;
    this.target          = target;
    this.contentRequired = contentRequired.booleanValue();
    this.filter          = filter;
  }

  /**
   * Provide the identifier of the Provider Meem's Facet to depend upon.
   *
   * @return Identifier of the Provider Meem's Facet to depend upon
   */

  public String getFacetIdentifier() {
    return(facetIdentifier);
  }

  /**
   * Provide the Filter used to limit information flow.
   *
   * @return Filter used to limit information flow.
   */

  public Filter getFilter() {
    return(filter);
  }

  /**
   * <p>
   * Indicates that the Meem Provider must send it's current content.
   * </p>
   * @return boolean Meem Provider must send it's current content
   */

  public boolean isContentRequired() {
    return(contentRequired);
  }

  /**
   * Provide the target of the specified Facet.
   *
   * @return Target of the specified Facet
   */

  public Facet getTarget() {
    return(target);
  }

  /**
   * <p>
   * Compares Reference to the specified object.
   * The result is true, if and only if all of the facetIdentifier,
   * target, filter and contentRequired are equal.
   * </p>
   * @return true if References are equal
   */

  public synchronized boolean equals(
    Object object) {

    if (object == this) return(true);

    if ((object instanceof Reference) == false) return(false);

    Reference thatReference = (Reference) object;

    if (facetIdentifier.equals(thatReference.getFacetIdentifier()) == false) {
      return(false);
    }
    
    if (target.equals(thatReference.getTarget()) == false) 
    	return(false);

    if (filter != null) {
      if (filter.equals(thatReference.getFilter()) == false) return(false);
    }

    return(contentRequired == thatReference.isContentRequired());
  }

  /**
   * <p>
   * Provides the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   * </p>
   * @return Reference hashCode
   */

  public synchronized int hashCode() {
    int hashCode = facetIdentifier.hashCode();

    hashCode ^= target.hashCode();

    hashCode ^= contentRequired ? 9329 : 7325;

    if (filter != null) hashCode ^= filter.hashCode();

    return(hashCode);
  }

  /**
   * Provides a String representation of the Reference.
   *
   * @return String representation of the Reference
   */

  public String toString() {
    return(
      getClass().getName() + "[" +
      "facetIdentifier="   + facetIdentifier +
      ", target="          + target          +
      ", contentRequired=" + contentRequired +
      ", filter="          + filter          +
      "]"
    );
  }
}
