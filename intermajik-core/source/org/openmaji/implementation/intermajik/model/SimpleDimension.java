/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.intermajik.model;

import java.io.Serializable;

public class SimpleDimension implements Serializable
{
	private static final long serialVersionUID = 6424227717462161145L;

  public int width;
  public int height;
  
  public SimpleDimension(int width, int height)
  {
    this.width = width;
    this.height = height;
  }
}
