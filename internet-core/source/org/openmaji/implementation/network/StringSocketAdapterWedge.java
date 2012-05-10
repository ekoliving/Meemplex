/*
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

package org.openmaji.implementation.network;

//  TODO: Use the Maji framework to get a new thread

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.common.StringValue;
import org.openmaji.common.Value;
import org.openmaji.common.Variable;
import org.openmaji.meem.MeemContext;
import org.openmaji.meem.Wedge;
import org.openmaji.meem.wedge.configuration.ConfigurationClient;
import org.openmaji.meem.wedge.configuration.ConfigurationClientAdapter;
import org.openmaji.meem.wedge.configuration.ConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.IntegerConfigurationSpecification;
import org.openmaji.meem.wedge.configuration.StringConfigurationSpecification;
import org.openmaji.meem.wedge.lifecycle.*;
import org.openmaji.system.manager.thread.ThreadManager;

/**
 * <p>
 * The StringSocketAdapterWedge Wedge wraps a Socket as a StringVariable.
 * StringSocketAdapterWedge is a variableControlConduit target that listens
 * for Variable method invocations and writes the string values on the socket.
 * Any strings read from the socket are provided on the
 * variableStateConduit source as StringValues.
 * </p>
 * <p>
 * This implementation assumes that strings read from the socket are terminated
 * with a newline character '\n'. The newline character is stripped from the
 * data read from the socket. Strings are written to the socket
 * <u>as is</u> so if you want newlines then you must supply them.
 * </p>
 * <p>
 * This implementation assumes that the maximum string length that can
 * be read from the socket is less than 1024 bytes.
 * </p>
 * <p>
 * When you invoke this Wedge's SocketConfiguration Facet it will close down
 * an existing socket and attempt to open a socket to the newly specified
 * host:port. If a socket was not open at the time the SocketConfiguration
 * call was made then this implementation will not start a new thread until
 * its lifecycle state is set to READY.
 * </p>
 * <p>
 * If a socket can't be opened to the specified host:port because of a
 * configuration error (resulting in a HostUnknownException) then this
 * implementation will not become READY and instead remain in a PENDING state.
 * </p>
 * <p>
 * If however a socket can't be opened because of a transient fault
 * (such as a service being unavailable) then this implementation will keep
 * trying to reconnect every 5 seconds, and any messages received in the
 * meantime via the variableControlConduit will be bufferred until the socket
 * connection is established.
 * </p>
 * <p>
 * Note: Implementation thread safe = not known
 * </p>
 * @author  Christos Kakris
 */

public class StringSocketAdapterWedge implements Wedge
{
	private static Logger logger = Logger.getAnonymousLogger();

	public MeemContext meemContext;

	
	/* ----------------- conduits ------------------ */

	public Vote lifeCycleControlConduit = null;

	public Variable variableControlConduit = new VariableControlConduit();

	public Variable variableStateConduit = null;

	public ConfigurationClient configurationClientConduit = new ConfigurationClientAdapter(this);

	public LifeCycleClient lifeCycleClientConduit = new LifeCycleClientAdapter(this);

	
	/* ------------ persisted fields ---------------- */

	public String host = null;

	public int port = -1;

	public String friendlyName = "noname";

	public int lineTerminator = 1;

	public char lineTerminatorChar = '\n';

	
	/* -------------- private members --------------- */

	/**
	 * A connection monitor.  This will maintain a SocketChannel and its connection.
	 */
	private ConnectionMonitor connectionMonitor = new ConnectionMonitor();

	/**
	 * Address for the socket to connect to.
	 */
	private InetSocketAddress inetSocketAddress = null;

	/**
	 * A queue of outboind messages to send down the socket.
	 */
	private volatile Vector<String> outgoingMessages = new Vector<String>();

	/**
	 * A byte buffer to use for reading bytes from the socket.
	 */
	private ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);

	/**
	 * buffer to place characters read from the socket channel cleared when a
	 * newline character is reached.
	 */
	private StringBuffer readBuffer;


	/* ------------- configuration specifications ----------------------- */

	public transient ConfigurationSpecification hostSpecification = new StringConfigurationSpecification("Hostname");

	public transient ConfigurationSpecification portSpecification = new IntegerConfigurationSpecification("Port", 1, 65536);

	public transient ConfigurationSpecification lineTerminatorSpecification = new ConfigurationSpecification("Line terminator 2=NL 1=CR 0=none", Integer.class, LifeCycleState.READY);

	/**
	 * Sets the hostname to connect to.
	 * 
	 * @param host The name of the host to connect to.
	 */
	public void setHost(String host) {
		this.host = host;

		friendlyName = host + ":" + port;
	}

	/**
	 * Sets the port to connect to.
	 * 
	 * @param port the port number.
	 */
	public void setPort(Integer port) {
		this.port = port.intValue();
		
		friendlyName = host + ":" + this.port;
	}

	/**
	 * Sets the line terminator character.
	 * 
	 * @param value an integer value of the line terminator.
	 */
	public void setLineTerminator(Integer value) {
		switch (value.intValue()) {
		case 0:
			lineTerminatorChar = 0;
			break;
		case 1:
			lineTerminatorChar = '\n';
			break;
		default:
			lineTerminatorChar = '\r';
		}
		this.lineTerminator = value.intValue();
	}
  
  
	/* ------- Meem functionality ------------------------------------- */
	
	/**
	 * 
	 */
	public void commence() {
		
		if (DebugFlag.TRACE) {
			logger.fine("commence() - myName=[" + friendlyName + "]");
		}

		if (host == null || port == -1) {
			logger.info("not configured yet, can not go READY");
			vote(false);
			return;
		}

		try {
			inetSocketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
		}
		catch (UnknownHostException ex) {
			logger.info("startThreadRunning() - myName=[" + friendlyName + "]: configured with unknown host");
			vote(false);
			return;
		}
		
		 readBuffer = new StringBuffer();
				
		connectionMonitor.start();
	}

	/**
	 * 
	 */
	public void conclude() {
		if (DebugFlag.TRACE) {
			logger.fine("conclude() - myName=[" + friendlyName + "]");
		}
		
		connectionMonitor.stop();
		inetSocketAddress = null;
	}
  
	
	/* -------------- private methods ------------ */
	
	/**
	 * Votes whether the Meem is READY on behalf of this wedge.  Runs in a Maji thread.
	 */
	private void vote(final boolean ok) {
		ThreadManager.spi.create().queue(
				new Runnable() {
					public void run() {
						lifeCycleControlConduit.vote(meemContext.getWedgeIdentifier(), ok);						
					};
				}
		);
	}
	

	/**
	 * 
	 * @param selector
	 * @throws IOException
	 */
	private void handleReadyKeys(Selector selector) throws IOException {
		Set<SelectionKey> readyKeys = selector.selectedKeys();
		for (Iterator<SelectionKey> i = readyKeys.iterator(); i.hasNext();) {
			SelectionKey key = i.next();
			i.remove();
			if (key.isReadable()) {
				readDataFromChannel(key);
			}
		}
	}

	/**
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void readDataFromChannel(SelectionKey key) throws IOException {

		SocketChannel incomingChannel = (SocketChannel) key.channel();
		directBuffer.clear();

		int bytes = incomingChannel.read(directBuffer);
		if (bytes == -1) {
			if (DebugFlag.TRACE) {
				logger.fine("readDataFromChannel() - myName=[" + friendlyName + "]: end of data, closing channel");
			}
			incomingChannel.close();
			readBuffer = new StringBuffer();
			return;
		}

		if (bytes > 0) {
			directBuffer.flip();
			while (directBuffer.hasRemaining()) {
				byte oneByte = directBuffer.get();
				if (oneByte == lineTerminatorChar) {
					sendReaderBuffer();
				}
				else {
					readBuffer.append((char) oneByte);
				}
			}
			if (lineTerminatorChar == 0) {
				// null line termination character, send value 
				sendReaderBuffer();
			}
		}
	}

	private void sendReaderBuffer() {
		// end of line
		if (readBuffer.length() > 0) {
			// send the value
			StringValue value = new StringValue(readBuffer.toString());
			variableStateConduit.valueChanged(value);

			// new read buffer
			readBuffer = new StringBuffer();
		}
	}
	
	/**
	 * Writes all queued outgoing messages to the socket channel.
	 * 
	 * @param ocketChannel The SocketChannel to write to.
	 * @throws IOException
	 */
	private void writeDataToChannel(SocketChannel socketChannel) throws IOException {
		synchronized (outgoingMessages) {
			while ( outgoingMessages.size() > 0 && socketChannel.isOpen() ) {	
				String request = (String) outgoingMessages.get(0);
				if (request != null) {
					ByteBuffer byteBuffer = ByteBuffer.wrap(request.getBytes());
					// int bytesWritten =
					socketChannel.write(byteBuffer);
				}
				// writing was successful, remove the message from the queue
				outgoingMessages.remove(0);
			}
		}
	}

  
	/* ---------- Inner classes ----------------------------------------- */

	/**
	 * VariableControlConduit.
	 * Used to receive Values from the Variable conduit.
	 */
	private class VariableControlConduit implements Variable {
		public void valueChanged(Value value) {
			synchronized (outgoingMessages) {
				// add the value to the list of outgoing requests
				outgoingMessages.add(value.toString());
			}
		}
	}

	/**
	 * Creates and monitors a SocketChannel connection.
	 * Also reads data from and writes data to the SocketChannel. 
	 */
	private class ConnectionMonitor implements Runnable {
		private SocketChannel socketChannel = null;
		private Selector selector = null;
		private Thread thread = null;
		private long timeout = 5000;
		
		public void run() {
			// only run in the appropriate thread
			if (Thread.currentThread() != thread) {
				return;
			}

			// loop until stopped
			while (Thread.currentThread() == thread) {
				try {
					readAndWrite();
				}
				catch (InterruptedException e) {
					break;
				}
				catch (Exception e) {
					// problem with the socket
					
					if (DebugFlag.TRACE) {
						logger.log(Level.FINE, "Problem: " + e.getMessage(), e);
					}
					//LogTools.info(logger, "Problem: " + e.getMessage(), e);
					
					vote(false);
					synchronized (this) {
						try {
							this.wait(timeout);
						}
						catch (InterruptedException ie) {
							break;
						}
					}
				}
			}

			thread = null;
			
			// strip down the socket resources
			cleanup();
		}
		
		public synchronized void start() {
			if (thread == null) {
				thread = new Thread(this);
				thread.start();
			}
		}
		
		public synchronized void stop() {
			thread = null;
			notify();
		}
		
		public synchronized boolean isRunning() {
			return (thread != null);
		}
		
		public SocketChannel getSocketChannel() {
			return socketChannel;
		}
		
		private void readAndWrite() throws IOException, InterruptedException {

			// make sure we have a SocketChannel
			if (socketChannel == null || !socketChannel.isOpen() ) {
				createSocket();
			}

			// make sure we have an open connection
			waitForConnection();

			// read from the SocketChannel
			int keysReady = selector.select(100);
			if (keysReady > 0) {
				handleReadyKeys(selector);
			}

			// write messages to the socket channel
			writeDataToChannel(socketChannel);
		}

		private void createSocket() throws IOException {
			
			// first cleaup any existing SocketChannel stuff;
			cleanup();
			
			selector = Selector.open();
			
			// open the socket channel
			socketChannel = SocketChannel.open();
			
			// set to non-blocking
			socketChannel.configureBlocking(false);
			
			// register the selector
			socketChannel.register(selector, SelectionKey.OP_READ);
		}

		private void cleanup() {
			if (socketChannel != null) {
				try {
					socketChannel.close();
				}
				catch (Exception e) {
				}
				socketChannel = null;
			}
			if (selector != null) {
				try {
					selector.close();
				}
				catch (Exception e) {
				}
				selector = null;
			}
		}

		/**
		 * 
		 * @param socketChannel
		 * @throws IOException
		 */
		private void waitForConnection() throws IOException, InterruptedException {
			if (inetSocketAddress == null) {
				// cannot connect because no address to connect to
				return;
			}
			if (!socketChannel.isConnected()) {
				while (Thread.currentThread() == thread && !socketChannel.isConnected()) {
					if (socketChannel.isConnectionPending()) {
						synchronized (this) {
							boolean connected = socketChannel.finishConnect();
							while (!connected) {
								this.wait(1000L);
								connected = socketChannel.finishConnect();
							}
						}
					}
					else {
						socketChannel.connect(inetSocketAddress);
					}
				}
	
				if (socketChannel.isConnected()) {
					// will reach this part if creation is successful
					vote(true);
				}
			}
		}
	}
  

}

