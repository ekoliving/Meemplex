/*
 * @(#)UID.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.utility.uid;

import java.io.Serializable;

import org.openmaji.spi.MajiSPI;


/**
 * <p>
 * A Unique IDentifier (UID) is used to distinguish between one object and
 * another, particularly in situations where the object can move between
 * different Java Virtual Machines.
 * </p>
 * <p>
 * Individual UIDs must be unique, of course.  The equals() and hashcode()
 * methods should be properly implemented to ensure that proper comparisions
 * can be made and that UIDs can be used as Collection keys.
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */
public interface UID extends Serializable {
    
  /**
   * <p>
   * Provides a String representation of the UID.
   * </p>
   * <p>
   * It is required that the implementation provides a String
   * representation that can be used to "reconstruct" the UID.
   * In other words, setUIDString(getUIDString()) must work.
   * </p>
   * @return String representation of the UID
   */
  public String getUIDString();

  /**
   * Compares UID to the specified object.
   * The result is true, if and only if the UIDs are equal.
   *
   * @param object Object to be compared for equality
   * @return true, if UIDs are equal
   */
  public boolean equals(
    Object object);
  
  /**
   * Provide the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return UID hashCode
   */
  public int hashCode();
  
  /**
   * Provides a String representation of UID for diagnostics.
   *
   * @return String representation of UID
   */
  public String toString();


  /**
   * Nested class for service provider interface.
   * 
   * @see org.openmaji.spi.MajiSPI
   */
  public class spi {
  	/**
  	 * Create a random UID.
  	 * 
  	 * @return a UID
  	 */
    public static UID create() {
      return((UID) MajiSPI.provider().create(UID.class));
    }

	/**
	 * Create a UID from a string.
	 * 
	 * @param uidString the string representing the UID.
	 * @return the UID.
	 */
    public static UID create(
      String uidString) {

      return((UID) MajiSPI.provider().create(UID.class, uidString));
    }
    
    /**
     * Create a UID from the SHA1 hash of the identifier.
     * 
     * @param identifier string to be feed into the hash.
     * @return a UID based on identifier.
     */
    public static UID createSHA1(
      String identifier) {

      return ((UID) MajiSPI.provider().create(UID.class, new Object[] {identifier, "SHA1"}));
    }
  }
}
