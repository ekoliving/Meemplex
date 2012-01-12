/*
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment;

import org.jdom.Element;

/**
 * A Descriptor that may be configured with an XML elements.
 */
public interface Descriptor
{
  public void processElement(Element element);
}
