/*
 * @(#)DependencyType.java
 * Created on 9/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;

import java.io.Serializable;

import org.openmaji.meem.Meem;
import org.openmaji.meem.MeemPath;
import org.openmaji.meem.filter.Filter;
import org.openmaji.utility.uid.UID;


/**
 * The attributes describing a dependency.
 * <p>
 * @author Andy Gelme
 * @author Kin Wong
 */
public final class DependencyAttribute implements Cloneable, Serializable {
	
	private static final long serialVersionUID = -7276125170388699218L; 
	
	/** Uniquely identifies this dependency definition */
	private UID key;
	
	/** Determines the type of Dependency resolution that will occur */
	private DependencyType dependencyType;
	
	/** Determines the extent to which MeemRegistry will go to locate the Meem */
	private Scope scope;

	/** The id of the facet */
	private String facetIdentifier;
	
	/** Path to the meem that is depended on */
	private MeemPath meemPath;
	
	/** A filter for messages sent over the dependency connection */
	private Filter filter;
	
	/** The Meem that is depended on */
	private Meem meem;
	
	/** Whether initial content is required to be sent when the dependency is connected */
	private boolean initialContentRequired = true;
	
	/**
	 * Constructs an instance of <code>DependencyAttribute</code>.
	 * <p>
	 * @param dependencyType the type of Dependency resolution that will occur.
	 * @param scope The extent to which MeemRegistry will go to locate the Meem.
	 * @param meemPath The meem path of the meem cotaining the target facet.
   * @param facetIdentifier The identifier of the Facet
	 */
	public DependencyAttribute(DependencyType dependencyType, Scope scope, MeemPath meemPath, String facetIdentifier) {
		this(dependencyType, scope, meemPath, facetIdentifier, null, true);
	}
	
	/**
	 * Constructs an instance of <code>DependencyAttribute</code>.
	 * <p>
	 * @param dependencyType the type of Dependency resolution that will occur.
	 * @param scope The extent to which MeemRegistry will go to locate the Meem.
	 * @param meemPath The meem path of the meem cotaining the target facet.
   * @param facetIdentifier The identifier of the Facet
   * @param filter The filter
   * @deprecated Should now use constructor with initialContentRequired parameter
	 */
	public DependencyAttribute(
		DependencyType dependencyType, Scope scope, MeemPath meemPath, String facetIdentifier, Filter filter) {
		key = UID.spi.create();
		this.dependencyType = dependencyType;
		this.scope = scope;
		this.meemPath = meemPath;
		this.facetIdentifier = facetIdentifier;
		this.filter = filter;
	}
	
	/**
	 * Constructs an instance of <code>DependencyAttribute</code>.
	 * <p>
	 * @param dependencyType the type of Dependency resolution that will occur.
	 * @param scope The extent to which MeemRegistry will go to locate the Meem.
	 * @param meemPath The meem path of the meem cotaining the target facet.
   * @param facetIdentifier The identifier of the Facet
   * @param filter The filter
   * @param initialContentRequired Whether initial content is needed from the target facet (used for outbound target facets only)
   * @since 1.5.1
	 */
	public DependencyAttribute(
		DependencyType dependencyType, Scope scope, MeemPath meemPath, String facetIdentifier, Filter filter, boolean initialContentRequired) {
		key = UID.spi.create();
		this.dependencyType = dependencyType;
		this.scope = scope;
		this.meemPath = meemPath;
		this.facetIdentifier = facetIdentifier;
		this.filter = filter;
		this.initialContentRequired = initialContentRequired;
	}
	
	/**
	 * Constructs an instance of <code>DependencyAttribute</code>.
	 * <p>
	 * @param dependencyType the type of Dependency resolution that will occur.
	 * @param scope The extent to which MeemRegistry will go to locate the Meem.
	 * @param meem The meem cotaining the target facet.
   * @param facetIdentifier The identifier of the Facet
   * @param filter The filter
   * @deprecated Should now use constructor with initialContentRequired parameter
	 */	
	public DependencyAttribute(DependencyType dependencyType, Scope scope, Meem meem, String facetIdentifier, Filter filter) {
		key = UID.spi.create();
		this.dependencyType = dependencyType;
		this.scope = scope;
		this.meem = meem;
		this.meemPath = meem.getMeemPath();
		this.facetIdentifier = facetIdentifier;
		this.filter = filter;
	}
	
	/**
	 * Constructs an instance of <code>DependencyAttribute</code>.
	 * <p>
	 * @param dependencyType the type of Dependency resolution that will occur.
	 * @param scope The extent to which MeemRegistry will go to locate the Meem.
	 * @param meem The meem cotaining the target facet.
   * @param facetIdentifier The identifier of the Facet
   * @param filter The filter
   * @param initialContentRequired Whether initial content is needed from the target facet (used for outbound target facets only)
	 * @since 1.5.1
	 */	
	public DependencyAttribute(DependencyType dependencyType, Scope scope, Meem meem, String facetIdentifier, Filter filter, boolean initialContentRequired) {
		key = UID.spi.create();
		this.dependencyType = dependencyType;
		this.scope = scope;
		this.meem = meem;
		this.meemPath = meem.getMeemPath();
		this.facetIdentifier = facetIdentifier;
		this.filter = filter;
		this.initialContentRequired = initialContentRequired;
	}
	
	/**
	 * Constructs an instance of <code>DependencyAttribute</code> without a filter.
	 * <p>
	 * @param dependencyType the type of Dependency resolution that will occur.
	 * @param scope The extent to which MeemRegistry will go to locate the Meem.
	 * @param meem The meem cotaining the target facet.
	 * @param facetIdentifier The identifier of the Facet
	 */	
	public DependencyAttribute(DependencyType dependencyType, Scope scope, Meem meem, String facetIdentifier) {
		this(dependencyType, scope, meem, facetIdentifier, null, true);
	}
	
	/**
	 * Return the key associated with this attribute.
	 * 
	 * @return the key UID associated with this attribute.
	 */
	public Serializable getKey() {
		return key;
	}
	
	/**
	 * Provides the Facet identifier.
	 * <p>
	 * @return Facet identifier
	 */
	public String getFacetIdentifier() {
		return facetIdentifier;
	}
	
	/**
	 * Return the meem path associated with this attribute.
	 * 
	 * @return the meem path for the meem the dependency is on.
	 */
	public MeemPath getMeemPath() {
		MeemPath mp = this.meemPath;
		if (meem != null) {
			mp = meem.getMeemPath();
			if (mp == null) {
				throw new RuntimeException("MeemPath is null in DependencyAttribute");
			}
		}
		return mp;
	}
	
	/**
	 * Return the meem associated with this dependency attribute.
	 * 
	 * @return a meem.
	 */
	public Meem getMeem() {
		return meem;
	}

	/**
	 * Set the meem associated with this dependency attribute.
	 * 
	 * @param meem the meem the dependency is on.
	 */
	public void setMeem(Meem meem) 
	{
		this.meem = meem; 
	}
	
	/**
	 * Set the facet identifier associated with this dependency attribute.
	 * 
	 * @param facetIdentifier the facetIdentifier this dependency will connect to.
	 */
	public void setFacetIdentifier(String facetIdentifier) {
		this.facetIdentifier = facetIdentifier;
	}
	
	/**
	 * Set the meem path for the meem associated with this dependency attribute.
	 * 
	 * @param meemPath the meem path of the meem this dependency will connect to.
	 */
	public void setMeemPath(MeemPath meemPath) {
		this.meemPath = meemPath;
	}
	
	/**
	 * Set the filter associated with the dependency represented by this attribute.
	 * 
	 * @param filter the filter that will be applied to messages.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	/**
	 * Return the filter associated with this attribute.
	 * 
	 * @return a filter.
	 */
	public Filter getFilter() {
		return filter;
	}
	
	
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try { 
			return super.clone();
		} catch (CloneNotSupportedException e) { 
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Returns the DependencyType.
	 * <p>
	 * @return DependencyType
	 */
	public DependencyType getDependencyType() {
	  return(dependencyType);
	}

	/**
	 * Returns the Scope.
	 * <p>
	 * The Scope is used by MeemRegistry to determine to what extext that
	 * the provider Meem is located, e.g. within MemePlex, within JVM,
	 * across LAN or across WAN.
	 * </p>
	 * @return Scope
	 */
	public Scope getScope() {
	  return(scope);
	}

	/**
	 * Assigns the DependencyType.
	 * <p>
	 * @param dependencyType The type of the Dependency
	 */

	public void setDependencyType(
	  DependencyType dependencyType) {

	  this.dependencyType = dependencyType;
	}

	/**
	 * Assigns the Scope.
	 * <p>
	 * @param scope The visibility of a Meem
	 */
	public void setScope(Scope scope) {
	  this.scope = scope;
	}
	
	/**
	 * Returns the initial content required setting.
	 * <p>
	 * @return Returns true if initial content is required
	 */
	public boolean isInitialContentRequired() {
		return initialContentRequired;
	}
	
	/**
	 * Specifies if initial content is required.
	 * <p> 
	 * @param initialContentRequired Whether initial content is required
	 */
	public void setInitialContentRequired(boolean initialContentRequired) {
		this.initialContentRequired = initialContentRequired;
	}

	/**
	 * Compares DependencyAttribute to the specified object.
	 * The result is true, if and only if all of the meemPath,
	 * facetIdentifier, dependencyType and Scope are equal.
	 *
	 * @return true if DependencyAttributes are equal
	 */
	public synchronized boolean contentEquals(DependencyAttribute other) {
		if(other == null) return false;
		if(other == this) return true;

		if(!getDependencyType().equals(other.getDependencyType())) return false;
		if(!getScope().equals(other.getScope())) return false;
		if(!getFacetIdentifier().equals(other.getFacetIdentifier())) return false;
		if (!getMeemPath().equals(other.getMeemPath())) return false;
		if (isInitialContentRequired() != other.isInitialContentRequired()) return false;
		
		return this.getKey().equals(other.getKey());
	}
	
	/**
	 * Equality test based on the value of getKey().
	 */
	public synchronized boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj == this) return true;

		if(obj instanceof DependencyAttribute) 
		{
			return getKey().equals(((DependencyAttribute)obj).getKey());
		}
		
		return false;
	}
	
	/**
	 * Returns the Object hashCode.
	 * <p>
	 *
	 * @return DependencyAttribute hashCode
	 */
	public synchronized int hashCode() {
		return getKey().hashCode();
	}

	/**
	 * Returns a String representation of this <code>DependencyAttribute</code>.
	 * <p>
	 * @return A string representation of this <code>DependencyAttribute</code>.
	 */

	public synchronized String toString() {
		MeemPath meemPath = this.meemPath;
		if (meem != null) {
			meemPath = meem.getMeemPath();
		}
		
	  return(
	  "[Key=" + getKey().toString() + 
		", dependencyType="  + dependencyType +
		", scope="           + scope +
		", facetIdentifier=" + facetIdentifier + 
		", meemPath="        + meemPath +
		(initialContentRequired ? ", initial content required" : "") + 
		"]"
	  );
	}
	
}
