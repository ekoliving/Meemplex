/*
 * @(#)PoolThreadTracker.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.thread;


/**
 * @author mg
 */
public class PoolThreadTracker {
	
	private static class ThreadTracker extends ThreadLocal {
		public Object initialValue() {
			return new Long(-1);
		}
	}

	private static ThreadTracker threadTracker = new ThreadTracker();

	public static long getThreadID() {
		return ((Long) threadTracker.get()).longValue();
	}

	protected static void setThreadID(long newThreadID) {
		threadTracker.set(new Long(newThreadID));
	}

}