/*
 * Copyright 2006 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.util.wedge;

import org.openmaji.meem.Facet;

public interface SpeedControl extends Facet
{
  void backward();
  
  void forward();
  
  void pause();
  
  void speedUp();
  
  void slowDown();
}
