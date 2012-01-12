/*
 * @(#)RequestIdGenerator.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.request;


/**
 * @author mg
 */
public class RequestTracker {

	private static class ThreadRequest extends ThreadLocal {
		public Object initialValue() {
			RequestStack requestStack = new RequestStack();
			return requestStack;
		}
	}

	private static ThreadRequest threadRequest = new ThreadRequest();

	public static RequestStack getRequestStack() {
		return (RequestStack) threadRequest.get();
	}

	public static void setRequestStack(RequestStack requestStack) {
		threadRequest.set(requestStack);
	}

}