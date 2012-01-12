/*
 * @(#)TimeStampEntry.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.jini.lookup;

import java.util.Date;

import net.jini.entry.AbstractEntry;

/**
 * <p>
 * Entry used to keep track of the time the meem was added to the Jini Lookup Service
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public class TimeStampEntry extends AbstractEntry {
	private static final long serialVersionUID = 534540102928363464L;

	public final Date timeStamp;

	public TimeStampEntry() {
		timeStamp = new Date();
	}

	/**
	 * @return Date this entry was created.
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * @param timeStampEntry entry to compare with
	 * @return true if this TimeStampEntry is newer than the passed in TimeStampEntry 
	 */
	public boolean isNewer(TimeStampEntry timeStampEntry) {
		return timeStamp.after(timeStampEntry.getTimeStamp());
	}

}
