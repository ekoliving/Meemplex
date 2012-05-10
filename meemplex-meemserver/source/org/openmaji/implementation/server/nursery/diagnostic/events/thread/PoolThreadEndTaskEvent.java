/*
 * @(#)PoolThreadEndTaskEvent.java
 *
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.thread;


/**
 * @author mg
 */
public class PoolThreadEndTaskEvent extends AbstractPoolThreadEvent {
	private static final long serialVersionUID = 534540102928363464L;

	private static final String POOL_THREAD_END = "Pool Thread End Task";
	
	public PoolThreadEndTaskEvent() {
		super(Thread.currentThread().getName());
		
		PoolThreadTracker.setThreadID(-1);
	}

	public String getEventName() {
		return POOL_THREAD_END;
	}

}
