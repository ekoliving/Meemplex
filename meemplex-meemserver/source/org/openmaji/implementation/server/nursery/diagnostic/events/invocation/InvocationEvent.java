/*
 * @(#)InvocationEvent.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic.events.invocation;

import org.openmaji.implementation.server.nursery.diagnostic.DiagnosticEvent;
import org.openmaji.implementation.server.request.RequestStack;
import org.openmaji.meem.MeemPath;


/**
 * @author mg
 */
public interface InvocationEvent extends DiagnosticEvent {
	
	public long getThreadID();
	
	public MeemPath getSourceMeemPath();

	public MeemPath getTargetMeemPath();

	public Object[] getTargetMethodArgs();

	public String getTargetMethodName();

	public RequestStack getRequestStack();
}