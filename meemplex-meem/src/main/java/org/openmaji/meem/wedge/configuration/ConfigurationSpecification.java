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
import java.lang.reflect.Method;

import org.openmaji.meem.wedge.lifecycle.LifeCycleState;


/**
 * <code>ConfigurationSpecification</code> represents the specification of a 
 * configurable property exposed from a wedge. In its fullest form it allows you
 * to associated a type and a maximum life cycle state for changes with the 
 * configuration property.
 * <p>
 * @author Kin Wong
 */
public class ConfigurationSpecification<T extends Serializable> implements Serializable, Cloneable {

	static final long serialVersionUID = 4106487663762040101L;

	private ConfigurationIdentifier	id;
	private String description;
	private Class<T> type;
	private T defaultValue;
	private LifeCycleState maxLifeCycleState;

	/**
	 * Constructs an instance of <code>ConfigurationSpecification</code>.
	 * <p>
	 * @param description The description of the configurable property.
	 * @param type The type of the configurable property.
	 * @param maxLifeCycleState The maximum life cycle state we can accept config requests in.
	 */
	public static <T extends Serializable> ConfigurationSpecification<T> create(String description, Class<T> type, LifeCycleState maxLifeCycleState) {
		return new ConfigurationSpecification<T>(description, type, maxLifeCycleState);
	}
	
	/**
	 * Constructs an instance of <code>ConfigurationSpecification</code> with a default
	 * maximum life cycle state of LifeCycleState.LOADED.
	 * <p>
	 * @param description The description of the configurable property.
	 * @param type The type of the configurable property.
	 */
	public static <T extends Serializable> ConfigurationSpecification<T> create(String description, Class<T> type) {
		return new ConfigurationSpecification<T>(description, type, LifeCycleState.LOADED);
	}
	
	/**
	 * Constructs an instance of <code>ConfigurationSpecification</code> for 
	 * configurable property of type String with default value of an empty string and
	 * a default maximum life cycle state of LifeCycleState.LOADED.
	 * <p>
	 * @param description The description of the configurable property.
	 */
	public static ConfigurationSpecification<String> create(String description) {
		return new ConfigurationSpecification<String>(description, String.class, LifeCycleState.LOADED);
	}

	/**
	 * Constructs an instance of <code>ConfigurationSpecification</code>.
	 * <p>
	 * @param description The description of the configurable property.
	 * @param type The type of the configurable property.
	 * @param maxLifeCycleState The maximum life cycle state we can accept config requests in.
	 */
	protected ConfigurationSpecification(String description, Class<T> type, LifeCycleState maxLifeCycleState) 
	{
		this.description = description;
		this.type = type;
		this.maxLifeCycleState = maxLifeCycleState;
	}
	
	/**
	 * Constructs an instance of <code>ConfigurationSpecification</code> with a default
	 * maximum life cycle state of LifeCycleState.LOADED.
	 * <p>
	 * @param description The description of the configurable property.
	 * @param type The type of the configurable property.
	 */
	protected ConfigurationSpecification(String description, Class<T> type) 
	{
		this(description, type, LifeCycleState.LOADED);
	}
	
	/**
	 * Set the identifier for this specification - this will normally be done by the adapter.
	 * 
	 * @param identifier
	 */
	public void setIdentifier(ConfigurationIdentifier identifier) 
	{
		if (id != null)
		{
			throw new IllegalStateException("identifier already set");
		}
		this.id = identifier;
	}
	
	/**
	 * Set the defaultValue for this specification - this will normally be done by the adapter.
	 * 
	 * @param defaultValue
	 */
	public void setDefaultValue(T defaultValue) 
	{
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Gets the identifier of  this property.
	 * 
	 * @return the identifier of this property.
	 */
	public ConfigurationIdentifier getIdentifier() 
	{
		return id;
	}
	
	/**
	 * Gets the description of the configurable property.
	 * <p>
	 * @return The description of the configurable property.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the type of the configurable property.
	 * 
	 * @return The type of this configurable property.
	 */
	public Class<T> getType() {
		return type;
	}
	
	/**
	 * Gets the default value of the configurable property.
	 *
	 * @return The default value of this configurable property.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

    /**
     * Return the maximum life cycle state we can be in for this property to be changed.
     * 
     * @return maximum life cycle state for property changes.
     */
	public LifeCycleState getMaxLifeCycleState()
	{
		return maxLifeCycleState;
	}
	
	/**
	 * Return true if this specification has the same id as the passed in id
	 * 
	 * @param id the id we want to compare.
	 * @return true if the id is the same as ours, false otherwise.
	 */
	public boolean hasID(
		ConfigurationIdentifier	id)
	{
		return this.getIdentifier().equals(id);
	}
	
	/**
	 * Validate the passed in value as being suitable for the configuration property represented
	 * by this configuration specification.
	 * 
	 * @param value the value to validate
	 * @return null if value is okay, a message string if it isn't.
	 */
	public String validate(Object	value)
	{
      try
		{
			Method	method = this.getClass().getMethod("validate", new Class[] { this.getType() } );
                    
			return (String)method.invoke(this, new Object[] { value });
		}
		catch (IllegalAccessException e)
		{
			return "validate method found but cannot access: " + e.toString();
		}
		catch (Exception e)
		{
			//
			// ignore exception, if they haven't provided a method we'll just check the type.
			//
			if (!(this.getType().isAssignableFrom(value.getClass())))
			{
              StringBuffer buffer = new StringBuffer();
              buffer.append("validation failure. wedge=[");
              buffer.append(id.getWedgeIdentifier());
              buffer.append("] fieldname=");
              buffer.append(id.getFieldName());
              buffer.append("] expected=[");
              buffer.append(this.getType().getName());
              buffer.append("] actual=[");
              buffer.append(value.getClass().getName());
              buffer.append(']');
              return buffer.toString();
			}
		}

		return null;
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
	 * Based on the hashCode of getID.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getIdentifier().hashCode();
	}
	
	/**
	 * Two ConfigurationSpecifications will compare equals if their two getID
	 * methods return an equal value.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof ConfigurationSpecification)) return false;
		ConfigurationSpecification<?> that = (ConfigurationSpecification<?>) obj;
		return getIdentifier().equals(that.getIdentifier());
	}
	
	/**
	 * Compares whether the contents of the ConfigurationSpecifications are equal.
	 * <p>
	 * @param that A ConfigurationSpecification to compare with.
	 * @return true if the contents are equal, false otherwise.
	 */
	public boolean contentEquals(ConfigurationSpecification<?> that) {
		if(!getIdentifier().equals(that.getIdentifier())) return false;
		if(!getDescription().equals(that.getDescription())) return false;
		if(!getDefaultValue().equals(that.getDefaultValue())) return false;
		return getType().equals(that.getType());
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		String text = "ID=[" + getIdentifier() + "]";
		text += ", description=" + getDescription();
		text += ", type=" + getType();
		text += ", default value=" + getDefaultValue();
		return text;
	}
}
