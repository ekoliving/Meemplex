/*
 * @(#)SuspendedRequestImpl.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.request;

import org.openmaji.system.request.RequestSuspendedException;
import org.openmaji.system.request.SuspendedRequest;

/**
 * @author mg
 */
public class SuspendedRequestImpl implements SuspendedRequest {
	
	private final RequestStack requestStack;
	private Request request = null;
	private boolean resumed = false;
	
	public SuspendedRequestImpl(Object uid) throws RequestSuspendedException {
		requestStack = (RequestStack) RequestTracker.getRequestStack().clone();
		request = requestStack.peekRequest();
		if (request != null) {
			if (request.suspended == null) {
				request.suspended = uid;
			} else {
				throw new RequestSuspendedException("Request " + request + " already suspended by " + uid);
			}
		}
	}
	
	/**
	 * @see org.openmaji.system.request.SuspendedRequest#resume()
	 */
	public synchronized void resume() {
		if (!resumed) {
			resumed = true;
			request.suspended = null;
			RequestTracker.setRequestStack(requestStack);
		}
	}

}
