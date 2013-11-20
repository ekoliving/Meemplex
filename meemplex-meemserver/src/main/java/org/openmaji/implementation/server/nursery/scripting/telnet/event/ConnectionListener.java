/*
 * @(#)ConnectionListener.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */

/*
 * This code is based upon code from the TelnetD library.
 * http://sourceforge.net/projects/telnetd/
 * Used under the BSD license.
 */


package org.openmaji.implementation.server.nursery.scripting.telnet.event;

/**
 * Interface to be implemented if a class wants to
 * qualify as a ConnectionListener.
 *
 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent
 */
 public interface ConnectionListener {

	/**
	 * Called when a CONNECTION_TIMEDOUT event occured.
	 * 
	 * @param ce ConnectionEvent instance.
	 *
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent#CONNECTION_TIMEDOUT 
	 */
	 public void connectionTimedOut(ConnectionEvent ce);
	
	/**
	 * Called when a CONNECTION_LOGGEDOFF event occured.
	 * 
	 * @param ce ConnectionEvent instance.
	 *
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent#CONNECTION_LOGGEDOFF
	 */
	 public void connectionLoggedOff(ConnectionEvent ce);
	
	/**
	 * Called when a CONNECTION_LOGOUTREQUEST occured.
	 *
	 * @param ce ConnectionEvent instance.
	 *
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent#CONNECTION_LOGOUTREQUEST
	 */ 
	 public void connectionLogoutRequest(ConnectionEvent ce);
	 
	/**
	 * Called when a CONNECTION_BROKEN event occured.
	 * 
	 * @param ce ConnectionEvent instance.
	 *
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent#CONNECTION_BROKEN	 
	 */ 
	 public void connectionBroken(ConnectionEvent ce);
	
}//interface ConnectionListener