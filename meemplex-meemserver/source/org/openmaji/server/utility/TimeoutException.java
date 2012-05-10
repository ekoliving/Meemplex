/*
 * @(#)TimeoutException.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 * 
 * Created on 30/07/2003
 */
package org.openmaji.server.utility;

/**
 * TimeoutException
 * 
 * @author stormboy
 */
public class TimeoutException extends Exception {

	private static final long serialVersionUID = 8958928927L;

	/**
	 * 
	 */
	public TimeoutException() {
		super();
	}

	/**
	 * @param message
	 */
	public TimeoutException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TimeoutException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
