/*
 * @(#)MajiException.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.meem;

/**
 * Base class for all Maji system exceptions. This class should 
 * be extended when creating new exception types within Maji.
 * 
 * @author mg
 */
public class MajiException extends Exception {
	private static final long serialVersionUID = -1178365040590887015L;

	public MajiException() {
		super();
	}

	public MajiException(String message) {
		super(message);
	}

	public MajiException(String message, Throwable cause) {
		super(message, cause);
	}

	public MajiException(Throwable cause) {
		super(cause);
	}
}