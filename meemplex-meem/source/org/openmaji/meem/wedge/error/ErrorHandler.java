/*
 * @(#)ErrorHandler.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.meem.wedge.error;

import org.openmaji.meem.Facet;

/**
 * <p>
 * The facet used to propogate exceptions from within a meem to the outside world,
 * also used to support the errorHandler conduit.
 * </p>
 * @author  mg
 * @version 1.0
 */
public interface ErrorHandler extends Facet {
	
	/**
	 * Signal an exception has been thrown.
	 * 
	 * @param throwable the exception to be passed on
	 */
	public void thrown(
    Throwable throwable);
  
/**
 * Nested class for service provider.
 * 
 * @see org.openmaji.spi.MajiSPI
 */
  public class spi {
    public static String getIdentifier() {
      return("errorHandler");
    };
  }
}