/*
 * @(#)IllegalFilterException.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.meem.filter;

import java.io.Serializable;

/**
 * <p>
 * An IllegalFilterException is thrown when a FilterChecker receives a
 * Filter type that is unknown to it or inappropriate in the given
 * circumstances.
 * </p>
 * <p>
 * Note: Implementation thread safe = Yes (2003-02-13)
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.filter.FilterChecker
 */

public class IllegalFilterException extends Exception implements Serializable {

	private static final long serialVersionUID = -1178365040590887015L;

  /**
   * <p>
   * Create IllegalFilterException without a detail message.
   * </p>
   */

  public IllegalFilterException() {
  }

  /**
   * <p>
   * Create IllegalFilterException with a detail message.
   * </p>
   * @param message Detail concerning the cause of the IllegalFilterException
   */

  public IllegalFilterException(
    String message) {

    super(message);
  }
}
