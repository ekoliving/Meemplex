/*
 * Created on 8/12/2004
 */
package org.openmaji.implementation.server.security.auth.module;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.openmaji.implementation.server.security.auth.Authenticator;
import org.openmaji.implementation.server.security.auth.AuthenticatorLookupWedge;




/**
 * Uses Maji Authenticator Service
 * 
 * TODO access the Authenticator Service that is located by a Jini lookup
 * 
 * @author Warren Bloomer
 *
 */
public class MajiLoginModule implements LoginModule {

	private Subject         subject;
	private CallbackHandler callbackHandler;
	//private Map             sharedState;
	//private Map             options;

	// subject details
	private X500Principal         principal         = null;		// principal
	private X500PrivateCredential privateCredential = null;		// private credentials
	private CertPath              certPath          = null;		// public credentials
	private Principal[]           groups            = null;
	private Object[]              groupCredentials  = null;

	private int status;

	// status values
    private static final int UNINITIALIZED = 0;
    private static final int INITIALIZED   = 1;
    private static final int AUTHENTICATED = 2;
    private static final int LOGGED_IN     = 3;

	
	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
	public void initialize(
			Subject subject, 
			CallbackHandler callbackHandler, 
			Map sharedState,
			Map options) 
	{
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		//this.sharedState = sharedState;
		//this.options = options;
		
	    this.status = INITIALIZED;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#login()
	 */
	public boolean login() throws LoginException {
		// get Maji Authentication Service
		Authenticator authService = AuthenticatorLookupWedge.getAuthenticator();

		if (authService == null) {
			throw new LoginException("Could not locate the Maji authenticator");
		}

		try {
			boolean echo   = false;
			
			// get username
			NameCallback     nameCallback     = new NameCallback("username");
			callbackHandler.handle( new Callback[] { nameCallback } );			
			String username = nameCallback.getName();

			// get password
			PasswordCallback passwordCallback = new PasswordCallback("password", echo);
			callbackHandler.handle( new Callback[] { passwordCallback } );
			char[] password = passwordCallback.getPassword();
			
			SubjectDetails subjectDetails = authService.authenticate(username, password);

			if (subjectDetails == null) {
				throw new FailedLoginException("Could not authenticate details");
			}
			
			try {
				principal         = subjectDetails.getPrincipal();
				privateCredential = subjectDetails.getPrivateCredentials();
				certPath          = subjectDetails.getPublicCredentials();
				groups            = subjectDetails.getGroups();
				groupCredentials  = subjectDetails.getGroupCredentials();
			}
			catch (CertificateException ex) {
				throw new LoginException("Could not login. " + ex.getMessage());
			}
			catch (InvalidKeySpecException ex) {
				throw new LoginException("Could not login. " + ex.getMessage());
			}
			catch (NoSuchAlgorithmException ex) {
				throw new LoginException("Could not login. " + ex.getMessage());				
			}

		}
		catch (UnsupportedCallbackException ex) {
			throw new LoginException("Could not login. " + ex.getMessage());
		}
		catch (IOException ex) {
			throw new LoginException("Could not login. " + ex.getMessage());			
		}
		
		status = AUTHENTICATED;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException {
		
		switch (status) {
		case UNINITIALIZED:
		    throw new LoginException("The login module is not initialized");
		case INITIALIZED:
		    logoutInternal();
		    throw new LoginException("Authentication failed");
		case AUTHENTICATED:
		    commitInternal();
		    return true;
		case LOGGED_IN:
		    return true;
	    default:
		    throw new LoginException("Unknown problem");
		}		
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	public boolean abort() throws LoginException {
		switch (status) {
			case UNINITIALIZED:
			default:
			    return false;
			case INITIALIZED:
			    return false;
			case AUTHENTICATED:
			    logoutInternal();
			    return true;
			case LOGGED_IN:
			    logoutInternal();
			    return true;
		}
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws LoginException {
		
		switch (status) {
			case UNINITIALIZED:
			    throw new LoginException("The login module is not initialized");
			case INITIALIZED:
			case AUTHENTICATED:
			default:
			   // impossible for LoginModule to be in AUTHENTICATED state
			   // assert status != AUTHENTICATED;
			    return false;
			case LOGGED_IN:
			    logoutInternal();
			    return true;
		}
	}
	
	
	/* ---------------------- internal methods ------------------------ */

	private void commitInternal() throws LoginException {
		if (subject.isReadOnly()) {
		    throw new LoginException ("Subject is set readonly");
		} 
		else {
			subject.getPrincipals().add(principal);
			subject.getPublicCredentials().add(certPath);
			subject.getPrivateCredentials().add(privateCredential);

			// add group principals
			if (groups != null) {
				for (int i=0; i<groups.length; i++) {
					Principal groupPrincipal = groups[i];
					subject.getPrincipals().add(groupPrincipal);				
				}
			}
			if (groupCredentials != null) {
				// add group credentials
				for (int i=0; i<groupCredentials.length; i++) {
					Object cred = groupCredentials[i];
					subject.getPublicCredentials().add(cred);				
				}
			}
			
//			Principal consolePrincipal = new ConsoleRolePrincipal("admin");
//			subject.getPrincipals().add(consolePrincipal);
			
			status = LOGGED_IN;
			
		}
	}
	
	private void logoutInternal() throws LoginException {
		// remove/destroy the Subject's Principals and Credentials
		
		if (subject.isReadOnly()) {
		    // attempt to destroy the private credential
		    // even if the Subject is read-only
		    principal = null;
		    certPath  = null;
		    status    = INITIALIZED;
		    
		    // destroy the private credential
		    Iterator it = subject.getPrivateCredentials().iterator();
		    while (it.hasNext()) {
				Object obj = it.next();
				if (privateCredential.equals(obj)) {
				    privateCredential = null;
				    try {
						((Destroyable)obj).destroy();
						break;
				    } 
				    catch (DestroyFailedException dfe) {
						LoginException le = new LoginException
						    ("Unable to destroy private credential, " + obj.getClass().getName());
						le.initCause(dfe);
						throw le;
				    }
				}
		    }
		    
		    // throw an exception because we can not remove
		    // the principal and public credential from this
		    // read-only Subject
		    throw new LoginException
			(
					"Unable to remove Principal (" 
					+ "X500Principal "
					+ ") and public credential (certificatepath) "
					+ "from read-only Subject"
				);
		}

		if (principal != null) {
		    subject.getPrincipals().remove(principal);
		    principal = null;
		}
		if (groups != null) {
			for (int i = 0; i < groups.length; i++) {
				Principal groupPrincipal = groups[i];
			    subject.getPrincipals().remove(groupPrincipal);				
			} 
			groups = null;
		}
		if (groupCredentials != null) {
			for (int i = 0; i < groupCredentials.length; i++) {
				Object groupCred = groupCredentials[i];
			    subject.getPublicCredentials().remove(groupCred);				
			} 	
			groupCredentials = null;
		}
		if (certPath != null) {
			// MeemCoreRootAuthority turns CertPath into individual certs
            List certificates = certPath.getCertificates();
            for (int i = 0; i != certificates.size(); i++) {
                subject.getPublicCredentials().remove(certificates.get(i));
            }
            
		    subject.getPublicCredentials().remove(certPath);
		    certPath = null;
		}
		if (privateCredential != null) {
		    subject.getPrivateCredentials().remove(privateCredential);
		    privateCredential = null;
		}

		status = INITIALIZED;
	}

}
