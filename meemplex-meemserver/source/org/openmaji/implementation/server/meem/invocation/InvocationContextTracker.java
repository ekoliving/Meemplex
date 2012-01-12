/*
 * @(#)InvocationContextTracker.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.meem.invocation;


/**
 * @author mg
 */
public class InvocationContextTracker {
	
	private static class ThreadInvocationContext extends ThreadLocal<InvocationContext> {
		public InvocationContext initialValue() {
			InvocationContext requestStack = new InvocationContext();
			return requestStack;
		}
	}

	private static ThreadInvocationContext threadInvocationContext = new ThreadInvocationContext();

	public static InvocationContext getInvocationContext() {
		return (InvocationContext) threadInvocationContext.get();
	}

	public static void setInvocationContext(InvocationContext invocationContext) {
		threadInvocationContext.set(invocationContext);
	}

}