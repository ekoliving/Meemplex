/*
 * @(#)WedgeDefinition.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem.definition;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.openmaji.utility.CollectionUtility;

/**
 * <p>
 * The WedgeDefinition defines a Wedge's attributes and its Facets.
 * </p>
 * <p>
 * The attributes of a Wedge are its implementation class and persistent fields.
 * </p>
 * <p>
 * A Wedge is comprised of Facets that provide the interface contract.
 * </p>
 * <p>
 * Deeper Wedge structural parts, such as Dependencies are defined underneath the FacetDefinition.
 * </p>
 * <p>
 * Since the WedgeDefinition includes the deeper definition structure, it is used only in those occasions when the whole thing is needed.
 * </p>
 * <p>
 * Circumstances that require individual manipulation of parts of the Wedge definition structure, such as MetaMeem clients, should instead utilize the MeemStructure mechanism.
 * </p>
 * <p>
 * WedgeDefinitions are contained by the MeemDefinition, which is used to build a Meem. A Wedge is comprised of zero or more Facets, each of which is a Java interface that can be
 * the target of in-bound method invocations or the source of out-bound method invocations.
 * </p>
 * <p>
 * The key detail of a Wedge is it's implementation class name. All of the Wedge's in-bound Facet methods must be implemented by the Wedge's implementation class, so that the Meem
 * can be built by the MeemBuilder. Within a given Meem, the Wedge implementation class name must be unique.
 * </p>
 * <p>
 * Since the Wedge implementation class name is used as an identifier, there is a convenience method that strips the package information from the fully qualified class name, for
 * when a "short name" is required.
 * </p>
 * <p>
 * Facets are Java interfaces defined for a Wedge and are considered to be either in-bound or out-bound (not both). In-bound Facet methods are mapped to the Wedge's implementation.
 * Out-bound Facets appear as public fields in the Wedge implementation class. Method invocations can be made on the object reference defined by that public field, which will
 * result in method invocations being attempted on other Meems.
 * </p>
 * <p>
 * To make life easier when presenting information, the insertion order of the FacetDefinitions and persistent fields must be preserved by the WedgeDefinition object structures.
 * </p>
 * <p>
 * FacetDefinitions are stored as a LinkedHashSet to preserve insertion order and prevent duplicates.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-07-28)
 * </p>
 * 
 * @author Andy Gelme
 * @author MG
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.WedgeAttribute
 * @see org.openmaji.meem.definition.FacetDefinition
 */

public final class WedgeDefinition implements Serializable {
	private static final long serialVersionUID = 7302732789959660130L;

	/**
	 * Attributes of the Wedge
	 */

	private WedgeAttribute wedgeAttribute;

	/**
	 * Domain specific FacetDefinitions that define the Meem's functionality. Must be an "insertion order", "no duplicates" object structure.
	 */

	private LinkedHashSet<FacetDefinition> facetDefinitions = new LinkedHashSet<FacetDefinition>();

	/**
	 * Create a WedgeDefinition.
	 */

	public WedgeDefinition() {
		this(new WedgeAttribute());
	}

	/**
	 * Create a WedgeDefinition.
	 * 
	 * @param wedgeAttribute
	 *            Attributes of the Wedge
	 */

	public WedgeDefinition(WedgeAttribute wedgeAttribute) {

		if (wedgeAttribute == null) {
			throw new IllegalArgumentException("WedgeAttribute must be provided");
		}

		this.wedgeAttribute = wedgeAttribute;
	}

	/**
	 * Provides the WedgeAttribute.
	 * 
	 * @return Attributes of the Wedge
	 */

	public synchronized WedgeAttribute getWedgeAttribute() {
		return (wedgeAttribute);
	}

	/**
	 * Assigns the WedgeAttribute.
	 * 
	 * @param wedgeAttribute
	 *            Attributes of the Wedge
	 */

	public synchronized void setWedgeAttribute(WedgeAttribute wedgeAttribute) {

		this.wedgeAttribute = wedgeAttribute;
	}

	/**
	 * <p>
	 * Add another FacetDefinition to the WedgeDefinition.
	 * </p>
	 * <p>
	 * If a FacetDefinition already exists that matches the new FacetDefinition, then the old FacetDefinition is replaced by the new one.
	 * </p>
	 * <p>
	 * The insertion order of the FacetDefinitions is maintained.
	 * </p>
	 * 
	 * @param facetDefinition
	 *            FacetDefinition to add
	 */

	public synchronized void addFacetDefinition(FacetDefinition facetDefinition) {
		facetDefinitions.add(facetDefinition);
	}
	
	public synchronized void setFacetDefinitions(Collection<FacetDefinition> definitions) {
		this.facetDefinitions = new LinkedHashSet<FacetDefinition>(definitions);
	}

	/**
	 * Return an Collection of all of the FacetDefinitions. The Collection is a clone, so any additions or removal of elements will not affect the underlying collection of
	 * FacetDefinitions.
	 * 
	 * @return Collection of all of the FacetDefinitions
	 */

	public synchronized Collection<FacetDefinition> getFacetDefinitions() {
		return new LinkedHashSet<FacetDefinition>(facetDefinitions);
	}

	public synchronized FacetDefinition getFacetDefinition(String facetIdentifier) {
		for (FacetDefinition facetDefinition : facetDefinitions) {
			if (facetIdentifier.equals(facetDefinition.getFacetAttribute().getIdentifier())) {
				return facetDefinition;
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Remove the specified FacetDefinition from the WedgeDefinition.
	 * </p>
	 * <p>
	 * It is not considered to be a problem if the specified FacetDefinition doesn't exist.
	 * </p>
	 * 
	 * @param facetDefinition
	 *            FacetDefinition to remove
	 */

	public synchronized FacetDefinition removeFacetDefinition(FacetDefinition facetDefinition) {
		if (facetDefinitions.remove(facetDefinition)) {
			return facetDefinition;
		}
		else {
			return null;
		}
	}

	public synchronized FacetDefinition removeFacetDefinition(String facetIdentifier) {
		FacetDefinition facetDefinition = getFacetDefinition(facetIdentifier);
		if (facetDefinition != null) {
			facetDefinitions.remove(facetDefinition);
		}
		return facetDefinition;
	}

	/**
	 * Rename the specified facet's identifier.
	 * 
	 * @param oldIdentifier
	 *            The facet identifier you wish to change
	 * @param newIdentifier
	 *            The new identifier for the facet
	 */

	public synchronized void renameFacetIdentifier(String oldIdentifier, String newIdentifier) throws IllegalArgumentException {

		// First make sure that the newIdentifier isn't already used
		if (getFacetDefinition(newIdentifier) != null) {
			throw new IllegalArgumentException("The identifier '" + newIdentifier + "' is already used");
		}

		// Now go ahead and change it
		FacetDefinition facetDefinition = getFacetDefinition(oldIdentifier);
		if (facetDefinition != null) {
			FacetAttribute facetAttribute = facetDefinition.getFacetAttribute();
			facetAttribute.setIdentifier(newIdentifier);
		}
	}

	/**
	 * Compares WedgeDefinition to the specified object. The result is true, if and only if the WedgeAttributes are equal and all the FacetDefinitions are equal.
	 * 
	 * @return true if WedgeDefinitions are equal
	 */

	public synchronized boolean equals(Object object) {

		if (object == this)
			return (true);

		if ((object instanceof WedgeDefinition) == false)
			return (false);

		WedgeDefinition thatWedgeDefinition = (WedgeDefinition) object;

		boolean match = false;

		if (wedgeAttribute.equals(thatWedgeDefinition.getWedgeAttribute())) {
			match = CollectionUtility.equals(getFacetDefinitions(), thatWedgeDefinition.getFacetDefinitions());
		}

		return (match);
	}

	/**
	 * Provides the Object hashCode. Must follow the Object.hashCode() and Object.equals() contract.
	 * 
	 * @return WedgeDefinition hashCode
	 */

	public synchronized int hashCode() {
		return (wedgeAttribute.hashCode() ^ facetDefinitions.hashCode());
	}

	/**
	 * Provides a String representation of WedgeDefinition.
	 * 
	 * @return String representation of WedgeDefinition
	 */

	public synchronized String toString() {
		return (getClass().getName() + "[" + "wedgeAttribute=" + wedgeAttribute + ", facetDefinitions=" + facetDefinitions + "]");
	}
}
