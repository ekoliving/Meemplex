/*
 * @(#)WedgeValidationException.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.meem.wedge.lifecycle;

/**
 * Exception that can be thrown by the validation method, if defined, in a wedge.
 */
public class WedgeValidationException
	extends Exception
{
	private static final long serialVersionUID = -1178365040590887015L;

    /**
     * Base constructor.
     */
    public WedgeValidationException()
    {
    }
    
	/**
	  * Constructor that associates a message with the exception.
	  * 
	  * @param message the message to be passed.
	  */
	 public WedgeValidationException(
	 	String message)
	 {
	 	super(message);
	 }
}