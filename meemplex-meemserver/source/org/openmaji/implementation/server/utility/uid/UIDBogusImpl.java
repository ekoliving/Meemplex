/*
 * @(#)UIDBogusImpl.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.implementation.server.utility.uid;

import java.io.Serializable;
import java.util.Random;

/**
 * <p>
 * A Unique IDentifier (UID) is used to distinguish between one object and
 * another, particularly in situations where the object can move between
 * different Java Virtual Machines.
 * </p>
 * <p>
 * This is a UID implementation using a bogus random number generator to
 * create the identifier.  UIDBogusImpl is likely to "break" (generate
 * duplicates) if used in serious production environments.
 * </p>
 * <p>
 * This is a quick and cheerful UID implementation that is good for having
 * quick development turnaround (edit-compile-execute), without the long
 * startup overhead of UIDImpl (due to SecureRandom).
 * Also, the shorter identifiers are easier to remember !
 * </p>
 * <p>
 * To use this implementation, set the following property ...
 * <ul>
 *   <li>org.openmaji.utility.UIDImplClassName=
 *       org.openmaji.implementation.utility.UIDBogusImpl
 * </ul>
 * </p>
 * <p>
 * Note: Implementation thread safe = Single-threaded (2003-05-22)
 * </p>
 * @author Andy Gelme
 * @version 1.0
 * @see org.openmaji.utility.uid.UID
 * @see org.openmaji.implementation.server.utility.uid.UIDBase
 * @see org.openmaji.implementation.server.utility.uid.UIDImpl
 */

public class UIDBogusImpl extends UIDBase implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;

  /**
   * Attempt to create every UID with a unique identifier.
   * Depend on this at your peril !
   */

  private static Random seed = new Random();
  
  /**
   * Instantiate a new UIDBogusImpl with an unique (cough, cough) identifier.
   */
  
  public UIDBogusImpl() {
    leastSignificantBits = seed.nextInt(999999);
  }

  /**
   * Provides a String representation of UIDBogusImpl.
   *
   * @return String representation of UIDBogusImpl
   */

  public String toString() {
    return("UIDBogusImpl[" + super.toString() + "]");
  }
}
