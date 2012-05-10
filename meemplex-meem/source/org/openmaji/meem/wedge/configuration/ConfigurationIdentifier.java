/*
 * @(#)ConfigurationSpecification.java
 * Created on 15/01/2004
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem.wedge.configuration;

import java.io.Serializable;

/**
 * <code>ConfigurationIdentifier</code> represents a unique key across the meem
 * for a property. The object compares for equality and generates its hashCode based
 * on the return value of getWedgeIdentifier() and getFieldName().
 * <p>
 * @author Kin Wong
 */
public class ConfigurationIdentifier implements Serializable, Cloneable {

	static final long serialVersionUID = -527539296028022783L;

	private String wedgeID;
	private String name;
	private String alias;

	/**
	 * Constructs an instance of <code>ConfigurationIdentifier</code>.
	 * <p>
	 * @param name The name of the configurable property.
	 */
	public ConfigurationIdentifier(String wedgeID, String name)
	{
				this.wedgeID = wedgeID;
				this.name = name;
				this.alias = wedgeID + "." + name;
	}
	
	/**
	 * Gets the ID of the wedge this property is for.
	 * 
	 * @return the ID of the wedge this property is for.
	 */
	public String getWedgeIdentifier() 
	{
		return wedgeID;
	}
	
	/**
	 * Gets the name of the configurable property.
	 * <p>
	 * @return The name of the configurable property.
	 */
	public String getFieldName() 
	{
		return name;
	}
	
	/**
	 * Set an alias for this identifier - by default this is the field name, however in the event
	 * you are setting these via a meem definition you should choose an alias which is 
	 * meaningful and unique across the meem.
	 * 
	 * @param alias the alias to use.
	 */
	public void setAlias(
		String	alias)
	{
		this.alias = alias;
	}
	
	/**
	 * Return the alias for this configuration identifier.
	 * 
	 * @return this identifiers alias.
	 */
	public String getAlias()
	{
		return alias;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return super.clone();
		}
		catch(CloneNotSupportedException e) {
			// Should never reaches here.
			return null;
		}
	}
	
	/**
	 * Based on the hashCode of getName() and getWedgeID. The alias is ignored.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getWedgeIdentifier().hashCode() ^ getFieldName().hashCode();
	}
	
	/**
	 * Two ConfigurationIdentifier will compare equals if their two getName and getWedgeID
	 * methods return an equal value. The alias is ignored.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof ConfigurationIdentifier)) return false;
		ConfigurationIdentifier that = (ConfigurationIdentifier)obj;
		return getWedgeIdentifier().equals(that.getWedgeIdentifier()) && getFieldName().equals(that.getFieldName());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		String text = "wedgeID=" + getWedgeIdentifier();
		text += " name=" + getFieldName();
		text += " alias=" + getAlias();
		return text;
	}
}
