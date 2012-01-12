/*
 * @(#)PoolThreadStartTaskEvent.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.thread;


/**
 * @author mg
 */
public class PoolThreadStartTaskEvent extends AbstractPoolThreadEvent {
	private static final long serialVersionUID = 534540102928363464L;

	private static final String POOL_THREAD_START = "Pool Thread Start Task";
	
	public PoolThreadStartTaskEvent() {
		super(Thread.currentThread().getName());
		
		PoolThreadTracker.setThreadID(getEventID());
	}

	public String getEventName() {
		return POOL_THREAD_START;
	}

}
