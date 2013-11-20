/*
 * Created on 9/12/2004
 */
package org.openmaji.implementation.server.security.auth;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.openmaji.implementation.server.security.auth.module.SubjectDetails;


/**
 * Authenticate a user.
 * 
 * @author Warren Bloomer
 *
 */
public interface Authenticator extends Remote {

	/**
	 * Authenticate using the username and password.  Details of the user are returned.
	 * 
	 * @param username
	 * @param password
	 */
	SubjectDetails authenticate(String username, char[] password)
		throws RemoteException;
}
