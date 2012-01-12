/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.intermajik.model;

import java.io.Serializable;

public class SimplePoint implements Serializable
{
	private static final long serialVersionUID = 6424227717462161145L;

  public int x;
  public int y;
  
  public SimplePoint(int x, int y)
  {
    this.x = x;
    this.y = y;
  }
}
