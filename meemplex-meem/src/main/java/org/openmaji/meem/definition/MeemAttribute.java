/*
 * @(#)MeemAttribute.java
 * Created on 9/07/2003
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.meem.definition;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import org.openmaji.utility.uid.UID;


/**
 * Provides the basic attributes for a meem - it's identifier and scope.
 * <p>
 * @author Kin Wong
 * @version 1.1
 */

public final class MeemAttribute implements Cloneable, Serializable {
	private static final long serialVersionUID = 2196602392087626645L;

	static private final String DEFAULT_IDENTIFIER = "";

	static private final Scope DEFAULT_SCOPE = Scope.LOCAL;

	static private final int DEFAULT_VERSION = 1;

	private UID key;

	private String identifier;

	private Scope scope;

	private int version;
  
  /**
   * ImmutableAttributes allows developers to place custom information that
   * is strongly associated with the instance of a Meem, prior to the creation
   * of that Meem.  Once the ImmutableAttributes have been sealed, then whilst
   * a Meem is LOADED or at a higher LifeCycle state, there is no way to make
   * further changes to this structure.
   * 
   * All LifeCycleManager implementations *must* automatically invoke
   * <code>sealImmutableAttributes()<code> whenever a Meem is created.
   * Whilst it is possible for a MeemDefinition author to "seal" the
   * ImmutableAttributes themselves (just to be paranoid), typically
   * you should leave this up to the LifeCycleManager. 
   * 
   * Note: There is no way that this mechanism by itself can protect against
   * changes to the MeemDefinition or MeemContent whilst a Meem is stored or
   * distributed outside of the care of a MeemServer.
   * 
   * The initial use-case for ImmutableAttributes is for the Service Rights
   * Management (SRM) licensing system.  So, that a "license identifier" can
   * be attached to a Meem.
   * 
   * The design is documented at ...
   * https://dev.majitek.com/snipsnap/comments/maji-design-035-immutable-attributes
   */  

  private Hashtable<Object,Object> immutableAttributes = new Hashtable<Object,Object>();
  
  /**
   * Indicates whether ImmutableAttributes have been sealed,
   * so that no further changes will be accepted, i.e they
   * have become truly immutable.
   * 
   * This is an irreversable operation.
   */
  
  private boolean immutableAttributesSealed = false;
	
	/**
	 * Constructs an instance of <code>MeemAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this <code>MeemAttribute</code>.
	 * @param scope The Scope used by Meem Registry to determine what extext of
	 * Meem visibility.
	 * @param version The version of this <code>MeemAttribute</code>.
	 */
	public MeemAttribute(String identifier, Scope scope, int version) {
		this.key = UID.spi.create();
		this.identifier = identifier;
		this.scope = scope;
		this.version = version;
	}
	
	/**
	 * Constructs an instance of <code>MeemAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this <code>MeemAttribute</code>.
	 * @param scope The Scope used by Meem Registry to determine what extext of
	 * Meem visibility.
	 */
	public MeemAttribute(String identifier, Scope scope) {
		this(identifier, scope, DEFAULT_VERSION);
	}
	
	/**
	 * Constructs an instance of <code>MeemAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this <code>MeemAttribute</code>.
	 */	
	public MeemAttribute(String identifier) {
		this(identifier, DEFAULT_SCOPE);
	}
	
	/**
	 * Constructs an instance of <code>MeemAttribute</code>.
	 * <p>
	 */
	public MeemAttribute() {
		this(DEFAULT_IDENTIFIER);
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
	 * Return the key associated with this object.
	 * @return the unique key for this object.
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * Provides the identifier.
	 *
	 * @return Means of distinguishing the MeemAttribute
	 */

	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the Scope of this <code>MeemAttribute</code>.
	 * <p>
	 * The Scope is used by MeemRegistry to determine what extext that the
	 * Meem is visible, i.e. can be located.
	 * </p>
	 * @return Scope
	 */

	public Scope getScope() {
		return scope;
	}

	/**
	 * Returns the version number of this <code>MeemAttribute</code>.
	 * <p>
	 * @return Version number of this <code>MeemAttribute</code>.
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the identifier of this <code>MeemAttribute</code>.
	 * <p>
	 * @param identifier The identifier of this <code>MeemAttribute</code>.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * Sets the scope of this <code>MeemAttribute</code>.
	 * <p>
	 * @param scope The visibility of a Meem.
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * Sets a version number of this <code>MeemAttribute</code>.
	 * <p>
	 * Whenever the MeemAttribute is changed, the version number should be
	 * incremented.  MeemStore should only persist a MeemAttribute, if the
	 * version number is greater than that of the currently persisted version.
	 * </p>
	 * @param version Version number
	 */

	public void setVersion(int version) {
		this.version = version;
	}
  
  /**
   * Assign the value of an ImmutableAttribute.
   * The value for a given key may only be assigned once.
   * This method will not function after sealImmutateAttributes()
   * has been invoked.
   *
   * @param key Index for a given ImmutableAttribute
   * @param value ImmutableAttribute value
   * @exception IllegalStateException ImmutableAttributes sealed or specific ImmutableAttribute already assigned
   */
  
  public void setImmutableAttribute(
    Object key,
    Object value)
    throws IllegalStateException {
    
    if (immutableAttributesSealed) {
      throw new IllegalStateException("ImmutableAttributes have been sealed");
    }
    
    if (immutableAttributes.contains(key)) {
      throw new IllegalStateException(
        "ImmutableAttribute key: " + key + " has already been assigned"
      );
    }
    
    immutableAttributes.put(key, value);
  }
  
  /**
   * Provide the value of an ImmutableAttribute.
   *
   * @param key Index for the required ImmutableAttribute value
   * @return ImmutableAttribute value for the given key
   * @exception IllegalArgumentException ImmutableAttribute key isn't valid
   */

  public Object getImmutableAttribute(
    Object key)
    throws IllegalArgumentException {

    if (immutableAttributes.containsKey(key) == false) {
      throw new IllegalArgumentException(
        "ImmutableAttribute key: " + key + " isn't valid"
      );
    }
    
    return(immutableAttributes.get(key));
  }

  /**
   * Prevent further modification to the ImmutableAttributes.
   * 
   * This is an irreversable operation.
   */
  
  public void sealImmutableAttributes() {
    immutableAttributesSealed = true;
  }

  /**
   * Copy the value of one MeemAtribute object to another,
   * whilst preserving the integrity of the ImmutableAttributes
   * and the <code>immutableAttributesSealed <code> value. 
   *
   * @param targetMeemAttribute MeemAttribute to be updated
   * @param sourceMeemAttribute MeemAttribute to be assigned
   */

  public static void copyPreservingImmutableAttributes(
    MeemAttribute targetMeemAttribute,
    MeemAttribute sourceMeemAttribute) {
    
    if (targetMeemAttribute == null) {
      targetMeemAttribute = (MeemAttribute) sourceMeemAttribute.clone();
    }
    else {
      targetMeemAttribute.identifier = sourceMeemAttribute.identifier;
      targetMeemAttribute.key        = sourceMeemAttribute.key;
      targetMeemAttribute.scope      = sourceMeemAttribute.scope;
      targetMeemAttribute.version    = sourceMeemAttribute.version;
      
      for (Map.Entry<Object, Object> entry: sourceMeemAttribute.immutableAttributes.entrySet()) {
      	Object key = entry.getKey();
      	if (!targetMeemAttribute.immutableAttributes.containsKey(key)) {
      		targetMeemAttribute.setImmutableAttribute(key, entry.getValue());
      	}
      }
    }
  }

	/**
	 * Compares <code>MeemAttribute</code> to the specified object.
	 * <p>
	 * The result is true, if and only if the hash code are equal.
	 * @return true if MeemDefinitions are equal
	 * @see #contentEquals(MeemAttribute)
	 */
	public boolean equals(Object object) {
		if(object instanceof MeemAttribute) {
			MeemAttribute meemType = (MeemAttribute)object;
			return getKey().equals(meemType.getKey());
		}
		return false;
	}
	
	/**
	 * Compares the content of this <code>MeemAttribute</code> to another, without including
	 * the unique key.
	 * @param meemType A <code>MeemAttribute</code> to compare to.
	 * @return boolean true if and only if the content of both 
	 * <code>MeemAttribute</code> are identical, otherwise false.
	 */	
	public boolean contentEquals(MeemAttribute meemType) {
		if(getVersion() != meemType.getVersion()) return false;
		if(!getIdentifier().equals(meemType.getIdentifier())) return false;
		if(!getScope().equals(meemType.getScope())) return false;
		return true;
	}

	/**
	 * Provides the Object hashCode.
	 * <p>
	 * Must follow the Object.hashCode() and Object.equals() contract.
	 * @return The hashCode of this <code>MeemAttribute</code>.
	 */
	public int hashCode() {
		return getKey().hashCode();
	}

  /**
   * Provides a String representation of <code>MeemAttribute</code>.
   *
   * @return String representation of MeemAttribute
   */

  public synchronized String toString() {
    return(
      getClass().getName() + "[" +
        "identifier=" + identifier      +
      ", scope="      + scope +
      ", version="    + version +
      ", key="     +    key +
      "]"
    );
  }
}
