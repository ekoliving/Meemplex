/*
 * @(#)MajiSystemProvider.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * -ag- Complete JavaDoc.
 */

package org.openmaji.system.spi;

import java.util.Collection;

import org.openmaji.spi.MajiProvider;
import org.openmaji.spi.MajiSPI;

/**
 * <p>
 * The base implementation for a Maji Service Provider.
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public abstract class MajiSystemProvider extends MajiSPI
  implements MajiProvider {

  protected MajiSystemProvider() {}

  public static MajiSystemProvider systemProvider() {
    return((MajiSystemProvider) MajiSPI.provider());
  }

  public abstract void addSpecificationEntry(
    SpecificationEntry specificationEntry)
    throws IllegalArgumentException, IllegalStateException;
    
      
  public abstract Class<?> getImplementation(
    Class<?> specification)
    throws IllegalArgumentException;
      
  public abstract Class<?> getSpecification(
    String identifier);    

  public abstract Collection<Class<?>> getSpecifications();

  public abstract Collection<Class<?>> getSpecifications(
    SpecificationType specificationType);
    
  public abstract SpecificationEntry getSpecificationEntry(
    Class<?> specification);

  public abstract void removeSpecificationEntry(
    Class<?> specification)
    throws IllegalArgumentException, IllegalStateException;
}