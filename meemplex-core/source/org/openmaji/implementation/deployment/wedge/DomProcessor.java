/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment.wedge;

import org.jdom.Element;

/**
 * This interface is implemented by conduits that are used to pass DOM 
 * representations of an XML from one wedge to another.
 *
 * @author Chris Kakris
 */
public interface DomProcessor
{
  public void process(Element rootElement);
}
