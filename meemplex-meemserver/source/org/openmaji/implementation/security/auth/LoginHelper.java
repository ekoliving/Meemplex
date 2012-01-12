package org.openmaji.implementation.security.auth;

import java.security.PrivilegedAction;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * A helper Class for authenticating Subjects in Maji.
 * 
 * @author Warren Bloomer
 */
public class LoginHelper
{
	
	public  static final String APPNAME_MAJI        = "MajiLogin";
	public  static final String APPNAME_CONSOLE     = "Console";
	public  static final String APPNAME_CLOUDSYSTEM = "CloudSystem";

//	private static final String AUTH_MODULE_MAJI    = "org.openmaji.implementation.server.security.auth.module.MajiLoginModule";
//	private static final String AUTH_MODULE_CLOUD   = "org.openmaji.implementation.server.security.auth.module.CloudSystemLoginModule";
//	private static final String AUTH_MODULE_CONSOLE = "org.openmaji.implementation.edge.console.security.ConsoleLoginModule";

	/**
	 * Return an authenticated Subject corresponding to the username and 
	 * password supplied.
	 * 
	 * @param username
	 * @param password
	 * @throws LoginException
	 */
	public static LoginContext login(String username, String password)  throws LoginException {
		
		CallbackHandler handler = new MajiCallbackHandler(username, password);
		return login(handler);
	}
	
	/**
	 * Return a Subject authenticated with the details provided via
	 * the supplied CallBackHandler.
	 * 
	 * @param callbackHandler
	 */
	public static LoginContext login(CallbackHandler callbackHandler) throws LoginException {
		
		return login(APPNAME_MAJI, callbackHandler);
	}

	/**
	 * 
	 * @param appname the name of the application to log in to
	 * @param callbackHandler
	 * @throws LoginException
	 */
	public static LoginContext login(String appname, CallbackHandler callbackHandler)
		throws LoginException
	{
		return login(appname, callbackHandler, null);
	}
	
	public static LoginContext login(String appname, CallbackHandler callbackHandler, Subject subject) 
		throws LoginException
	{

		// set our default login configuration to use the keystore login we want

		LoginContext loginContext = null;
		
		if (subject == null) {
			loginContext = new LoginContext(appname, callbackHandler);
		}
		else {
			loginContext = new LoginContext(appname, subject, callbackHandler);
		}

		// TODO use the following JDK 1.5 constructor
		//LoginContext loginContext = new LoginContext("MajiLogin", null, callbackHandler, new LoginConfiguration("default"));

		loginContext.login();

		return loginContext;
	}
	
	/**
	 * Execute the runnable as the user identified by username and authenticated
	 * using the supplied password.
	 * 
	 * @param runnable
	 * @param username
	 * @param password
	 * @throws LoginException
	 */
	public static void doAs(Runnable runnable, String username, String password) 
		throws LoginException
	{
		doAs(runnable, new MajiCallbackHandler(username, password));
	}
		
	/**
	 * Execute a Runnable as an authenticated Subect of Maji.
	 * The callback handler is used to provide credentials as required.
	 * 
	 * @param runnable
	 * @param callbackHandler
	 * @throws LoginException
	 */
	public static void doAs(final Runnable runnable, CallbackHandler callbackHandler) 
		throws LoginException
	{
		doAs(runnable, APPNAME_MAJI, callbackHandler);
	}

	/**
	 * Execute a runnable in the context of a newly authenticated subject.
	 * 
	 * @param runnable The action to run.
	 * @param appname The name of the application to log into
	 * @param callbackHandler To prompt for authorisation details when needed
	 * @throws LoginException
	 */
	public static void doAs(final Runnable runnable, String appname, CallbackHandler callbackHandler) 
		throws LoginException
	{
		LoginContext context = login(appname, callbackHandler);
		Subject subject = context.getSubject();
	
	    PrivilegedAction action = new PrivilegedAction() {
			public Object run() {
				runnable.run();
				return null;
			}
		};
	
	    Subject.doAsPrivileged(subject, action, null);

		// TODO Currently Maji depends on the Subject that activates Meems to remain logged in.
//	    context.logout();
	}

	/**
	 * Execute a runnable in the context of a newly authenticated subject combined
	 * with the principals and credentials of an existing Subject.
	 * 
	 * A copy of the original Subject is used, so that modifcations to the Subject
	 * caused by authentication do not affect other threads using this Subject. 
	 *
	 * @param runnable
	 * @param appname
	 * @param callbackHandler
	 * @param subject
	 * @throws LoginException
	 */
	public static void doAs(final Runnable runnable, String appname, CallbackHandler callbackHandler, Subject subject) 
		throws LoginException
	{
		LoginContext context = login(appname, callbackHandler, subject);
		
		// get the subject of the current context
		subject = context.getSubject();
		
		// create a copy of the subject
		subject = copySubject(subject);
	
	    PrivilegedAction action = new PrivilegedAction() {
			public Object run() {
				runnable.run();
				return null;
			}
		};
	
	    Subject.doAsPrivileged(subject, action, null);

	    context.logout();
	}
	
	/**
	 * Create a copy of a Subject.
	 * 
	 * To call this method, the caller must have AuthPermission("modifyPrincipals"),
	 * AuthPermission("modifyPublicCredentials") and AuthPermission("modifyPrivateCredentials").
	 * 
	 * @param subject The original Subject to copy
	 * @return a copy of the Subject
	 */
	private static Subject copySubject(Subject subject) {
		final boolean readOnly = false;
		Subject newSubject = new Subject(
				readOnly,
				subject.getPrincipals(),
				subject.getPublicCredentials(),
				subject.getPrivateCredentials()
			);
		return newSubject;
	}

	/**
	 * A username/password callback handler for Maji Login.
	 */
	static class MajiCallbackHandler implements CallbackHandler
	{
	  private final String username;
	  private final String password;

	  public MajiCallbackHandler(String username, String password) {
	    this.username = username;
	    this.password = password;
	  }

	  /**
	   * Handle callbacks presented by the LoginModule.
	   */
	  public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
	    for (int i = 0; i < callbacks.length; i++)
	    {
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
	          nc.setName(username);
	      }

	      // PasswordCallback is used for requesting passwords
	      else if (callback instanceof PasswordCallback)  {
	          PasswordCallback pc = (PasswordCallback) callbacks[i];
	          String prompt = pc.getPrompt();
	          if (prompt.startsWith("password")) {
	            pc.setPassword(password.toCharArray());
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