/*
 * @(#)RequestStack.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.request;

import java.io.Serializable;
import java.util.*;

/**
 * @author mg
 */
public class RequestStack extends Stack<Request> implements Cloneable, Serializable {
	private static final long serialVersionUID = 534540102928363464L;

	public static String REQUEST_STACK = "requestStack";

	public synchronized void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
	}

	public synchronized void pushRequest(Request request) {
		push(request);
	}

	public synchronized void popRequest(Request request) {
		int index = indexOf(request);
		if (index >= 0) {
			removeRange(index, size());
		}
	}

	public synchronized Request peekRequest() {
		try {
			return super.peek();
		}
		catch (EmptyStackException e) {
			return null;
		}
	}

	public synchronized Request matchLatestRequest(Collection<Request> requests) {
		Request result = null;
		for (Request request : requests) {
			if (requests.contains(request)) {
				result = request;
			}
		}

		return result;
	}

	public String toString() {
		return "RequestStack[" + hashCode() + "]: " + super.toString();
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return super.clone();
	}

}
