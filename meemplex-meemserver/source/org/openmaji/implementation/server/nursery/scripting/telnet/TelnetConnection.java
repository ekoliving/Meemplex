/*
 * @(#)TelnetConnection.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.telnet;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent;
import org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionListener;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.TerminalIO;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class TelnetConnection {

	private Socket socket;

	private long lastActivity;

	private boolean warned;

	private String negotiatedTerminalType;

	private int[] terminalGeometry;

	private boolean terminalGeometryChanged = true;

	private boolean isAlive = true;

	private Vector<ConnectionListener> listeners = new Vector<ConnectionListener>();

	private TerminalIO terminalIO;

	public TelnetConnection(Socket socket) {
		this.socket = socket;

		terminalGeometry = new int[2];
		terminalGeometry[0] = 80; // width
		terminalGeometry[1] = 25; // height
		negotiatedTerminalType = "default";

		activity();

		terminalIO = new TerminalIO(this);

	}

	/**
	 * Method that registers a ConnectionListener with the Connection instance.
	 * 
	 * @param connectionListener
	 *            ConnectionListener to be registered.
	 * 
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionListener
	 */
	public void addConnectionListener(ConnectionListener connectionListener) {
		listeners.add(connectionListener);
	}

	/**
	 * Method that removes a ConnectionListener from the Connection instance.
	 * 
	 * @param connectionListener
	 *            ConnectionListener to be removed.
	 * 
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionListener
	 */
	public void removeConnectionListener(ConnectionListener connectionListener) {
		listeners.remove(connectionListener);
	}

	/**
	 * Method called by the io subsystem to pass on a "low-level" event. It will be properly delegated to all registered listeners.
	 * 
	 * @param connectionEvent
	 *            ConnectionEvent to be processed.
	 * 
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent
	 */
	public void processConnectionEvent(ConnectionEvent connectionEvent) {
		for (int i = 0; i < listeners.size(); i++) {
			ConnectionListener cl = (ConnectionListener) listeners.elementAt(i);
			if (connectionEvent.isType(ConnectionEvent.CONNECTION_LOGGEDOFF)) {
				cl.connectionLoggedOff(connectionEvent);
				isAlive = false;
			}
			else if (connectionEvent.isType(ConnectionEvent.CONNECTION_TIMEDOUT)) {
				cl.connectionTimedOut(connectionEvent);
				isAlive = false;
			}
			else if (connectionEvent.isType(ConnectionEvent.CONNECTION_LOGOUTREQUEST)) {
				cl.connectionLogoutRequest(connectionEvent);
			}
			else if (connectionEvent.isType(ConnectionEvent.CONNECTION_BROKEN)) {
				isAlive = false;
				cl.connectionBroken(connectionEvent);
			}
		}
	}

	public boolean isAlive() {
		return isAlive;
	}

	/**
	 * Method to access the associated terminal io.
	 * 
	 * @return BasicTerminalIO associated with the Connection instance.
	 * 
	 * @see org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO
	 */
	public BasicTerminalIO getTerminalIO() {
		return terminalIO;
	}

	/**
	 * Returns a reference to the socket the Connection is associated with.
	 * 
	 * @return Reference to the associated Socket.
	 * 
	 * @see java.net.Socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Returns the remote port to which the socket is connected.
	 * 
	 * @return String that contains the remote port number to which the socket is connected.
	 */
	public int getPort() {
		return socket.getPort();
	}

	/**
	 * Returns the fully qualified host name for the connection's IP address.<br>
	 * The name is cached on creation for performance reasons. Subsequent calls will not result in resolve queries.
	 * 
	 * @return String that contains the fully qualified host name for this address.
	 * 
	 */
	public String getHostName() {
		return socket.getInetAddress().getHostName();
	}

	/**
	 * Returns the IP address of the connection.
	 * 
	 * @return String that contains the connection's IP address.<br>
	 *         The format "%d.%d.%d.%d" is well known, where %d goes from zero to 255.
	 */
	public String getHostAddress() {
		return socket.getInetAddress().getHostAddress();
	}

	/**
	 * Returns the InetAddress object associated with the connection.
	 * 
	 * @return InetAddress associated with the connection.
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/**
	 * Returns a timestamp of the last activity that happened on the associated connection.
	 * 
	 * @return the timestamp as a long representing the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
	 */
	public long getLastActivity() {
		return lastActivity;
	}

	/**
	 * Sets a new timestamp to the actual time in millis retrieved from the System. This will remove an idle warning flag if it has been set. Note that you can use this behaviour
	 * to implement your own complex idle timespan policies within the context of your application.<br>
	 * The check frequency of the ConnectionManager should just be set according to the lowest time to warning and time to disconnect requirements.
	 */
	public void activity() {
		warned = false;
		lastActivity = System.currentTimeMillis();
	}

	/**
	 * Sets the state of the idle warning flag.<br>
	 * Note that this method will also update the the timestamp if the idle warning flag is removed, which means its kind of a second way to achieve the same thing as with the
	 * activity method.
	 * 
	 * @param bool
	 *            true if a warning is to be issued, false if to be removed.
	 * 
	 * @see #activity()
	 */
	public void setWarned(boolean bool) {
		warned = bool;
		if (!bool) {
			lastActivity = System.currentTimeMillis();
		}
	}

	/**
	 * Returns the state of the idle warning flag, which will be true if a warning has been issued, and false if not.
	 * 
	 * @return the state of the idle warning flag.
	 */
	public boolean isWarned() {
		return warned;
	}

	/**
	 * Sets the terminal geometry data.<br>
	 * <em>This method should not be called explicitly
	 * by the application (i.e. the its here for the io subsystem).</em><br>
	 * A call will set the terminal geometry changed flag.
	 * 
	 * @param width
	 *            of the terminal in columns.
	 * @param height
	 *            of the terminal in rows.
	 */
	public void setTerminalGeometry(int width, int height) {
		terminalGeometry[0] = width;
		terminalGeometry[1] = height;
		terminalGeometryChanged = true;
	}

	/**
	 * Returns the terminal geometry in an array of two integers.
	 * <ul>
	 * <li>index 0: Width in columns.
	 * <li>index 1: Height in rows.
	 * </ul>
	 * A call will reset the terminal geometry changed flag.
	 * 
	 * @return integer array containing width and height.
	 */
	public int[] getTerminalGeometry() {
		// we toggle the flag because the change should now be known
		if (terminalGeometryChanged)
			terminalGeometryChanged = false;
		return terminalGeometry;
	}

	/**
	 * Returns the width of the terminal in columns for convenience.
	 * 
	 * @return the number of columns.
	 */
	public int getTerminalColumns() {
		return terminalGeometry[0];
	}

	/**
	 * Returns the height of the terminal in rows for convenience.
	 * 
	 * @return the number of rows.
	 */
	public int getTerminalRows() {
		return terminalGeometry[1];
	}

	/**
	 * Returns the state of the terminal geometry changed flag, which will be true if it has been set, and false if not.
	 * 
	 * @return the state of the terminal geometry changed flag.
	 */
	public boolean isTerminalGeometryChanged() {
		return terminalGeometryChanged;
	}

	/**
	 * Sets the terminal type that has been negotiated between telnet client and telnet server, in form of a String.<br>
	 * 
	 * <em>This method should not be called explicitly
	 * by the application (i.e. the its here for the io subsystem).</em><br>
	 * 
	 * @param termtype
	 *            the negotiated terminal type as String.
	 */
	public void setNegotiatedTerminalType(String termtype) {
		negotiatedTerminalType = termtype;
	}

	/**
	 * Returns the terminal type that has been negotiated between the telnet client and the telnet server, in of a String.<br>
	 * 
	 * @return the negotiated terminal type as String.
	 */
	public String getNegotiatedTerminalType() {
		return negotiatedTerminalType;
	}
}
