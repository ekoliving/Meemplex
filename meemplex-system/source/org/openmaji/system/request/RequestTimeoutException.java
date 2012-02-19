/*
 * @(#)RequestTimeoutException.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.system.request;

/**
 * @author mg
 */
public class RequestTimeoutException extends Exception {
	private static final long serialVersionUID = 534540102928363464L;

	public RequestTimeoutException(String message) {
		super(message);
	}
	
	public RequestTimeoutException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
