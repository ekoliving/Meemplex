/*
 * @(#)RemoteInboundInvocationEvent.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.invocation;

import java.lang.reflect.Method;

import org.openmaji.meem.MeemPath;


/**
 * @author mg
 */
public class RemoteInboundInvocationEvent extends AbstractInvocationEvent {
	private static final long serialVersionUID = 534540102928363464L;

	private static final String REMOTE_INBOUND_INVOCATION = "Remote Inbound";
	
	public RemoteInboundInvocationEvent(MeemPath sourceMeemPath, MeemPath targetMeemPath, Method targetMethod, Object[] targetMethodArgs) {
		super(sourceMeemPath, targetMeemPath, targetMethod, targetMethodArgs);
	}
	
	public String getEventName() {
		return REMOTE_INBOUND_INVOCATION;
	}
}
