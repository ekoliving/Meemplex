/*
 * @(#)SecureSession.java
 *
 * Copyright 2003 by Majitek Limited.  All Rights Reserved.
 *
 * This software is the proprietary information of Majitek Limited.
 * Use is subject to license terms.
 */

/* ToDo:
 * - None, yet.
 */
package org.openmaji.implementation.server.nursery.scripting.telnet.session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.*;

import jline.console.ConsoleReader;

import org.openmaji.implementation.server.nursery.scripting.telnet.io.BasicTerminalIO;
import org.openmaji.implementation.server.nursery.scripting.telnet.io.toolkit.Editfield;
import org.openmaji.implementation.server.space.meemspace.MeemSpace;


/**
 * <p>
 * ...
 * </p>
 *
 * @author  mg
 * @author Warren Bloomer
 * @version 1.0
 */
public abstract class SecureSession extends AbstractSession {
	private static final String AUTH_MODULE = "org.openmaji.implementation.server.security.auth.module.MajiLoginModule";

	public SecureSession(Socket socket, String name) throws IOException {
		super(socket, name);
	}

	public abstract void startSession();

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// set our default login configuration to use the keystore login we want
		Configuration conf = null;
		try {
			conf = Configuration.getConfiguration();
		}
		catch (SecurityException e) {
			// no config file is set safe to ignore
		}

		LoginContext loginContext = null;
		Subject      subject      = null;
		    
		Configuration.setConfiguration(new LoginConfiguration(MeemSpace.getIdentifier()));
		try {
			CallbackHandler handler = new MajiCallbackHandler(consoleReader);
			loginContext = new LoginContext("MajiLogin", handler);

			for (int count=0; subject == null && count < 3; count++) {
				try {
					loginContext.login();
					subject = loginContext.getSubject();
				}
				catch (FailedLoginException e) {
					try {
						consoleReader.println("Login incorrect");
						consoleReader.flush();
					}
					catch (IOException ioe) {
					}
					//terminalIO.write("Login incorrect\n");
				}
			}
		}
		catch (LoginException e1) {
			e1.printStackTrace();
		}
		finally {
			if (conf != null) {
				Configuration.setConfiguration(conf);
			}
		}

		if (subject == null) {
			try {
				getSocket().close();
			}
			catch (IOException e) {
			}
		}
		else {
			//clear the screen and start from zero
			//terminalIO.eraseScreen();
			//terminalIO.homeCursor();
			try {
				consoleReader.clearScreen();
			}
			catch (IOException e) {
			}
			
			Subject.doAs(
					subject, 
					new PrivilegedAction<Object>() {
						public Object run() {
							startSession();
							return null;
						}
					}
			);
			
			try {
				loginContext.logout();
			}
			catch (LoginException ex) {
			}
		}
	}

	/**
	 * Configuration for Login.  States that authentication against the MajiLoginModule is
	 * required.
	 * 
	 * The meemSpaceName may be used inthe furute for login in to different MeemSpaces.
	 */
	static class LoginConfiguration extends Configuration 
	{
	  AppConfigurationEntry entry = null;
	  AppConfigurationEntry consoleEntry = null;

	  public LoginConfiguration(String meemSpaceName) {
	    Map<String, String> options = new HashMap<String, String>();
	    options.put("meemSpace", meemSpaceName);

	    entry = new AppConfigurationEntry(AUTH_MODULE, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);

	    consoleEntry = new AppConfigurationEntry(
		  		"com.majitek.edge.console.security.ConsoleLoginModule",
		  		AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
				options
			);
	  }

	  public AppConfigurationEntry[] getAppConfigurationEntry(String applicationName) {
	  	if ("Console".equalsIgnoreCase(applicationName)) {
	  		return new AppConfigurationEntry[] { consoleEntry };
	  	}
	  	else {
	  		return new AppConfigurationEntry[] { entry };
	  	}
	  }

	  public void refresh() {
	    // nothing to do here...
	  }
	}

	/**
	 * Callback handler for Maji Login.
	 */
	static class MajiCallbackHandler implements CallbackHandler {
		BufferedReader reader;

		ConsoleReader consoleReader;

		public MajiCallbackHandler(ConsoleReader consoleReader) {
			this.consoleReader = consoleReader;
			reader = new BufferedReader( new InputStreamReader(consoleReader.getInput()) );
		}

		/**
		 * Handle callbacks presented by the LoginModule.
		 */
		public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; i++) {
			  Callback callback = callbacks[i];
			
			  // a TextOutputCallback is simply used to provide the handler with
			  // messages that may need to be displayed to the user.
			  if (callback instanceof TextOutputCallback) {
			    TextOutputCallback toc = (TextOutputCallback) callbacks[i];
			    switch (toc.getMessageType())
			    {
			      case TextOutputCallback.INFORMATION :
			        System.out.println("INFORMATION: " + toc.getMessage());
			      	break;
				  case TextOutputCallback.ERROR :
				    System.out.println("ERROR: " + toc.getMessage());
				    break;
				  case TextOutputCallback.WARNING :
				    System.out.println("WARNING: " + toc.getMessage());
				    break;
				  default :
				    throw new UnsupportedCallbackException(callback, "Unsupported message type: " + toc.getMessageType());
				    }
			  }
			
			  // the NameCallback is used by the
			  else if (callback instanceof NameCallback) {
					NameCallback nc = (NameCallback) callbacks[i];
					try {
						consoleReader.print("Username: ");
						consoleReader.flush();
						String u = reader.readLine();
						nc.setName(u);
					}
					catch (IOException e) {
					}
			  }
			
			  // PasswordCallback is used for requesting passwords
			  else if (callback instanceof PasswordCallback)  {
				PasswordCallback pc = (PasswordCallback) callbacks[i];
				String prompt = pc.getPrompt();
				if (prompt.startsWith("password")) {
					try {
						consoleReader.print("Password: ");
						consoleReader.flush();
						String p = reader.readLine();
						pc.setPassword(p.toCharArray());
					}
					catch (Exception e) {
					}
				}
				else {
					throw new UnsupportedCallbackException(callback, "Unrecognized password prompt: " + prompt);
				}
			  }
			
			  // at the last stage of the login we get back a confirmation message (for information only)
			  else if (callback instanceof ConfirmationCallback) {
			      // ignore
			  }
			  else {
			      throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
			  }
			}
		}
	}


}
