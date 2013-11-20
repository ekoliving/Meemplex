/*
 * @(#)AbstractSession.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.telnet.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import jline.Terminal;
import jline.TerminalFactory;
import jline.TerminalFactory.Flavor;
import jline.console.ConsoleReader;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionEvent;
import org.openmaji.implementation.server.nursery.scripting.telnet.event.ConnectionListener;
import org.openmaji.implementation.server.nursery.scripting.telnet.util.TelnetSessionLog;


import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public abstract class AbstractSession extends Thread implements ConnectionListener, TelnetSessionLog {
	//protected TelnetConnection telnetConnection;

	//protected BasicTerminalIO terminalIO;

	private Socket socket;
	
	protected ConsoleReader consoleReader;

	protected boolean isAlive = true;
	
	protected InputStream instream;
	protected OutputStream outstream;
	
	public AbstractSession(Socket socket, String name) throws IOException {

		super(name);
		
		this.socket = socket;

		Terminal term = null;

		try {
			term = TerminalFactory.getFlavor(Flavor.UNIX);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
//		instream = new DataInputStream(socket.getInputStream());
//		outstream = new DataOutputStream(socket.getOutputStream());
		instream = socket.getInputStream();
		outstream = socket.getOutputStream();

		consoleReader = new ConsoleReader(name, instream, outstream, term);
		
//		BufferedReader reader = new BufferedReader( new InputStreamReader(consoleReader.getInput()) );
//		consoleReader.setPrompt("prompt: ");
//		consoleReader.setEchoCharacter(new Character('\0'));
//		while (true) {
//			String line = reader.readLine();
//			while (line == null || line.equals("\n") || line.equals("\r") || line.isEmpty()) {
//				System.out.println("skipping : " + line);
//				line = reader.readLine();
//				//line = consoleReader.readLine("prompt: ");
//			}
//			System.out.println("got string : " + line);
//			consoleReader.println("nice");
//			consoleReader.flush();
//		}

	}

	public abstract void run();

	public synchronized void close() {
		//terminalIO.close();
	}

	public synchronized void connectionTimedOut(ConnectionEvent ce) {
		synchronized (this) {
			isAlive = false;
			close();
		}
	}

	public synchronized void connectionLoggedOff(ConnectionEvent ce) {
		synchronized (this) {
			isAlive = false;
			close();
		}
	}

	public synchronized void connectionLogoutRequest(ConnectionEvent ce) {
	}

	public synchronized void connectionBroken(ConnectionEvent ce) {
		synchronized (this) {
			isAlive = false;
			close();
		}
	}

	public void error(String message) {

		logger.log(Level.WARNING, message);
	}

	public void info(String message) {

		logger.log(Level.INFO, message);
	}

	public void trace(String message) {

		logger.log(logLevel, message);
	}

	public void verbose(String message) {

		logger.log(logLevelVerbose, message);
	}

	public void warn(String message) {

		logger.log(Level.WARNING, message);
	}

	protected Socket getSocket() {
		return socket;
	}
	
	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static Logger logger = Logger.getAnonymousLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static Level logLevel = Common.getLogLevel();

	/**
	 * Acquire the Maji system-wide verbose logging level.
	 */

	private static Level logLevelVerbose = Common.getLogLevelVerbose();
}
