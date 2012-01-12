/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment;

public interface TagRegistrator
{
  public void register(String tag, Class<? extends Descriptor> descriptorClassname);
}
