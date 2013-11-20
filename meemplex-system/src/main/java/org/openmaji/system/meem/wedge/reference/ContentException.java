/*
 * @(#)ContentException.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.meem.wedge.reference;

/**
 * The exception thrown by ContentProvider in the event of a problem sending content.
 * 
 * @author Peter
 */
public class ContentException extends Exception
{
	private static final long serialVersionUID = 534540102928363464L;

	/**
	 * Base constructor.
	 */
	public ContentException()
	{
		super();
	}

    /**
     * Create the exception with the passed in message.
     * 
     * @param message a message to be associated with the exception.
     */
	public ContentException(String message)
	{
		super(message);
	}

	/**
	 * Create the exception with the passed in throwable.
	 * 
	 * @param cause an underlying throwable to be associated with the exception.
	 */
	public ContentException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Create the exception with the passed in message and throwable.
	 * 
	 * @param message a message to be associated with the exception.
	 * @param cause an underlying throwable to be associated with the exception.
	 */
	public ContentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
