/*
 * @(#)SuspendedRequest.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.system.request;

/**
 * A SuspendedRequest is generated when RequestContext.suspend() is called.
 * It is used to resume a suspended request (eg: when a non-Maji non-blocking method 
 * returns) 
 * @author mg
 */
public interface SuspendedRequest {
	
	/**
	 * Resumes the request that was susspended by a previous call to RequestContext.suspend().
	 * Has no effect if the request has already been resumed.
	 */
	public void resume();
}
