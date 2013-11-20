/*
 * @(#)Log.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.telnet.util;

/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @version 1.0
 */
public interface TelnetSessionLog {
	/**
   * Convenience method for creating error logging messages.
   * This "hook" provides an easy way for external scripts to enter
   * a message into the Maji platform log system.
   *
   * @param message Error message to be logged
   */

	public void error(String message);
	
	/**
	 * Convenience method for creating informational logging messages.
	 * This "hook" provides an easy way for external scripts to enter
	 * a message into the Maji platform log system.
	 *
	 * @param message Informational message to be logged
	 */

	public void info(String message);

	/**
	 * Convenience method for creating trace logging messages.
	 * This "hook" provides an easy way for external scripts to enter
	 * a message into the Maji platform log system.
	 *
	 * @param message Trace message to be logged
	 */

	public void trace(String message);

	/**
	 * Convenience method for creating verbose logging messages.
	 * This "hook" provides an easy way for external scripts to enter
	 * a message into the Maji platform log system.
	 *
	 * @param message Verbose message to be logged
	 */

	public void verbose(String message);

	/**
	 * Convenience method for creating warning logging messages.
	 * This "hook" provides an easy way for external scripts to enter
	 * a message into the Maji platform log system.
	 *
	 * @param message Warning message to be logged
	 */

	public void warn(String message);
}
