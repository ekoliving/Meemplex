/*
 * Created on 9/12/2004
 */
package org.openmaji.implementation.server.security.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import org.openmaji.implementation.server.security.auth.module.SubjectDetails;
import org.swzoo.log2.core.LogFactory;
import org.swzoo.log2.core.LogTools;
import org.swzoo.log2.core.Logger;


/**
 * @author Warren Bloomer
 *
 */
public class KeyStoreAuth {
	
	private static final Logger logger = LogFactory.getLogger();
	
	private String keystorePassword = null;
	
	private KeyStore keyStore       = null;
	private File     keyStoreFile   = null;
	private long     lastModified   = 0;
	
//	private KeystoreMonitor keystoreMonitor = new KeystoreMonitor();
	
	/**
	 * 
	 * @param filename name of the keystore file
	 * @param password KeyStore password
	 */
	protected KeyStoreAuth(String filename, String password) {
		this.keyStoreFile     = new File(filename);
		this.keystorePassword = password;
	}

	protected synchronized void initialize() {
		loadKeyStoreFile();
	}

	protected synchronized void cleanup() {

		if (keyStore != null) {
//			Provider provider = keyStore.getProvider();
//			if (provider instanceof AuthProvider) {
//				try {
//					((AuthProvider)provider).logout();
//				}
//				catch (LoginException ex) {					
//				}
//			}
			keyStore = null;
		}
		keyStoreFile = null;
		lastModified = 0;
	}
	
	
	protected synchronized SubjectDetails getSubjectDetails(String username, char[] password) {
		
		SubjectDetails subjectDetails = null;
		
		// check if the keystore file has been updated
		if (isKeystoreUpdated()) {
			loadKeyStoreFile();
		}
		
		try {
			// get private key
			Key key = keyStore.getKey(username, password);
			if (key == null || !(key instanceof PrivateKey)) {
				LogTools.warn(logger, "Could not get user details. No key for " + username);
				return subjectDetails;
			}
		    
			Certificate[] certChain = keyStore.getCertificateChain(username);

			// get Group Principals
			// TODO these should be asynchronously sent to the MajiLoginModule so that details can be updated.
			Principal[] groupPrincipals  = MeemCoreRootAuthority.getGroups(username);
			Object[]    groupCredentials = null;
			if (groupPrincipals != null) {
				groupCredentials = new Object[groupPrincipals.length];
				for (int i=0; i<groupCredentials.length; i++) {
					groupCredentials[i] = MeemCoreRootAuthority.getAuthenticator(groupPrincipals[i]);
				}
			}

			subjectDetails = new SubjectDetails(certChain, (PrivateKey)key, username, groupPrincipals, groupCredentials);
		}
		catch (KeyStoreException ex) {
			LogTools.warn(logger, "Could not get user details. " + ex.getMessage());
		}
		catch (NoSuchAlgorithmException ex) {
			LogTools.warn(logger, "Could not get user details. " + ex.getMessage());
		}
		catch (UnrecoverableKeyException ex) {
			LogTools.warn(logger, "Could not get user details. " + ex.getMessage());
		}
		catch (CertificateEncodingException ex) {
			LogTools.warn(logger, "Could not construct user details. " + ex.getMessage());			
		}
	    
		return subjectDetails;
	}
	
	/**
	 * Load the keystore file.
	 *
	 */
	private synchronized void loadKeyStoreFile() {
		
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			
			FileInputStream fis = new FileInputStream(keyStoreFile);
			keyStore.load(fis, keystorePassword.toCharArray());
			fis.close();
			lastModified = keyStoreFile.lastModified();
		}
		catch (KeyStoreException ex) {
			LogTools.warn(logger, "Could not load keystore", ex);
		}
		catch (FileNotFoundException ex) {
			LogTools.warn(logger, "Could not load keystore", ex);
		}
		catch (NoSuchAlgorithmException ex) {
			LogTools.warn(logger, "Could not load keystore", ex);
		}
		catch (IOException ex) {
			LogTools.warn(logger, "Could not load keystore", ex);
		}
		catch (CertificateException ex) {
			LogTools.warn(logger, "Could not load keystore", ex);			
		}
	}
	
	private boolean isKeystoreUpdated() {
		return keyStoreFile.lastModified() > lastModified;
	}
	
}
