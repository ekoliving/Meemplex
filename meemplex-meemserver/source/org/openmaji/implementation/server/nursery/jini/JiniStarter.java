/*
 * @(#)JiniStarter.java
 *
 * Copyright 2004 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.jini;

/**
 * Starts a class server and reggie server.  Also listens for command on a socket
 * to know when to shutdown.
 * 
 * @author  mg
 * @author  stormboy
 * @version 1.1
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Enumeration;

import net.jini.admin.Administrable;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.utility.PropertiesLoader;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import com.sun.jini.admin.DestroyAdmin;
import com.sun.jini.start.NonActivatableServiceDescriptor;
import com.sun.jini.start.ServiceDescriptor;
import com.sun.jini.tool.ClassServer;

public class JiniStarter {
	private static Logger logger = LogFactory.getLogger();

	/** the property name for the NIC to bind to */
	public static final String NIC_KEY = "org.openmaji.jini.nic";

	/** the property name for the specific address to bind to */
	public static final String ADDRESS_KEY = "org.openmaji.jini.address";

	/** the property name for the port to bind the class-server to */
	public static final String PORT_KEY = "org.openmaji.jini.port";

	private static final String PROPERTY_JINI_CONFIG = "org.openmaji.jini.configuration";
	
	private static final String PROPERTY_JINI_JARS = "org.openmaji.jini.jars";
	
	private static final String PROPERTY_REGGIE_POLICY = "reggie.policy";
	
	private static final String PROPERTY_HTTP_URL = "http.url";
	
	/** the default port for the class-server */
	public static final int DEFAULT_PORT = 8081;

	/** a port to listen on to stop the jini starter process */
	public static final String JINI_STARTER_PORT = "org.openmaji.implementation.server.nursery.jini.JiniStarterPort";

	/** classpath for the jini starter process */
	public static final String JINI_STARTER_CLASSPATH = "org.openmaji.implementation.server.nursery.jini.JiniStarterClassPath";

	public static final String COMMAND_SHUTDOWN = "shutdown";
	
	// paths
	
	private static final String JINI_LIB_DIR = "/lib/jini";
	
	private static final String JINI_CONFIG_DIR = "/conf/jini"; 
	
	private static final String POLICY_FILE = "/conf/security/all.policy";
	
	private static final String START_REGGIE_CONFIG = "/start-reggie.config";

	// private members
	
	private String jiniJarDir = null;

	private String policyFile = null;

	private String jiniConfigDir = null;

	private String jiniConfigFile = null;

	private String hostAddress = null;

	private int hostPort = -1;

	private Object reggieServiceProxy = null;

	private ClassServer classServer = null;

	/**
	 * Start the jini services.
	 */
	protected void commence() {
		LogTools.info(logger, "commencing...");

		if ( !validateEnvironment() ) {
			return;
		}

		LogTools.info(logger, "checking class-server port...");
		
		// start the class server
		if (portNotBound(hostPort)) {
			LogTools.info(logger, "starting class-server...");
			try {
				classServer = new ClassServer(hostPort, jiniJarDir + "-dl", false, false);
			}
			catch (IOException e) {
				LogTools.error(logger, "commence() - failed to launch ClassServer. ", e);
			}
			classServer.start();
		}

		// start reggie
		try {
			LogTools.info(logger, "starting reggie...");
			
			System.setProperty(PROPERTY_HTTP_URL, "http://" + hostAddress + ":" + hostPort);
			System.setProperty(PROPERTY_JINI_CONFIG, jiniConfigDir);
			System.setProperty(PROPERTY_JINI_JARS, jiniJarDir);
			System.setProperty(PROPERTY_REGGIE_POLICY, policyFile);
			
			LogTools.info(logger, "policy: " + policyFile + " : configDir: " + jiniConfigDir + " : jars: " + jiniJarDir);

			Configuration configuration = ConfigurationProvider.getInstance(new String[] { jiniConfigFile });
			ServiceDescriptor[] descs = (ServiceDescriptor[])  configuration.getEntry("com.sun.jini.start", "serviceDescriptors", ServiceDescriptor[].class, null);
			Object created = descs[0].create(configuration);

			if (created instanceof NonActivatableServiceDescriptor.Created) {
				reggieServiceProxy = ((NonActivatableServiceDescriptor.Created) created).proxy;
			}

		}
		catch (Exception ex) {
			LogTools.error(logger, "commence() - failed to launch reggie", ex);
		}
	}

	protected void conclude() {
		if (reggieServiceProxy != null) {
			try {
				if (reggieServiceProxy instanceof Administrable) {
					Object obj = ((Administrable) reggieServiceProxy).getAdmin();
					if (obj instanceof DestroyAdmin) {
						((DestroyAdmin) obj).destroy();
					}
				}

			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		if (classServer != null) {
			classServer.terminate();
			classServer = null;
		}
	}

	/* ------- private methods of this wedge ------------------------------------- */

	private boolean portNotBound(int port) {
		try {
			Socket socket = new Socket(hostAddress, port);
			socket.close();
			return false;
		}
		catch (Exception ex) {
			return true;
		}
	}

	private boolean validateEnvironment() {
		if ( !validateHostAndPort() ) {
			return false;
		}

		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
		
		// check for proper deployment
		jiniConfigDir = majitekDirectory + JINI_CONFIG_DIR;
		jiniConfigFile = jiniConfigDir + START_REGGIE_CONFIG;

		File file = new File(jiniConfigFile);
		if (file.exists()) {
			LogTools.info(logger, "validateEnvironment() - Running from installed InterMajik");
			
			jiniJarDir = majitekDirectory + JINI_LIB_DIR;
			policyFile = majitekDirectory + POLICY_FILE;
			return true;
		}

		// running in development environment
		jiniConfigDir = majitekDirectory + "/majitek-source/project-maji-runtime/conf/jini";
		jiniConfigFile = jiniConfigDir + "/start-reggie.config";
		file = new File(jiniConfigFile);
		if (file.exists()) {
			LogTools.info(logger, "validateEnvironment() - Running from eclipse launched InterMajik");
			jiniJarDir = majitekDirectory + "/majitek-jars/jini";
			policyFile = majitekDirectory + "/majitek-source/project-maji-runtime/security/all.policy";
			return true;
		}

		LogTools.error(logger, "validateEnvironment() - Unable to locate start-reggie.config");
		return false;
	}

	public boolean validateHostAndPort() {
		String nicName = System.getProperty(NIC_KEY);
		hostAddress = System.getProperty(ADDRESS_KEY);
		String temp = System.getProperty(PORT_KEY);

		InetAddress address = null;

		if (hostAddress != null) {
			try {
				address = InetAddress.getByName(hostAddress);
			}
			catch (UnknownHostException ex) {
				LogTools.error(logger, "validateHostAndPort() - Unknown host " + hostAddress);
				return false;
			}
		}
		else if (nicName != null) {
			try {
				NetworkInterface ni = NetworkInterface.getByName(nicName);
				if (ni == null) {
					LogTools.error(logger, "validateHostAndPort() - no nic with name: " + nicName);
					return false;
				}
				Enumeration<InetAddress> addressEnum = ni.getInetAddresses();
				while (addressEnum.hasMoreElements() && hostAddress == null) {
					address = (InetAddress) addressEnum.nextElement();
					
					// for now, only accepting inet4 addresses
					if (address instanceof Inet4Address) {
						hostAddress = address.getHostAddress();
					}
				}
			}
			catch (SocketException ex) {
				LogTools.error(logger, "validateHostAndPort() - problem getting nic " + nicName);
				return false;
			}
		}

		if (address == null) {
			// use default address
			try {
				address = selectLocalAddress();
				hostAddress = address.getHostAddress();
			}
			catch (IOException ex) {
				LogTools.error(logger, "validateHostAndPort() - Unable to determine my local address");
				return false;
			}
		}

		if (address.isLoopbackAddress()) {
			LogTools.error(logger, "validateHostAndPort() - Your hostname is set to localhost. Can't proceed");
			return false;
		}

		// check if address relates to a nic on this computer
		try {
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
			if (networkInterface == null) {
				LogTools.error(logger, "validateHostAndPort() - no network interface with address " + hostAddress);
				return false;
			}
		}
		catch (SocketException ex) {
			LogTools.error(logger, "validateHostAndPort() - unable to determine network interface");
			return false;
		}

		if (temp != null) {
			try {
				hostPort = Integer.parseInt(temp);
			}
			catch (NumberFormatException ex) {
				LogTools.error(logger, "validateHostAndPort() - property '" + PORT_KEY + "' is not an integer");
				return false;
			}
		}
		else {
			hostPort = DEFAULT_PORT;
		}

		LogTools.info(logger, "validateHostAndPort() - using address=[" + hostAddress + "] and port=[" + hostPort + "]");
		return true;
	}

	/**
	 * Select a local address by iterating through the local network
	 * interfaces.
	 * 
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	private static InetAddress selectLocalAddress() throws SocketException, UnknownHostException {
		InetAddress address = null;
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (address == null && networkInterfaces.hasMoreElements()) {
			NetworkInterface ni = networkInterfaces.nextElement();
			Enumeration<InetAddress> addresses = ni.getInetAddresses();
			while (address == null && addresses.hasMoreElements()) {
				InetAddress addr = addresses.nextElement();
				if ( !addr.isLoopbackAddress() && addr instanceof Inet4Address ) {
					address = addr;
				}
			}
		}
		if (address == null) {
			address = InetAddress.getLocalHost();
		}
		return address;
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PropertiesLoader.load();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		JiniStarter jiniStarter = new JiniStarter();

		jiniStarter.commence();

		int jiniStarterPort = new Integer(System.getProperty(JINI_STARTER_PORT)).intValue();
		
		LogTools.info(logger, "Jini Starter port: " + jiniStarterPort);

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(jiniStarterPort);
		}
		catch (IOException e) {
			e.printStackTrace();
			LogTools.info(logger, "Could not start JiniStarter server.  Will not be able to shut it down via ip", e);
			return;
		}

		for (boolean running=true; running; ) {
			
			Socket socket = null;
			try {
				socket = serverSocket.accept();
			}
			catch (IOException e) {
			}
			
			if (socket != null) {
		 		try {
		 			BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
		 			String command = reader.readLine();

		 			if (COMMAND_SHUTDOWN.equalsIgnoreCase(command)) {
		 				// cleanup.
						running = false;
						jiniStarter.conclude();
						socket.close();
						serverSocket.close();
					}
					else {
						// unhandled command
						LogTools.info(logger, "Unhandled command: " + command);
					}
				}
				catch (IOException e) {
				}
				
				try {
					socket.close();
				}
				catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 
	 */
	public static final class ShutdownMessage implements Serializable {
		private static final long serialVersionUID = -8923498724359875342L;
		
		public ShutdownMessage() {
		}
	}
}
