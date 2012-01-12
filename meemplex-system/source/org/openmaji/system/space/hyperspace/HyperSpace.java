/*
 * @(#)HyperSpace.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

package org.openmaji.system.space.hyperspace;

import org.openmaji.system.space.Category;

/**
 * <p>
 * Hyperspace is the root category for hyperspace meem paths.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not applicable
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 * @see org.openmaji.meem.space.Space
 */

public interface HyperSpace extends Category {
    
	/**
	 * Nested class for service provider.
	 * 
	 * @see org.openmaji.spi.MajiSPI
	 */
  public class spi {
    public static String getIdentifier() {
      return("hyperSpace");
    };
  }    
}