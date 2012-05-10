/*
 * @(#)RemoteOutboundInvocationEvent.java
 *
 * Copyright 2005 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.invocation;

import java.lang.reflect.Method;

import org.openmaji.meem.MeemPath;


/**
 * @author mg
 */
public class RemoteOutboundInvocationEvent extends AbstractInvocationEvent {
	private static final long serialVersionUID = 534540102928363464L;

	private static final String REMOTE_OUTBOUND_INVOCATION = "Remote Outbound";
	
	public RemoteOutboundInvocationEvent(MeemPath sourceMeemPath, MeemPath targetMeemPath, Method targetMethod, Object[] targetMethodArgs) {
		super(sourceMeemPath, targetMeemPath, targetMethod, targetMethodArgs);
	}
	
	public String getEventName() {
		return REMOTE_OUTBOUND_INVOCATION;
	}
}
