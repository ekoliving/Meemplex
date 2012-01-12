/*
 * 
 */
package org.openmaji.implementation.server.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * TrackingServer
 * 
 * @author stormboy
 */
public class LogSocketServer implements LogListener, Runnable {

	private static final Logger logger = Logger.getAnonymousLogger();

	private static boolean DEBUG = false;

	/**
	 * A set of reader that read log information
	 */
	private HashSet<LogReader> logReaders = new HashSet<LogReader>();
	
	/** 
	 * set of socket writers, one for each connected client. Used to send
	 * messages
	 */
	private HashSet<SocketWriter>      socketWriters = new HashSet<SocketWriter>();

	/**
	 * The socket to accept connections on.
	 */
	private ServerSocket serverSocket;

	/**
	 * Thread used to accept server socket connections
	 */
	private Thread       acceptThread;
	
	private boolean      serverRunning = false;

	/**
	 * 
	 * @param port
	 * @throws IOException
	 */
	public LogSocketServer(int port) throws IOException {
		this.serverSocket          = new ServerSocket(port);
	}

	/**
	 * Listen on a particular address.
	 * 
	 * @param addressString
	 * @param port
	 * @throws IOException
	 */
	public LogSocketServer(String addressString, int port) throws IOException {
		int backlog = -1;  // use the default value
		InetAddress address = InetAddress.getByName(addressString);
		this.serverSocket          = new ServerSocket(port, backlog, address);
	}
	
	/**
	 * 
	 * @param processor
	 */
	public void addLogReader(LogReader logReader) {
		synchronized (logReaders) {
			logReaders.add(logReader);
			if (serverRunning) {
				logReader.addListener(this);
			}
		}
	}
	
	/**
	 * 
	 * @param processor
	 */	
	public void removeLogReader(LogReader logReader) {
		synchronized (logReaders) {
			logReader.removeListener(this);
			logReaders.remove(logReader);
		}
	}
	
	
	
	/* --------------------- Runnable interface ------------------- */
	
	public void run() {
		serverRunning = true;
		while (serverRunning) {
			accept();
		}
		acceptThread = null;
	}

	/* ------------------------ LogListener interface ---------------------- */
	
	public void message(String event) {
		// send to socket handlers
		synchronized (socketWriters) {
			Iterator<SocketWriter> iter = socketWriters.iterator();
			while (iter.hasNext()) {
				SocketWriter handler = iter.next();
				handler.sendMessage(event);
			}
		}
	}


	/* ------------------------ -------------------------- */
	
	/**
	 * 
	 */
	public synchronized void start() {

		synchronized (logReaders) {
			Iterator<LogReader> iter = logReaders.iterator();
			while (iter.hasNext()) {
				LogReader logReader = iter.next();
				logReader.addListener(this);
			}
		}
				
		if (acceptThread == null) {
			acceptThread = new Thread(this);
			acceptThread.start();
		}
		else {
			if (DEBUG) {
				logger.info("acceptThread already running");
			}
		}

	}
	/**
	 * 
	 * @throws IOException
	 */
	public synchronized void stop() throws IOException {
		
		synchronized (logReaders) {
			Iterator<LogReader> iter = logReaders.iterator();
			while (iter.hasNext()) {
				LogReader logReader = iter.next();
				logReader.removeListener(this);
			}
		}

		serverRunning = false;
		if (serverSocket != null) {
			serverSocket.close();
			serverSocket = null;
		}
		
		// stop to socket handlers
		synchronized (socketWriters) {
			Iterator<SocketWriter> iter = socketWriters.iterator();
			while (iter.hasNext()) {
				SocketWriter socketWriter = iter.next();
				socketWriter.stop();
			}
			socketWriters.clear();
		}
	}
	
	/**
	 * Accept a socket connection
	 */
	protected void accept() {
		try {
			if (DEBUG) {
				logger.info("accepting...");
			}
			Socket socket = serverSocket.accept();

			if (DEBUG) {
				logger.info("accepted, creating SocketWriter...");
			}
			
			SocketWriter writer = new SocketWriter(socket);
			
			if (DEBUG) {
				logger.info("adding SocketWriter...");
			}
			
			addSocketWriter(writer);
			writer.start();

			if (DEBUG) {
				logger.info("finished accepting");
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public void removeSocketWriter(SocketWriter handler) {
		synchronized (socketWriters) {
			socketWriters.remove(handler);
		}
	}


	public void addSocketWriter(SocketWriter handler) {	
		synchronized(socketWriters) {
			socketWriters.add(handler);
		}
	}
	
	/**
	 * SocketWriter
	 * Writes messages to an individual socket.
	 */
	public class SocketWriter implements Runnable {
		private Socket socket;
		private Thread runningThread = null;
		private Vector<String> messageQueue = new Vector<String>();
		private Writer streamWriter;

		SocketWriter(Socket socket) 
			throws IOException
		{
			this.socket = socket;
			OutputStream os = socket.getOutputStream();
			
			streamWriter = new OutputStreamWriter(os);
		}
		
		public void run() {
			while (runningThread == Thread.currentThread()) {
				try {
					// read info from queue
					String str = null;
					synchronized (messageQueue) {
						str = messageQueue.remove(0);
					}

					if (DEBUG) {
						logger.info("writing message to socket: " + str);
					}
					
					// write info down socket	
					streamWriter.write(str);
					streamWriter.write('\n');	// write a carriage return
					streamWriter.flush();					
				}
				catch (IndexOutOfBoundsException ex) {
					// no objects in queue
					synchronized (messageQueue) {
						try {
							messageQueue.wait(10000);
						}
						catch (InterruptedException e) {
							runningThread = null;
						}
					}
				}
				catch (IOException ex) {
					if (DEBUG) {
						logger.info("IOException writing to socket. " + ex);
					}
					runningThread = null;
				}
			}
			if (DEBUG) {
				logger.info("Server socket writer is no longer running");
			}
			// remove this handler from the list of handlers
			try {
				socket.close();
			}
			catch (IOException ex) {
			}

			// remove from the set of SocketWriters
			removeSocketWriter(this);
			runningThread = null;
		}

		public void start() {
			synchronized (messageQueue) {
				if (runningThread == null) {
					runningThread = new Thread(this);
					runningThread.start();
				}
			}
		}
		
		public void stop() {
			if (DEBUG) {
				logger.info("stopping SocketReader...");
			}
			synchronized (messageQueue) {
				runningThread = null;
				messageQueue.notify();
			}
		}
		
		public void sendMessage(String message) {
			if (DEBUG) {
				logger.info("adding message to sender queue: " + message);
			}
			// add to local queue
			synchronized(messageQueue) {
				messageQueue.add(message);
				messageQueue.notify();
			}
		}
	}


}
