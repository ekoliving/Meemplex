/*
 * @(#)TerminatorSingleton.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util.log;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


import org.openmaji.utility.CollectionUtility;
import org.swzoo.log2.core.LogEvent;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class EclipseLog {
	
	private static EclipseLog instance = new EclipseLog();
	
	private Set logEntries = Collections.synchronizedSet(CollectionUtility.createLinkedHashSet());
	private EclipseTerminator terminator = new EclipseTerminator(this);
	private Set listeners = CollectionUtility.createHashSet();
	
	private EclipseLog() {
	}
	
	public static EclipseLog getInstance() {
		return instance;
	}
	
	public EclipseTerminator getTerminator() {
		return terminator;
	}
	
	public synchronized void log(LogEvent event) {
		logEntries.add(event);
		notifyListeners(event);	
	}
	
	public synchronized Set addListener(EclipseLogListener listener) {
		listeners.add(listener);
		return logEntries;
	}
	
	public synchronized void removeListener(EclipseLogListener listener) {
		listeners.remove(listener);
	}
	
	private void notifyListeners(LogEvent event) {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EclipseLogListener listener = (EclipseLogListener)i.next();
			listener.event(event);
		}
	}
}
