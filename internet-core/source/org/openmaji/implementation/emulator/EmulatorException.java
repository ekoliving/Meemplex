/*
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.emulator;

/**
 * Exception class used by the emulator package. 
 * 
 * @author Chris Kakris
 */
public class EmulatorException extends Exception
{
	private static final long serialVersionUID = 0L;
	
  public EmulatorException(String message)
  {
    super(message);
  }
}
