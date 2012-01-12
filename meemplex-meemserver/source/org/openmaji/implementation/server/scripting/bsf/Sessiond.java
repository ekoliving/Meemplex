/*****************************************************************************
 *                                                                           *
 *  This file is part of the BeanShell Java Scripting distribution.          *
 *  Documentation and updates may be found at http://www.beanshell.org/      *
 *                                                                           *
 *  Sun Public License Notice:                                               *
 *                                                                           *
 *  The contents of this file are subject to the Sun Public License Version  *
 *  1.0 (the "License"); you may not use this file except in compliance with *
 *  the License. A copy of the License is available at http://www.sun.com    * 
 *                                                                           *
 *  The Original Code is BeanShell. The Initial Developer of the Original    *
 *  Code is Pat Niemeyer. Portions created by Pat Niemeyer are Copyright     *
 *  (C) 2000.  All Rights Reserved.                                          *
 *                                                                           *
 *  GNU Public License Notice:                                               *
 *                                                                           *
 *  Alternatively, the contents of this file may be used under the terms of  *
 *  the GNU Lesser General Public License (the "LGPL"), in which case the    *
 *  provisions of LGPL are applicable instead of those above. If you wish to *
 *  allow use of your version of this file only under the  terms of the LGPL *
 *  and not to allow others to use your version of this file under the SPL,  *
 *  indicate your decision by deleting the provisions above and replace      *
 *  them with the notice and other provisions required by the LGPL.  If you  *
 *  do not delete the provisions above, a recipient may use your version of  *
 *  this file under either the SPL or the LGPL.                              *
 *                                                                           *
 *  Patrick Niemeyer (pat@pat.net)                                           *
 *  Author of Learning Java, O'Reilly & Associates                           *
 *  http://www.pat.net/~pat/                                                 *
 *                                                                           *
 *****************************************************************************/

package org.openmaji.implementation.server.scripting.bsf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import bsh.Interpreter;
import bsh.NameSpace;

import org.openmaji.implementation.security.auth.LoginHelper;

/**
 * BeanShell remote session server. Starts instances of bsh for client connections. Note: the sessiond effectively maps all connections to the same interpreter (shared namespace).
 */
public class Sessiond extends Thread {

	private ServerSocket ss;

	NameSpace globalNameSpace;

	/*
	 * public static void main(String argv[]) throws IOException { new Sessiond( Integer.parseInt(argv[0])).start(); }
	 */

	public Sessiond(NameSpace globalNameSpace, int port) throws IOException {
		ss = new ServerSocket(port);
		this.globalNameSpace = globalNameSpace;
	}

	public void run() {
		try {
			while (true) {
				new SessiondConnection(globalNameSpace, ss.accept()).start();
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}

final class SessiondConnection extends Thread {
	NameSpace globalNameSpace;

	Socket client;

	SessiondConnection(NameSpace globalNameSpace, Socket client) {
		this.client = client;
		this.globalNameSpace = globalNameSpace;
	}

	public void run() {

		try {
			InputStream in = client.getInputStream();
			PrintStream out = new PrintStream(client.getOutputStream());

			CallbackHandler handler = new MajiCallbackHandler(out, in);

			Subject subject = null;
			LoginContext loginContext = null;
			for (int count = 0; count < 3 && subject == null; count++) {
				try {
					loginContext = LoginHelper.login(handler);
					subject = loginContext.getSubject();
				}
				catch (FailedLoginException e) {
					out.println("login incorrect");
				}
			}

			if (subject != null) {
				final Interpreter i = new Interpreter(new InputStreamReader(in), out, out, true, globalNameSpace);
				i.setExitOnEOF(false); // don't exit interp

				Subject.doAs(subject, new PrivilegedAction<Void>() {
					public Void run() {
						i.run();
						return null;
					}
				});
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
		catch (LoginException e) {
			System.out.println(e);
		}

		try {
			client.close();
		}
		catch (IOException e) {
			// ignore
		}
	}

	/**
	 * Callback handler for Maji Login.
	 */
	static class MajiCallbackHandler implements CallbackHandler {
		PrintStream out;

		InputStream in;

		public MajiCallbackHandler(PrintStream out, InputStream in) {
			this.out = out;
			this.in = in;
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
					switch (toc.getMessageType()) {
					case TextOutputCallback.INFORMATION:
						System.out.println("INFORMATION: " + toc.getMessage());
						break;
					case TextOutputCallback.ERROR:
						System.out.println("ERROR: " + toc.getMessage());
						break;
					case TextOutputCallback.WARNING:
						System.out.println("WARNING: " + toc.getMessage());
						break;
					default:
						throw new UnsupportedCallbackException(callback, "Unsupported message type: " + toc.getMessageType());
					}
				}

				// the NameCallback is used by the
				else if (callback instanceof NameCallback) {
					NameCallback nc = (NameCallback) callbacks[i];
					out.print("User: ");
					try {
						nc.setName(getLine());
					}
					catch (IOException e) {
						throw new RuntimeException("error on user read");
					}
				}

				// PasswordCallback is used for requesting passwords
				else if (callback instanceof PasswordCallback) {
					PasswordCallback pc = (PasswordCallback) callbacks[i];
					String prompt = pc.getPrompt();
					if (prompt.startsWith("password")) {
						out.print("Password: ");
						try {
							pc.setPassword(getLine().toCharArray());
						}
						catch (IOException e) {
							throw new RuntimeException("error on user read");
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

		private String getLine() throws IOException {
			int c;
			ByteArrayOutputStream bOut = new ByteArrayOutputStream();

			while ((c = in.read()) != '\n' && c > 0) {
				bOut.write(c);
			}

			byte[] bytes = bOut.toByteArray();

			if (bytes.length >= 1 && bytes[bytes.length - 1] == '\r') {
				return new String(bytes, 0, bytes.length - 1);
			}

			return new String(bytes);
		}
	}

}
