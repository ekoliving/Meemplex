/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment;

public interface ProgressConduit
{
  public void reset();

  /**
   * Add more points for completion.
   * 
   * @param points
   */
  public void addCompletionPoints(int points);

  /**
   * Add to progress.
   * 
   * @param points
   */
  public void addProgressPoints(int points);
}
