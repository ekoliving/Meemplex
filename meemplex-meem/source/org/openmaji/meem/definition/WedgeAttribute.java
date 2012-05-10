/*
 * @(#)WedgeAttribute.java
 * Created on 9/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Attributes describing a wedge.
 * <p>
 * 
 * TODO add Facets, ConfigProperties to this class?
 * 
 * @author Kin Wong
 */
public final class WedgeAttribute implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -8328404055695098217L;

	static private final String DEFAULT_IMPLEMENTATION_CLASS_NAME = "";

	/**
	 * Name of the implementation class
	 */
	private String implementationClassName;

	/**
	 * Names of fields that should be persisted
	 */
	private LinkedHashSet<String> persistentFieldNames;

	/**
	 * Whether the wedge is a system wedge or not.
	 */
	private boolean isSystemWedge = false;

	/**
	 * An identifier unique to the Wedge with the Meem
	 */
	private String identifier = null;

	/**
	 * Create an instance of <code>WedgeAttribute</code> with the specified implementation class name and identifier.
	 * 
	 * @param implementationClassName
	 *            The name of the wedge's implementation class
	 * @param identifier
	 *            The unique identifier for the wedge
	 */
	public WedgeAttribute(String implementationClassName, String identifier) {
		this(implementationClassName);
		this.identifier = identifier;
	}

	/**
	 * Constructs an instance of <code>WedgeAttribute</code>. The identifier is set to the name of the implementation class minus the package name.
	 * <p>
	 * 
	 * @param implementationClassName
	 *            The implementation class name of this <code>WedgeAttribute</code>.
	 */
	public WedgeAttribute(String implementationClassName) {
		this.implementationClassName = implementationClassName;
		setIdentifier();
	}

	/**
	 * Constructs an instance of <code>WedgeAttribute</code>.
	 * <p>
	 */
	public WedgeAttribute() {
		this(DEFAULT_IMPLEMENTATION_CLASS_NAME);
	}

	private void setIdentifier() {
		if (implementationClassName == null)
			return;

		int lastIndex = implementationClassName.lastIndexOf('.');
		if (lastIndex == -1) {
			identifier = implementationClassName;
		}
		else {
			identifier = implementationClassName.substring(lastIndex + 1);
		}
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Define the persistent field names for a wedge.
	 * 
	 * @param fields
	 *            a collection of field names.
	 */
	public void setPersistentFields(Collection<String> fields) {
		persistentFieldNames = new LinkedHashSet<String>(fields);
	}

	/**
	 * Add a persistent field name to the wedge attribute.
	 * 
	 * @param persistentFieldName
	 *            the field name to be added.
	 */
	public void addPersistentField(String persistentFieldName) {
		if (persistentFieldNames == null) {
			persistentFieldNames = new LinkedHashSet<String>();
		}
		persistentFieldNames.add(persistentFieldName);
	}

	/**
	 * Return an Iterator for all of the persistent field names.
	 * <p>
	 * 
	 * @return Iterator for all of the persistent field names
	 */
	public Collection<String> getPersistentFields() {
		if (persistentFieldNames == null) {
			return Collections.emptyList();
		}
		ArrayList<String> fields = new ArrayList<String>(persistentFieldNames);
		return fields;
	}

	/**
	 * Remove the specified persistent field from the WedgeAttribute.
	 * <p>
	 * It is not considered to be a problem if the specified persistent field doesn't exist.
	 * </p>
	 * 
	 * @param persistentFieldName
	 *            Name of the persistent field to remove
	 */
	public void removePersistentField(String persistentFieldName) {
		if (persistentFieldNames == null) {
			return;
		}
		persistentFieldNames.remove(persistentFieldName);
	}

	/**
	 * Gets the identifier of the wedge type. By default it is the implementation class name stripped of the package name.
	 * 
	 * @return Wedge identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the identifier for this wedge attribute. If the default identifier set by the constructor or by setImplementationClassName() is not appropriate you should use this
	 * method to explicitly set it. The identifier can be used to uniquely identify a wedge within a Meem.
	 * 
	 * @param identifier
	 *            The new identifier for this wedge attribute
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Returns the implementation class name of this <code>WedgeAttribute</code>.
	 * <p>
	 * 
	 * @return The implementation class name of this <code>WedgeAttribute</code>.
	 */
	public String getImplementationClassName() {
		return implementationClassName;
	}

	/**
	 * Sets the Implementation class name. As a side effect this method also sets the identifier for this wedge attribute to the name of the implementation class minus the package
	 * name.
	 * 
	 * @param implementationClassName
	 *            Name of the implementation class
	 */
	public void setImplementationClassName(String implementationClassName) {
		this.implementationClassName = implementationClassName;
		setIdentifier();
	}

	/**
	 * Marks the wedge as a system wedge
	 * 
	 * @param isSystemWedge
	 *            True if system wedge
	 */
	public void setSystemWedge(boolean isSystemWedge) {
		this.isSystemWedge = isSystemWedge;
	}

	/**
	 * Return whether or not this WedgeAttribute is for a system wedge.
	 * 
	 * @return true if a system wedge, false otherwise.
	 */
	public boolean isSystemWedge() {
		return isSystemWedge;
	}

	/**
	 * Compares <code>WedgetType</code> to the specified object.
	 */
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object.getClass().equals(this.getClass()) == false) {
			return false;
		}
		WedgeAttribute that = (WedgeAttribute) object;
		return that.identifier.equals(this.identifier);
	}

	/**
	 * Returns a hash code for this <code>WedgeAttribute</code>.
	 */
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (identifier == null ? 0 : identifier.hashCode());
		return hash;
	}

	public synchronized String toString() {
		return (getClass().getName() + "[" + "identifier=" + identifier + ", implementationClassName=" + implementationClassName + ", persistentFieldNames=" + persistentFieldNames + "]");
	}
}
