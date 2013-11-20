/*
 * @(#)BeanShellSession.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.beanshell;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openmaji.implementation.server.Common;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.toolkit.BufferOverflowException;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.toolkit.Editline;
import org.openmaji.implementation.server.nursery.scripting.telnet.session.SecureSession;
import org.openmaji.implementation.server.nursery.scripting.telnet.util.TelnetSessionLog;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;
import org.openmaji.system.meemserver.MeemServer;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * <p>
 * ...
 * </p>
 * 
 * @author mg
 * @version 1.0
 */
public class BeanShellSession extends SecureSession {
	private static final Logger logger = Logger.getAnonymousLogger();
	
	/**
	 * Default BeanShell directory path (relative)
	 */

	public final static String DEFAULT_BEANSHELL_DIRECTORY = "/scripts/beanshell";

	/**
	 * Property for specifying the BeanShell directory path (relative)
	 */

	public final static String PROPERTY_BEANSHELL_DIRECTORY = "org.openmaji.scripting.bsf.beanshell.directory";

	/**
	 * Default BeanShell initialization script name
	 */

	public final static String DEFAULT_BEANSHELL_SCRIPT = "initialize.bsh";

	/**
	 * Property for specifying the BeanShell initialization script
	 */

	public final static String PROPERTY_BEANSHELL_SCRIPT = "org.openmaji.implementation.scripting.bsf.beanshell.scriptName";

	/**
	 * BeanShell directory path
	 */

	private String beanShellDirectory = DEFAULT_BEANSHELL_DIRECTORY;

	/**
	 * BeanShell initialization script
	 */

	private String beanShellScript = DEFAULT_BEANSHELL_SCRIPT;

	private static Interpreter rootInterpreter = null;

	public BeanShellSession(Socket socket) throws IOException {
			super(socket, "BeanShell Session");

		Properties properties = System.getProperties();

		String propertyBeanShellDirectory = properties.getProperty(PROPERTY_BEANSHELL_DIRECTORY);

		if (propertyBeanShellDirectory != null) {
			beanShellDirectory = propertyBeanShellDirectory;
		}

		String propertyMajitekDirectory = properties.getProperty(Common.PROPERTY_MAJI_HOME);
		propertyMajitekDirectory = propertyMajitekDirectory.replace('\\', '/');

		if (propertyMajitekDirectory != null) {
			beanShellDirectory = propertyMajitekDirectory + beanShellDirectory;
		}

	}
	
	// TODO temporary
//	@Override
//	public void run() {
//		startSession();
//	}

	public void startSession() {
//		PipedInputStream pipedInputStream = new PipedInputStream();
//		PipedOutputStream pipedOutputStream = new PipedOutputStream();
		String meemSpaceName = MeemSpace.getIdentifier();
		String meemServerName = MeemServer.spi.getName();

		if (meemServerName == null) {
			throw new RuntimeException("Empty meemServerName property: " + MeemServer.PROPERTY_MEEMSERVER_NAME);
		}
		
//		try {
//			pipedOutputStream.connect(pipedInputStream);
//		}
//		catch (IOException e) {
//		}

		Interpreter rootInterpreter = getRootInterpreter();
		
//		Interpreter interpreter = new Interpreter(
//				new InputStreamReader(pipedInputStream), 
//				new PrintStream(terminalIO.getOutputStream()), 
//				new PrintStream(terminalIO.getOutputStream()), 
//				true, 
//				new NameSpace(rootInterpreter.getNameSpace(), "BeanShellSession"), 
//				rootInterpreter, 
//				null
//			);

		Interpreter interpreter = new Interpreter(
				new InputStreamReader(consoleReader.getInput()), 
				new PrintStream(new WriterOutputStream(consoleReader.getOutput(), Charset.forName("UTF-8"))), 
				new PrintStream(new WriterOutputStream(consoleReader.getOutput(), Charset.forName("UTF-8"))), 
				true, 
				new NameSpace(rootInterpreter.getNameSpace(), "BeanShellSession"), 
				rootInterpreter, 
				null
			);

//		new Thread(new PipedTerminalStream(terminalIO, pipedOutputStream), "BeanShell Session Input").start();
		
		try {
			interpreter.eval("print(\"MeemSpace [" + meemSpaceName + "]  MeemServer [" + meemServerName + "]  Version " + Common.getIdentification() + "\");");
			interpreter.setExitOnEOF(false);
		}
		catch (EvalError e) {
			error("Error generating BeanShell welcome\n" + e.toString());
		}

		//new Thread(new Interactor(interpreter)).start();
		
		// this will block until the input stream is closed
		interpreter.run();
	}
	
	private Interpreter getRootInterpreter() {

		if (rootInterpreter == null) {
//			terminalIO.write("Initializing. Please wait");
//			terminalIO.flush();
			
			try {
				consoleReader.println("Initializing. Please wait");
				consoleReader.flush();
			}
			catch (IOException e) {
			}
			
			rootInterpreter = new Interpreter();
			try {
				rootInterpreter.set("log", (TelnetSessionLog) this);
				rootInterpreter.eval("cd(\"" + beanShellDirectory + "\");");
				rootInterpreter.eval("source(\"" + beanShellScript + "\");");
				//rootInterpreter.eval("bsh.prompt=\"\\n" + meemServerName + " % \";");
				rootInterpreter.eval("bsh.prompt=\"" + getPrompt() + "\";");
			}
			catch (EvalError e) {
				error("Error while initializing BeanShell session\n" + e.toString());
			}

			// clear the screen and start from zero
//			terminalIO.eraseScreen();
//			terminalIO.homeCursor();
			try {
				consoleReader.clearScreen();
			}
			catch (Exception e) {
			}
		}
		return rootInterpreter;
	}

	private String getPrompt() {
		String prompt = "meemspace";
		String meemSpace = (String) System.getProperty(MeemSpace.PROPERTY_MEEMSPACE_IDENTIFIER);
		if (meemSpace != null) {
			prompt += " [" + meemSpace + "]";
		}
		prompt += "> ";
		
		return prompt;
	}
	
	
	class Interactor implements Runnable {
		Interpreter interpreter;
		
		public Interactor(Interpreter interpreter) {
			this.interpreter = interpreter;
        }
		
		public void run() {
			try {
				
				consoleReader.setPrompt(getPrompt());
				while (true) {
					String line = consoleReader.readLine();
					logger.log(Level.INFO, "got string : " + line);
					try {
						Object result = interpreter.eval(line);
						consoleReader.println("" + result);
					}
					catch (EvalError e) {
						consoleReader.println("Error: " + e.getErrorText());
					}
					consoleReader.flush();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	class PipedTerminalStream implements Runnable {

		BasicTerminalIO terminalIO;

		PipedOutputStream pipedOutputStream;

		public PipedTerminalStream(BasicTerminalIO terminalIO, PipedOutputStream pipedOutputStream) {
			this.terminalIO = terminalIO;
			this.pipedOutputStream = pipedOutputStream;
		}

		public void run() {
			int lastChar = -1;
			while (true) {
				synchronized (this) {
					if (!isAlive)
						break;
				}
				Editline editline = new Editline(terminalIO);
				if (lastChar != -1) {
					try {
						editline.append((char) lastChar);
						terminalIO.flush();
					}
					catch (BufferOverflowException e1) {
						e1.printStackTrace();
					}
				}
				lastChar = editline.run();

				// terminalIO.write("\n");
				terminalIO.flush();

				try {
					pipedOutputStream.write(editline.getValue().getBytes());
				}
				catch (IOException e) {
				}
			}
		}

	}

}
