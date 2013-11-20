/*
 * @(#)MajiProvider.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.spi;

/**
 * <p>
 * The MajiProvider interface provides the basis for the service provider used by maji to
 * create classes. The purpose behind this is to allow the underlying classes supporting
 * </p>
 * <p>
 * Note: Implementation thread safe = Not considered yet
 * </p>
 * @author  Andy Gelme
 * @version 1.0
 */

public interface MajiProvider {

  /**
   * Create an object from an underlying implementation where the constructor requires
   * no arguments.
   * 
   * @param specification the class to instance the object from.
   */
  public <T> T create(Class<T> specification);

  /**
   * Create an object from an underlying implementation where the constructor just takes
   * one argument.
   * 
   * @param specification the class to instance the object from.
   * @param arg the object to be passed as an argument.
   */
  public <T> T create(Class<T>  specification, Object arg);
    
	/**
	 * Create an object from an underlying implementation where the constructor takes
	 * multiple arguments.
	 * 
	 * @param specification the class to instance the object from.
	 * @param args the array of arguments to passed to the constructor.
	 */
  public <T> T create(Class<T> specification, Object[] args);
}