/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment;

public interface Progress
{
  public void setCompletionPoint(int endpoint);
  public int getCompletionPoint();

  public void setProgressPoint(int progressPoint);
  public int getProgressPoint();
}
