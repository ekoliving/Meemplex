/*
 * @(#)AbstractPoolThreadEvent.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.thread;

import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticLog;

/**
 * @author mg
 */
public abstract class AbstractPoolThreadEvent implements PoolThreadEvent {
	
	private long eventID;
	private long timeStamp;
	
	private String threadName;
	
	protected AbstractPoolThreadEvent(String threadName) {
		this.eventID = DiagnosticLog.getEventID();
		this.timeStamp = System.currentTimeMillis();
		
		this.threadName = threadName;
	}
	
	public long getEventID() {
		return eventID;
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}
}
