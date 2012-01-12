/*
 * @(#)EclipseTerminator.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.tool.eclipse.util.log;

import org.swzoo.log2.component.LogNode;
import org.swzoo.log2.core.LogEvent;

/**
 * <p>
 * ...
 * </p>
 * @author  mg
 * @version 1.0
 */
public class EclipseTerminator extends LogNode {
	private static final long serialVersionUID = 6424227717462161145L;

	private EclipseLog log;

	public EclipseTerminator(EclipseLog log) {
		this.log = log;
	}

	/**
	 * Put log event into the list
	 * @see org.swzoo.log2.core.Logger#log(org.swzoo.log2.core.LogEvent)
	 */
	public synchronized void log(LogEvent event) {
		log.log(event);
	}

}
