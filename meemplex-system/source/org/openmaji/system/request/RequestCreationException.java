/*
 * @(#)RequestCreationException.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.request;

/**
 * A RequestCreationException will be thrown if a new request is created with a timeout not greater than zero
 * or the context is null.
 * @author mg
 */
public class RequestCreationException extends Exception {
	private static final long serialVersionUID = 534540102928363464L;

	public RequestCreationException(String message) {
		super(message);
	}
	
}
