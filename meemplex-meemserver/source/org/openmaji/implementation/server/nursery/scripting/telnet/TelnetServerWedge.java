/*
 * @(#)TelnetServerWedge.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.telnet;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.nursery.scripting.beanshell.BeanShellSession;
import org.openmaji.implementation.server.nursery.scripting.telnet.session.AbstractSession;

import org.openmaji.meem.Wedge;
import org.openmaji.meem.definition.*;
import org.openmaji.meem.wedge.lifecycle.*;
import org.swzoo.log2.core.*;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class TelnetServerWedge implements TelnetServer, MeemDefinitionProvider, Wedge {

	public static final String TELNET_SERVER_SESSION_PROPERTIES = "org.openmaji.server.telnet.session";

	private HashMap<Integer, ServerThread> serverThreads = new HashMap<Integer, ServerThread>();

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	public void commence() {
		// get properties of sessions to start from system properties
		// properites are of the form
		// TELNET_SERVER_SESSION_PROPERTIES.<port>=<session class>

		HashMap<String, String> sessions = new HashMap<String, String>();

		Properties properties = System.getProperties();
		for (Object key : properties.keySet()) {
			String keyName = (String) key;
			if (keyName.startsWith(TELNET_SERVER_SESSION_PROPERTIES)) {
				sessions.put(keyName, (String) properties.get(keyName));
			}
		}

		for (Map.Entry<String, String> entry : sessions.entrySet()) {
			String key = entry.getKey();
			String portString = key.substring(key.lastIndexOf(".") + 1).trim();
			int port = -1;
			try {
				port = new Integer(portString).intValue();
			}
			catch (NumberFormatException e) {
				LogTools.error(logger, "Specified port number is not a number: " + portString);
				continue;
			}
			String sessionClassName = entry.getValue().trim();
			addServer(port, sessionClassName);
		}
	}

	public void conclude() {
		Iterator<Integer> iterator = serverThreads.keySet().iterator();
		while (iterator.hasNext()) {
			Integer port = (Integer) iterator.next();
			removeServer(port.intValue());
		}
	}

	public void addServer(int port, String sessionClass) {
		if (Common.TRACE_ENABLED && Common.TRACE_TELNET_SERVER) {
			LogTools.trace(logger, logLevel, "Starting telnet server on port " + port + " for session class " + sessionClass);
		}

		ServerThread serverThread = new ServerThread(port, sessionClass);

		serverThreads.put(Integer.valueOf(port), serverThread);
	}

	public void removeServer(int port) {

		ServerThread serverThread = (ServerThread) serverThreads.get(new Integer(port));
		if (serverThread != null) {
			if (Common.TRACE_ENABLED && Common.TRACE_TELNET_SERVER) {
				LogTools.trace(logger, logLevel, "Stopping telnet server on port " + port);
			}
			serverThread.stop();
		}
	}

	class ServerThread implements Runnable {

		private int port;

		private String sessionClassName;

		private Thread thisThread;

		private ServerSocket serverSocket = null;

		private Vector<AbstractSession> sessions = new Vector<AbstractSession>();

		public ServerThread(int port, String sessionClassName) {
			this.port = port;
			this.sessionClassName = sessionClassName;
			this.thisThread = new Thread(this, "Telnet Server: " + sessionClassName.substring(sessionClassName.lastIndexOf(".") + 1));

			this.thisThread.start();
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {

			try {
				serverSocket = new ServerSocket(port);
			}
			catch (IOException e) {
				LogTools.error(logger, "Unable to create ServerSocket on port " + port, e);
				return;
			}

			while (serverSocket != null && serverSocket.isClosed() == false) {
				try {
					Socket socket = serverSocket.accept();

					AbstractSession session = null;

					// got a new connection. Start a new session
					try {
						Class<?> sessionClass = Class.forName(sessionClassName);
						Constructor<?> sessionClassConstructor = sessionClass.getConstructor(new Class[] { Socket.class });
						session = (AbstractSession) sessionClassConstructor.newInstance(new Object[] { socket });
						
					}
					catch (Exception e) {
						LogTools.error(logger, "Exception while creating new telnet session", e);
						e.printStackTrace();
					}
					
					if (session != null) {
						sessions.add(session);
	
						if (Common.TRACE_ENABLED && Common.TRACE_TELNET_SERVER) {
							LogTools.trace(logger, logLevel, "Starting telnet session on port " + port + " for session class " + sessionClassName + " from " + socket.getInetAddress());
						}
						
						session.start();
					}
				}
				catch (IOException e) {
					break;
				}
			}

			// cleanup
			for (AbstractSession session : sessions) {
				session.close();
			}
		}

		public void stop() {
			synchronized (this) {
				try {
					serverSocket.close();
				}
				catch (IOException e) {
					LogTools.error(logger, "Exception while stopping telnet server", e);
				}
				serverSocket = null;
			}
		}
	}

	public static void main(String[] args) {
		TelnetServer telnetServer = new TelnetServerWedge();
		telnetServer.addServer(6969, "org.openmaji.implementation.server.nursery.scripting.beanshell.BeanShellSession");
		//telnetServer.addServer(24, "org.openmaji.implementation.nursery.telnet.session.PythonSession");
		LogTools.info(logger, "maji dir: " + System.getProperty(Common.PROPERTY_MAJI_HOME));
		LogTools.info(logger, "beanshell dir: " + System.getProperty(BeanShellSession.PROPERTY_BEANSHELL_DIRECTORY));
	}

	/* ---------- MeemDefinitionProvider method(s) ----------------------------- */

	private MeemDefinition meemDefinition = null;

	public MeemDefinition getMeemDefinition() {
		if (meemDefinition == null) {
			meemDefinition = MeemDefinitionFactory.spi.create().createMeemDefinition(new Class[] { this.getClass() });
		}

		return (meemDefinition);
	}

	/* ---------- Logging fields ----------------------------------------------- */

	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */

	private static Logger logger = LogFactory.getLogger();

	/**
	 * Acquire the Maji system-wide logging level.
	 */

	private static int logLevel = Common.getLogLevelVerbose();

}
