/*
 * @(#)DiagnosticEvent.java
 *
 * Copyright 2005 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.nursery.diagnostic;

import java.io.Serializable;

/**
 * @author mg
 */
public interface DiagnosticEvent extends Serializable {

	public long getEventID();	
	public String getEventName();
	
	public long getTimeStamp();
	public String getThreadName();
	
}
