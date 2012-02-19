/*
 * @(#)CreatePoolThreadEvent.java
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
public class CreatePoolThreadEvent extends AbstractPoolThreadEvent {
	private static final long serialVersionUID = 534540102928363464L;

	private static final String CREATE_POOL_THREAD = "Pool Thread Created";
	
	public CreatePoolThreadEvent(String threadName) {
		super(threadName);
	}
	
	public String getEventName() {
		return CREATE_POOL_THREAD;
	}
	
}
