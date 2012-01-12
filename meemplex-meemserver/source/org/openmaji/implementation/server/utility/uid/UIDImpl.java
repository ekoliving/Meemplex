/*
 * @(#)UIDImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - Consider using net.jini.id.UuidFactory.generate().
 */

package org.openmaji.implementation.server.utility.uid;

import java.io.Serializable;
import java.security.*;

/**
 * <p>
 * A Unique IDentifier (UID) is used to distinguish between one object and
 * another, particularly in situations where the object can move between
 * different Java Virtual Machines.
 * </p>
 * <p>
 * This is a UID implementation using an 128-bit unique identifier generated
 * via SecureRandom.
 * </p>
 * <p>
 * There is a significant startup time associated with creating
 * a new SecureRandom (about 10 seconds on a Pentium III 450Mhz).
 * If you need to generate a lot of UIDs (i.e more than one !),
 * it is recommended that a quantity of UIDImpls are created
 * within a single JVM invocation.
 * </p>
 * <p>
 * When testing (and ONLY when testing), try using UIDBogusImpl to
 * avoid this long startup time.
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-05-22)
 * </p>
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.utility.uid.UID
 * @see org.openmaji.implementation.server.utility.uid.UIDBase
 * @see org.openmaji.implementation.server.utility.uid.UIDBogusImpl
 */

public class UIDImpl extends UIDBase implements Serializable {
	private static final long serialVersionUID = -8453677642499648767L;

  /**
   * Create a new Random number generator (this is expensive in terms of time)
   */

  private static final SecureRandom secureRandom = new SecureRandom();

  /**
   * Instantiate a new UIDImpl with an unique identifier.
   */

  public UIDImpl() {
    byte[] uidBuffer = new byte[16];  // 128-bit buffer for SecureRandom

    secureRandom.nextBytes(uidBuffer);

    setAsValidJiniServiceID(uidBuffer);
  }

	/**
	 * Reconstruct a UID from it's String representation.
   * </p>
   * <p>
   * It is required that the implementation can "reconstruct" a
   * UID from it's own String representation.
   * </p>
   * @param uidString String representation of the UID
   * @exception IllegalArgumentException Invalid UID String representation
   */
  public UIDImpl(
    String uidString) 
    throws IllegalArgumentException {

    if (uidString.length() != 36																	||
      uidString.substring( 8, 9).equals(UIDBase.HYPHEN) == false	||
      uidString.substring(13,14).equals(UIDBase.HYPHEN) == false	||
      uidString.substring(18,19).equals(UIDBase.HYPHEN) == false	||
      uidString.substring(23,24).equals(UIDBase.HYPHEN) == false) {
      	throw new IllegalArgumentException("Invalid UID string");
    }
    
    long part1 = Long.parseLong(uidString.substring( 0,  8), 16);
    long part2 = Long.parseLong(uidString.substring( 9, 13), 16);
    long part3 = Long.parseLong(uidString.substring(14, 18), 16);
    long part4 = Long.parseLong(uidString.substring(19, 23), 16);
    long part5 = Long.parseLong(uidString.substring(24, 36), 16);

    mostSignificantBits  = (part1 << 32) + (part2 << 16) + part3;
    leastSignificantBits = (part4 << 48) +  part5;

		// -mg- this should also check to make sure the uid is a valid jini uid
  }
  
  /**
   * Instantiate a new UIDImpl with an unique identifier using a 
   * specific MessageDigest. If the messageDigestName parameter is
   * null, the SHA1 digest will be used.
   *
   * @param  identifier Name used to produce a unique identifier
   * @param  messageDigestName Message digest name. eg: SHA1, MD5
   */

  public UIDImpl(
    String identifier, String messageDigestName) {

		if (messageDigestName == null) {
			messageDigestName = "SHA1";
		}

    try {
      MessageDigest messageDigest = 
      	MessageDigest.getInstance(messageDigestName);

      byte[] uidBuffer = messageDigest.digest(identifier.getBytes());

      setAsValidJiniServiceID(uidBuffer);
    }
    catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(
        "Couldn't generate UID using " + messageDigestName + 
				" algorithm: " + noSuchAlgorithmException
      );
    }
  }

  /**
   * Provides a String representation of UIDImpl.
   *
   * @return String representation of UIDImpl
   */

  public String toString() {
    return("UIDImpl[" + super.toString() + "]");
  }
}
