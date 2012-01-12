package org.openmaji.implementation.server.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Reads log entries from a File or Reader, and sends the log messages to listeners.
 * Log entries are merely Strings that end in '\n'.  The messages are sent without the trailing
 * '\n'.
 * 
 * @author stormboy
 *
 */
public class LogReader {

	private static final Logger logger = Logger.getAnonymousLogger();

	/**
	 * The set of listeners for log messages.
	 */
	private HashSet<LogListener> listeners = new HashSet<LogListener>();

	/**
	 * The Reader object from which to read the original messages.  Messages
	 * are lines of text that terminate in '\n'
	 */
	private BufferedReader reader = null;
	
	/**
	 * An object that reads from the Reader and sends message events.
	 */
	private LineReader lineReader = new LineReader();

	/**
	 * Whether to skip to the end of the file.  Otherwise the entries in the entire file 
	 * are sent.
	 */
	private boolean skipToEof = false;
	
	/**
	 * Whether to ignore empty lines of text.  If false, blank lines will be sent as
	 * messages if they are read.
	 */
	private boolean ignoreBlankLines = false;
	
	/**
	 * 
	 * @param filename
	 */
	public LogReader(String filename) throws FileNotFoundException, IOException  {
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		if (skipToEof) {
			fileReader.skip(Long.MAX_VALUE);		// skip to the end of the file
		}
		this.reader = new BufferedReader(fileReader);
	}

	/**
	 * 
	 * @param reader
	 */
	LogReader(Reader reader) {
		this.reader = new BufferedReader(reader);
	}
	
	public void addListener(LogListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(LogListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Start reading and sending messages.
	 */
	public void start() {
		lineReader.start();
	}

	/**
	 * Stop reading and sending messages.
	 */
	public void stop() {
		lineReader.stop();
	}
	
	private void sendMessage(String message) {
		synchronized (listeners) {
			Iterator<LogListener> listenerIter = listeners.iterator();
			while (listenerIter.hasNext()) {
				LogListener listener = listenerIter.next();
				listener.message(message);
			}
		}
	}
	
	private class LineReader implements Runnable {
		
		private Thread thisThread;
		
		public void start() {
			thisThread = new Thread(this);
			thisThread.start();
		}
		
		public void stop() {
			this.thisThread = null;
		}

		public void run() {
			while (Thread.currentThread() == thisThread) {
				try {
					String message = reader.readLine();
					if (message != null) {
						if (ignoreBlankLines && message.length() == 0 ) {
							// ignore
						}
						else {
							//logger.info("sending message: " + message);
							sendMessage(message);
						}
					}
				}
				catch (IOException e) {
					logger.info("problem while reading log message: " + e);
				}
			}
		}
    }
}
