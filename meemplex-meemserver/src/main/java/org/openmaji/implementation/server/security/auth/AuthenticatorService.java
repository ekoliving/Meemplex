/*
 * Created on 9/12/2004
 */
package org.openmaji.implementation.server.security.auth;

import org.openmaji.implementation.server.security.auth.module.SubjectDetails;


/**
 * @author Warren Bloomer
 *
 */
public class AuthenticatorService implements Authenticator {

	KeyStoreAuth keyStoreAuth = null;
	
	public AuthenticatorService(String keyStoreFilename, String keyStorPassword) {
		keyStoreAuth = new KeyStoreAuth(keyStoreFilename, keyStorPassword);
		keyStoreAuth.initialize();
	}

	public SubjectDetails authenticate(String username, char[] password) {
		SubjectDetails details = null;
		if (keyStoreAuth != null) {
			details = keyStoreAuth.getSubjectDetails(username, password);
		}
		return details;
	}
	
	public void cleanup() {
		if (keyStoreAuth != null) {
			keyStoreAuth.cleanup();
			keyStoreAuth = null;
		}
	}
}
