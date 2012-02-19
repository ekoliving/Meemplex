/*
 * @(#)ConnectionEvent.java
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

import org.openmaji.implementation.server.nursery.scripting.telnet.TelnetConnection;


/**
 * Class implmenting a ConnectionEvent.
 * These events are used to communicate things that are
 * supposed to be handled within the application context.
 * These events are processed by the Connection instance
 * calling upon its registered listeners.
 *
 * @see org.openmaji.implementation.server.nursery.scripting.telnet.TelnetConnection
 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionListener
 */
public class ConnectionEvent {

	//Members
	private int myType;
	//Associations
	private TelnetConnection mySource;

	/**
	 * Constructs a new instance of a ConnectionEvent
	 * with a given source (Connection) and a given type.
	 * 
	 * @param source Connection that represents the source of this event.
	 * @param typeid int that contains one of the defined event types.
	 */
	 public ConnectionEvent(TelnetConnection source, int typeid){
		myType=typeid;
		mySource=source;
	 }//constructor

	/**
	 * Accessor method returning the source of the
	 * ConnectionEvent instance.
	 * 
	 * @return Connection representing the source. 
	 */
	 public TelnetConnection getSource() {
	 	return mySource;
	 }//getSource
	 
	/**
	 * @deprecated for better naming, replaced by getSource
	 * @see #getSource()
	 */
	 public TelnetConnection getConnection(){
		return mySource;
	 }//getConnection

	 /**
	  * Method that helps identifying the type.
	  * 
	  * @param typeid int that contains one of the defined event types.
	  */
	  public boolean isType(int typeid){
		return (myType==typeid);
	  }//isType


//Constants

	/**
	 * Defines the connection timed out event type.
	 * It occurs if a connection has been idle exceeding
	 * the configured time to warning. 
	 */
	 public static final int CONNECTION_TIMEDOUT=100;
	
	/**
	 * Defines the connection logged off event type.
	 * It occurs if a connection has been idle exceeding
	 * the configured time to warning and the configured time
	 * to disconnect, and will be closed by the system.
	 */
	 public static final int CONNECTION_LOGGEDOFF=101;

	/**
	 * Defines the connection requested logout event type.
	 * It occurs if a connection requested disgraceful logout by
	 * sending a <Ctrl>-<D> key combination.
	 */
	 public static final int CONNECTION_LOGOUTREQUEST=102;

	/**
	 * Defines the connection broken event type.
	 * It occurs if a connection has to be closed by
	 * the system due to communication problems (i.e. I/O errors). 
	 */
	 public static final int CONNECTION_BROKEN=103;
	
	
}//class ConnectionEvent