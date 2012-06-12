/*
 * @(#)MeemDefinition.java
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
 * The MeemDefinition defines a Meem's attributes and its Wedges.
 * </p>
 * <p>
 * The attributes of a Meem are its identifier, scope and version.
 * </p>
 * <p>
 * A Meem is comprised of Wedges that provide the implementation code.
 * </p>
 * <p>
 * Deeper Meem structural parts, such as Facets and Dependencies are defined underneath the WedgeDefinition.
 * </p>
 * <p>
 * Since the MeemDefinition includes the entire definition structure, it is used only in those occasions when the whole thing is needed. For example, during Meem creation and
 * persistence.
 * </p>
 * <p>
 * Circumstances that require individual manipulation of parts of the Meem definition structure, such as MetaMeem clients, should instead utilize the MeemStructure mechanism.
 * </p>
 * <p>
 * All Meems have a predefined set of Maji system Wedges, as well as some application (or domain) specific Wedges. Typically, the MeemDefinition will only contain the Wedges
 * defined by the application designer. The MeemBuilder automagically provides the predefined Maji system WedgeDefinitions.
 * </p>
 * <p>
 * The Wedge implementation class name is used to distinguish between Wedges. Therefore, Meems can not have two Wedges with the same implementation class.
 * </p>
 * <p>
 * Furthermore, it is a Meem building requirement that all Facets (even those in different Wedges) have a unique identifier. However, that restriction does not have to be inforced
 * in the "definition structures", due to the cost of tracking Facet identifier changes ... for dubious return value. This Facet identifier restriction only has to be checked at
 * Meem build time.
 * </p>
 * <p>
 * To make life easier when presenting information, the insertion order of the WedgeDefinitions must be preserved by the MeemDefinition object structures.
 * </p>
 * <p>
 * WedgeDefinitions are stored as a LinkedHashSet to preserve insertion order and prevent duplicates.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-07-28)
 * </p>
 * 
 * @author Andy Gelme
 * @author MG
 * @version 1.0
 * @see org.openmaji.meem.Meem
 * @see org.openmaji.meem.definition.MeemAttribute
 * @see org.openmaji.meem.definition.WedgeDefinition
 */

public final class MeemDefinition implements Serializable, Cloneable {
	private static final long serialVersionUID = 3496278156280617095L;

	/**
	 * Attributes of the Meem
	 */

	private MeemAttribute meemAttribute;

	/**
	 * Domain specific WedgeDefinitions that define the Meem's functionality. Must be an "insertion order", "no duplicates" object structure.
	 */

	private LinkedHashSet<WedgeDefinition> wedgeDefinitions = new LinkedHashSet<WedgeDefinition>();

	/**
	 * Create a MeemDefinition.
	 */

	public MeemDefinition() {
		this(new MeemAttribute());
	}

	/**
	 * Create a MeemDefinition.
	 * 
	 * @param meemAttribute
	 *            Attributes of the Meem
	 */

	public MeemDefinition(MeemAttribute meemAttribute) {

		if (meemAttribute == null) {
			throw new IllegalArgumentException("MeemAttribute must be provided");
		}

		this.meemAttribute = meemAttribute;
	}

	/**
	 * Provides the MeemAttribute.
	 * 
	 * @return Attributes of the Meem
	 */

	public synchronized MeemAttribute getMeemAttribute() {
		return (meemAttribute);
	}

	/**
	 * Assigns the MeemAttribute. The existing MeemAttribute's ImmutableAttributes will not be overridden.
	 * 
	 * @param meemAttribute
	 *            Attributes of the Meem
	 */

	public synchronized void setMeemAttribute(MeemAttribute meemAttribute) {

		if (this.meemAttribute == null) {
			this.meemAttribute = meemAttribute;
		}
		else {
			MeemAttribute.copyPreservingImmutableAttributes(this.meemAttribute, meemAttribute);
		}
	}

	/**
	 * <p>
	 * Add another WedgeDefinition to the MeemDefinition.
	 * </p>
	 * <p>
	 * If a WedgeDefinition already exists that matches the new WedgeDefinition, then the old WedgeDefinition is replaced by the new one.
	 * </p>
	 * <p>
	 * The insertion order of the WedgeDefinitions is maintained.
	 * </p>
	 * 
	 * @param wedgeDefinition
	 *            WedgeDefinition to add
	 */

	public synchronized void addWedgeDefinition(WedgeDefinition wedgeDefinition) {

		wedgeDefinitions.add(wedgeDefinition);
	}

	/**
	 * Return a Collection of all of the WedgeDefinitions. The Collection is a clone, so any additions or removal of elements will not affect the underlying collection of
	 * WedgeDefinitions.
	 * 
	 * @return Collection of all of the WedgeDefinitions
	 */

	public Collection<WedgeDefinition> getWedgeDefinitions() {
		return new LinkedHashSet<WedgeDefinition>(wedgeDefinitions);
	}

	public synchronized WedgeDefinition getWedgeDefinition(String wedgeIdentifier) {
		for (WedgeDefinition wedgeDefinition : wedgeDefinitions) {
			if (wedgeIdentifier.equals(wedgeDefinition.getWedgeAttribute().getIdentifier())) {
				return wedgeDefinition;
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Remove the specified WedgeDefinition from the MeemDefinition.
	 * </p>
	 * <p>
	 * It is not considered to be a problem if the specified WedgeDefinition doesn't exist.
	 * </p>
	 * 
	 * @param wedgeDefinition
	 *            WedgeDefinition to remove
	 */

	public synchronized void removeWedgeDefinition(WedgeDefinition wedgeDefinition) {
		wedgeDefinitions.remove(wedgeDefinition);
	}

	public synchronized void removeWedgeDefinition(String wedgeIdentifier) {
		WedgeDefinition wedgeDefinition = getWedgeDefinition(wedgeIdentifier);
		if (wedgeDefinition != null) {
			wedgeDefinitions.remove(wedgeDefinition);
		}
	}

	/**
	 * Compares MeemDefinition to the specified object. The result is true, if and only if the MeemAttributes are equal and all the WedgeDefinitions are equal.
	 * 
	 * @return true if MeemDefinitions are equal
	 */

	public synchronized boolean equals(Object object) {

		if (object == this)
			return (true);

		if ((object instanceof MeemDefinition) == false)
			return (false);

		MeemDefinition thatMeemDefinition = (MeemDefinition) object;

		boolean match = false;

		if (meemAttribute.equals(thatMeemDefinition.getMeemAttribute())) {
			match = CollectionUtility.equals(getWedgeDefinitions(), thatMeemDefinition.getWedgeDefinitions());
		}

		return (match);
	}

	/**
	 * Provides the Object hashCode. Must follow the Object.hashCode() and Object.equals() contract.
	 * 
	 * @return MeemDefinition hashCode
	 */

	public synchronized int hashCode() {
		return (meemAttribute.hashCode() ^ wedgeDefinitions.hashCode());
	}

	/**
	 * Provides a String representation of MeemDefinition.
	 * 
	 * @return String representation of MeemDefinition
	 */

	public synchronized String toString() {
		return (getClass().getName() + "[" + "meemAttribute=" + meemAttribute + ", wedgeDefinitions=" + wedgeDefinitions + "]");
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public synchronized Object clone() {

		MeemAttribute clonedMeemAttibute = (MeemAttribute) meemAttribute.clone();
		MeemDefinition clone = new MeemDefinition(clonedMeemAttibute);
		clone.wedgeDefinitions = new LinkedHashSet<WedgeDefinition>(wedgeDefinitions);
		return clone;
	}
}
