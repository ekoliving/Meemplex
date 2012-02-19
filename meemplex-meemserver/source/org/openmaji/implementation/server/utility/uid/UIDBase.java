/*
 * @(#)UIDBase.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.utility.uid;

import java.io.Serializable;

import org.openmaji.utility.uid.UID;


/**
 * <p>
 * A Unique IDentifier (UID) is used to distinguish between one object and
 * another, particularly in situations where the object can move between many
 * Java Virtual Machines.
 * </p>
 * <p>
 * This is an implementation that uses a 128-bit unique identifier.
 * </p>
 * <p>
 * The actual technique for generating the unique identifier is not
 * defined within this class, leaving that job for more specialized
 * versions of this base class.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-05-22)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.utility.uid.UID
 * @see org.openmaji.implementation.server.utility.uid.UIDBogusImpl
 * @see org.openmaji.implementation.server.utility.uid.UIDImpl
 */

public abstract class UIDBase implements UID, Serializable {

  /**
   * UIDBase String representation delimiter
   */

  protected static final String HYPHEN = "-";

  /**
   * Most significant (upper) 64 bits of the unique identifier
   */

  protected long mostSignificantBits = 0;

  /**
   * Least significant (lower) 64 bits of the unique identifier
   */

  protected long leastSignificantBits = 0;

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

  public synchronized String getUIDString() {
    return(
      digits(mostSignificantBits   >> 32, 8) + HYPHEN +
      digits(mostSignificantBits   >> 16, 4) + HYPHEN +
      digits(mostSignificantBits,  4)        + HYPHEN +
      digits(leastSignificantBits  >> 48, 4) + HYPHEN +
      digits(leastSignificantBits, 12)
    );
  }

  /**
   * Convert a bit value into a hexadecimal of a specified length.
   *
   * @param value Bit value to be converted into a hexadecimal String
   * @param length Length of hexadecimal String
   * @return Hexadecimal String representation
   */

  private String digits(
    long value,
    int  length) {

    long hiDigit = 1L << (length * 4);

    return(
      Long.toHexString(hiDigit | (value & (hiDigit - 1))).substring(1)
    );
  }

  /**
   * <p>
   * Save 16 element byte[] array as an internal UID representation.
   * Correct formatting as a valid Jini Service ID is performed.
   * </p>
   * @param uidBuffer  16 element byte[] array representation of the UID
   */

  public synchronized void setAsValidJiniServiceID(
    byte[] uidBuffer) {

    uidBuffer[6]  &= 0x0f;
    uidBuffer[6]  |= 0x40; /* Version 4 */
    uidBuffer[8]  &= 0x3f;
    uidBuffer[8]  |= 0x80; /* IETF variant */
    uidBuffer[10] |= 0x80; /* Multicast bit */

    for (int index = 0; index < 8; index++) {
      mostSignificantBits =
        (mostSignificantBits << 8) | (uidBuffer[index] & 0xff);
    }

    for (int index = 8; index < 16; index++) {
      leastSignificantBits =
        (leastSignificantBits << 8) | (uidBuffer[index] & 0xff);
    }
  }
  
	
  /**
   * Compares UID to the specified object.
   * The result is true, if and only if the UIDs are equal.
   *
   * @param object Object to be compared for equality
   * @return true, if UIDs are equal
   */

  public synchronized boolean equals(
    Object object) {

    if ((object instanceof UIDBase) == false) return(false);

    UIDBase uidBase = (UIDBase) object;

    return(
      mostSignificantBits  == uidBase.mostSignificantBits   &&
      leastSignificantBits == uidBase.leastSignificantBits
    );
  }

  /**
   * Provide the Object hashCode.
   * Must follow the Object.hashCode() and Object.equals() contract.
   *
   * @return UID hashCode
   */

  public synchronized int hashCode() {
    return(
      (int) ((mostSignificantBits  >> 32) ^ mostSignificantBits ^
             (leastSignificantBits >> 32) ^ leastSignificantBits)
    );
  }

  /**
   * Provides a String representation of UID for diagnostics.
   *
   * @return String representation of UID
   */

  public String toString() {
    return(getUIDString());
  }
}
