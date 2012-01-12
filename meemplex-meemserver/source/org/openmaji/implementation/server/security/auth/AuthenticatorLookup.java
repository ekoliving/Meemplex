/*
 * Created on 10/12/2004
 */
package org.openmaji.implementation.server.security.auth;

import org.openmaji.spi.MajiSPI;

/**
 * @author Warren Bloomer
 *
 */
public interface AuthenticatorLookup {

	/**
	 * Returns the authenticator or null if not located.
	 */
	Authenticator getAuthenticator();
	
	public static class spi {
		public static Authenticator getAuthenticator() {
			AuthenticatorLookup lookup = (AuthenticatorLookup) MajiSPI.provider().create(AuthenticatorLookup.class);
			return lookup.getAuthenticator();
		}
		
		public static String getIdentifier() {
			return "authenticatorLookup";
		}
	}
}
