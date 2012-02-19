/*
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.deployment.wedge;

import java.util.Collection;

import org.openmaji.implementation.deployment.Descriptor;

/**
 * This interface is implemented by conduits for handling the processing of
 * deployment descriptors passed from one wedge to another.
 *
 * @author Chris Kakris
 */
public interface DeploymentProcessor
{
  public void setDescriptors(Collection<Descriptor> deploymentDescriptors);
  
  public void addDescriptors(Collection<Descriptor> deploymentDescriptors);
 
  public void processDescriptors();
}
