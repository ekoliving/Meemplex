/*
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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
