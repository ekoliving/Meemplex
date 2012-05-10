/*
 * @(#)JiniStarter.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Enumeration;

import net.jini.admin.Administrable;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;

import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.utility.PropertiesLoader;
import com.sun.jini.admin.DestroyAdmin;
import com.sun.jini.start.NonActivatableServiceDescriptor;
import com.sun.jini.start.ServiceDescriptor;
import com.sun.jini.tool.ClassServer;

public class JiniStarter2 {
	private static Logger logger = LogFactory.getLogger();

	/** the property name for the NIC to bind to */
	public static final String PROPERTY_JINI_NIC = "org.openmaji.jini.nic";

	/** the property name for the specific address to bind to */
	public static final String PROPERTY_JINI_ADDRESS = "org.openmaji.jini.address";

	/** the property name for the port to bind the class-server to */
	public static final String PROPERTY_JINI_PORT = "org.openmaji.jini.port";

	/** the default port for the class-server */
	private static final int DEFAULT_PORT = 8081;

	private static final String PROPERTY_JINI_CONFIG = "org.openmaji.jini.configuration";
	
	private static final String PROPERTY_JINI_JARS = "org.openmaji.jini.jars";
	
	private static final String PROPERTY_HTTP_URL = "http.url";
	
	private static final String PROPERTY_REGGIE_POLICY = "reggie.policy";
	
	/** classpath for the jini starter process */
	public static final String JINI_STARTER_CLASSPATH = "org.openmaji.implementation.server.nursery.jini.JiniStarterClassPath";	

	public static final String COMMAND_SHUTDOWN = "shutdown";
	
	// paths
	
	private static final String JINI_LIB_DIR = "/lib/jini";
	
	private static final String JINI_CONFIG_DIR = "/conf/jini"; 
	
	private static final String POLICY_FILE = "/conf/security/all.policy";
	
	private static final String START_REGGIE_CONFIG = "/start-reggie.config";

	
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
	public void commence() {
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

			LogTools.info(logger, policyFile + " : " + jiniConfigDir + " : " + jiniJarDir);

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
		
		LogTools.info(logger, "Got protection domain: " + JiniStarter2.class.getProtectionDomain());

	}

	
	public void conclude() {
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

		// Try OpenMaji layout
		String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
		jiniJarDir = majitekDirectory + JINI_LIB_DIR;
		policyFile = majitekDirectory + POLICY_FILE;
		jiniConfigDir = majitekDirectory + JINI_CONFIG_DIR;
		jiniConfigFile = jiniConfigDir + START_REGGIE_CONFIG;

		if (new File(jiniConfigFile).exists() == false) {
			LogTools.info(logger, "validateEnvironment() - Unable to locate reggie config at: " + jiniConfigFile);
			return false;
		}
		if (new File(jiniJarDir).exists() == false) {
			LogTools.info(logger, "validateEnvironment() - Unable to locate jini jars at: " + jiniJarDir);
			return false;
		}
		if (new File(policyFile).exists() == false) {
			LogTools.info(logger, "validateEnvironment() - Unable to locate policy file at: " + policyFile);
			return false;
		}

		return true;
	}

	public boolean validateHostAndPort() {
		String nicName = System.getProperty(PROPERTY_JINI_NIC);
		hostAddress = System.getProperty(PROPERTY_JINI_ADDRESS);
		String temp = System.getProperty(PROPERTY_JINI_PORT);

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
				if (addressEnum.hasMoreElements()) {
					address = (InetAddress) addressEnum.nextElement();
					hostAddress = address.getHostAddress();
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
				LogTools.error(logger, "validateHostAndPort() - property '" + PROPERTY_JINI_PORT + "' is not an integer");
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

		JiniStarter2 jiniStarter = new JiniStarter2();

		jiniStarter.commence();
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
