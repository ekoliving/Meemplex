/*
 * @(#)MeemCoreRootAuthority.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.security.auth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.AccessControlException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.openmaji.implementation.server.Common;
import org.openmaji.system.meem.hook.security.AccessLevel;
import org.openmaji.system.meem.hook.security.GroupPrincipal;
import org.openmaji.system.meem.hook.security.Principals;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * manage subject information for meems.
 * 
 * <b>Note</b>: This class is anything but thread safe!!!!!
 * 
 */
public class MeemCoreRootAuthority {
	/**
	 * Create the per-class Software Zoo Logging V2 reference.
	 */
	private static final Logger logger = Logger.getAnonymousLogger();
	private static final String SECURITY_MANAGER_PROPERTIES = "org.openmaji.security";

	public static final String KEYSTORE_NAME = SECURITY_MANAGER_PROPERTIES + ".KeyStore";
	public static final String KEYSTORE_PASSWD = SECURITY_MANAGER_PROPERTIES + ".KeyStorePasswd";
	static final String USER_KEYSTORE_NAME = SECURITY_MANAGER_PROPERTIES + ".UserKeyStore";
	static final String USER_KEYSTORE_PASSWD = SECURITY_MANAGER_PROPERTIES + ".UserKeyStorePasswd";
	static final String USER_PASSWORD_FILE = SECURITY_MANAGER_PROPERTIES + ".UserPasswordFile";
	static final String GROUP_FILE = SECURITY_MANAGER_PROPERTIES + ".GroupFile";

	static final String KEY_ID = "server";

	private static MeemCoreAuth instance;
	private static boolean firstTime = false;

	static {
		instance = new MeemCoreAuth();
	}

	/**
	 * Private constructor to stop anyone from creating an instance of this
	 * class.
	 */
	private MeemCoreRootAuthority() {
	}

	/**
	 * Returns the GroupFile that maintains group membership.
	 */
	public static GroupFile getUserGroupFile() {
		return instance.getUserGroupFile();
	}

	/**
	 * Returns the user password file.
	 */
	public static PasswordFile getUserPasswordFile() {
		return instance.getUserPasswordFile();
	}

	/**
	 * Returns the GroupFile that maintains group membership.
	 * 
	 * TODO what is this really for?
	 * 
	 */
	public static GroupFile getCompositeGroupFile() {
		return instance.getCompositeGroupFile();
	}

	/**
	 * Returns the MeemSpace Subject.
	 * 
	 */
	public static Subject getSubject() {
		return instance.getSubject();
	}

	/**
	 * Return system-wide access level for a particular Subject.
	 * 
	 * @param subject
	 * @return the access level, if set.
	 */
	public static AccessLevel getAccessLevel(Subject subject) {
		return instance.getAccessLevel(subject);
	}

	/**
	 * Determine whether or not the Subject is valid as far as Maji is
	 * concerned.
	 * 
	 * @param subject
	 */
	public static boolean isValidSubject(Subject subject) {
		return instance.isValidSubject(subject);
	}

	/**
	 * Get Group Principals for a particular user.
	 * 
	 * @param username
	 */
	protected static Principal[] getGroups(String username) {
		return instance.getGroups(username);
	}

	/**
	 * 
	 * @param principal
	 * @param subject
	 */
	protected static PrincipalAuthenticator getAuthenticator(Principal principal) {
		return instance.getAuthenticator(principal);
	}

	/**
	 * Returns whether or not the subject is in the system group.
	 * 
	 * @param subject
	 */
	public static boolean isSystemGroup(Subject subject) {
		return instance.isSystemGroup(subject);
	}

	/**
	 * Returns an Iterator for the Group Principals of the current Subject.
	 * 
	 * @return a Collection of GroupPrincipals for the current Subject.
	 */
	public static Set<GroupPrincipal> getCurrentGroups() {
		return instance.getCurrentGroups();
	}

	/**
	 * Return the Maji system keystore.
	 * 
	 * @return The Maji keystore
	 */
	public static MajiKeyStore getMajiKeyStore() {
		return instance.getMajiKeyStore();
	}

	/**
	 * Provides the Maji user keystore.
	 * 
	 * @return The user keystore
	 */
	public static MajiKeyStore getUserKeyStore() {
		return instance.getUserKeyStore();
	}

	/**
	 * This class does all the work.
	 */
	private static class MeemCoreAuth {
		/**
		 * Grace period (in days) before certificate expiry is enforced
		 */
		static final int GRACE_PERIOD = 30;

		/**
		 * local variables
		 */
		private MajiKeyStore keyStore;

		private MajiKeyStore userKeyStore;

		/**
		 * Password for the MeemServer key
		 */
		// private char[] keyPasswd;

		/**
		 * A factory for creating certificates.
		 */
		// private CertificateFactory certFact;

		/**
		 * Certificate for the MeemSpace.
		 */
		private X509Certificate spaceCert = null;

		/**
		 * The Subject for the MeemServer. This is used for Genesis.
		 */
		private Subject serverSubject;

		/**
		 * A cache of validated Subjects.
		 */
		private Set<Subject> subjects = Collections.synchronizedSet(new HashSet<Subject>());

		/**
		 * A Map of users to server-wide AccessLevels
		 */
		private Map<Principal, AccessLevel> accessLevels = Collections.synchronizedMap(new HashMap<Principal, AccessLevel>());

		final GroupFile userGroupFile = new GroupFile(System.getProperty(GROUP_FILE));
		final PasswordFile userPasswordFile = new PasswordFile(System.getProperty(USER_PASSWORD_FILE));
		final GroupFile compositeGroupFile = null;

		/**
		 * 
		 */
		public MeemCoreAuth() {
			init();
		}

		public GroupFile getUserGroupFile() {
			return userGroupFile;
		}

		public PasswordFile getUserPasswordFile() {
			return userPasswordFile;
		}

		public GroupFile getCompositeGroupFile() {
			return compositeGroupFile;
		}

		public Subject getSubject() {
			return serverSubject;
		}

		public MajiKeyStore getMajiKeyStore() {
			return keyStore;
		}

		public MajiKeyStore getUserKeyStore() {
			return userKeyStore;
		}

		/**
		 * Return system-wide access level for a particular Subject.
		 * 
		 * @param subject
		 * @return the access level, if set.
		 */
		public AccessLevel getAccessLevel(Subject subject) {

			if (!isValidSubject(subject)) {
				throw new AccessControlException("Not a valid subject");
			}

			Set<X500Principal> principals = subject.getPrincipals(X500Principal.class);

			if (principals.size() < 1) {
				// no X500 principal
				return null;
			}

			X500Principal id = (X500Principal) principals.toArray()[0];
			AccessLevel level = accessLevels.get(id);

			if (level != null) {
				return level;
			}

			Set<PrincipalAuthenticator> groups = subject.getPublicCredentials(PrincipalAuthenticator.class);
			for (PrincipalAuthenticator pAuth : groups) {
				if (!pAuth.isValid()) {
					continue;
				}

				AccessLevel l = accessLevels.get(pAuth.getPrincipal());

				if (level == null) {
					level = l;
				}
				else {
					if (level.isGrantedBy(l)) {
						level = l;
					}
				}
			}

			return level;
		}

		/**
		 * 
		 * @param subject
		 */
		private boolean isValidSubject(Subject subject) {
			if (subject == null) {
				logger.log(Level.INFO, "Subject is null", new Exception());
				return false;
			}

			if (subjects.contains(subject)) {
				// subject is in cache
				return true;
			}

			//
			// verify the subject is coherent
			//
			Set<X500Principal> principals = subject.getPrincipals(X500Principal.class);

			if (principals.size() == 0) {
				logger.log(Level.INFO, "Subject has no X500 Principal: " + subject.getClass() + " : " + subject);
				// new Exception().printStackTrace();
				return false;
			}

			X500Principal id = (X500Principal) principals.toArray()[0];

			synchronized (subject) {
				if (!isValidCert(spaceCert)) {
					return false;
				}

				if (!subject.isReadOnly()) {
					Set<CertPath> certs = subject.getPublicCredentials(CertPath.class);
					if (certs.size() == 0) {

						logger.log(Level.INFO, "Subject does not have a certificate path: " + id);
						Set<X509Certificate> certificates = subject.getPublicCredentials(X509Certificate.class);
						logger.log(Level.INFO, "But has " + certificates.size() + " certificates");

						return false;

						// Set certificates =
						// subject.getPublicCredentials(X509Certificate.class);
						//
						// try {
						// CertificateFactory certFact =
						// CertificateFactory.getInstance("X.509", "SUN");
						// CertPath certPath =
						// certFact.generateCertPath(sortCerts(new
						// ArrayList(certificates)));
						//
						// if (!checkPath(certPath)) {
						// return false;
						// }
						// }
						// catch (Exception e) {
						// logger.log(Level.WARNING, "Could not validate user: "
						// + e, e);
						// return false;
						// }
					}
					else {
						CertPath certPath = (CertPath) certs.toArray()[0];

						try {
							if (!checkPath(certPath)) {
								return false;
							}
						}
						catch (Exception e) {
							logger.log(Level.WARNING, "Could not validate user, " + id + ": " + e.getMessage());
							return false;
						}

						// List certificates = certPath.getCertificates();
						//
						// for (int i = 0; i != certificates.size(); i++) {
						// subject.getPublicCredentials().add(certificates.get(i));
						// }
					}

					// subject.setReadOnly();

					subjects.add(subject);
				}
				else {
					return subjects.contains(subject);
				}
			}

			return true;
		}

		/**
		 * 
		 * @param username
		 */
		public Principal[] getGroups(String username) {
			return userGroupFile.getGroups(username);
		}

		/**
		 * 
		 * @param principal
		 */
		public PrincipalAuthenticator getAuthenticator(Principal principal) {
			return new PrincipalAuthenticatorImpl(principal);
		}

		/**
		 * @param subject
		 */
		public boolean isSystemGroup(Subject subject) {
			if (isValidSubject(subject)) {
				Set<PrincipalAuthenticator> groups = subject.getPublicCredentials(PrincipalAuthenticator.class);
				for (PrincipalAuthenticator pAuth : groups) {
					if (!pAuth.isValid()) {
						continue;
					}

					if (pAuth.getPrincipal().equals(Principals.SYSTEM)) {
						return true;
					}
				}
			}

			return false;
		}

		/**
		 * 
		 */
		public Set<GroupPrincipal> getCurrentGroups() {

			Subject currentSubject = Subject.getSubject(java.security.AccessController.getContext());

			if (isValidSubject(currentSubject)) {
				Set<GroupPrincipal> groups = currentSubject.getPrincipals(GroupPrincipal.class);
				return groups;
			}

			return null;
		}

		/**
		 * Initialize this object. Load the keystore. Determine the meemspace
		 * certificate.
		 * 
		 */
		private void init() {
			keyStore = loadMajiKeyStore();
			userKeyStore = loadUserKeyStore();

			initServerSubject();

		}

		private void initServerSubject() {
			try {

				//
				// get the MeemServer's certificates
				//
				Certificate[] serverCerts = keyStore.getCertificateChain(KEY_ID);
				if (serverCerts == null || serverCerts.length == 0) {
					throw new RuntimeException("meem server certificate chain not present.");
				}

				if (!(serverCerts[0] instanceof X509Certificate)) {
					throw new RuntimeException("meem server certificate chain does not contain X.509 certificates.");
				}

				// assume the second cert in the chain is the MeemSpace
				// certificate
				spaceCert = (X509Certificate) serverCerts[1];

				if (!isValidCert(spaceCert)) {
					throw new RuntimeException("Unable to proceed due to expired meem server certificate.");
				}

				// create the public credentials
				List<Certificate> serverChain = Arrays.asList(serverCerts);

				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "SUN");
				CertPath certPath = certificateFactory.generateCertPath(sortCerts(new ArrayList<Certificate>(serverChain)));

				//
				// get the private key
				//
				String keyStorePasswd = System.getProperty(KEYSTORE_PASSWD);
				if (keyStorePasswd == null) {
					throw new RuntimeException("unable to find property for key store password.");
				}
				// key password is the same as the keystore password
				char[] keyPasswd = keyStorePasswd.toCharArray();

				Key serverKey = keyStore.getKey(KEY_ID, keyPasswd);

				if (serverKey == null) {
					throw new RuntimeException("unable to find private key for meem server");
				}

				if (!(serverKey instanceof PrivateKey)) {
					throw new RuntimeException("key found not a private key");
				}

				// create the private credentials
				X509Certificate serverCert = (X509Certificate) serverCerts[0];
				X500PrivateCredential serverPrivate = new X500PrivateCredential(serverCert, (PrivateKey) serverKey, KEY_ID);

				serverSubject = new Subject(false, Collections.singleton(serverCert.getSubjectX500Principal()), Collections.singleton(certPath), Collections.singleton(serverPrivate));

				// add "system" group principal to the "server" subject
				serverSubject.getPrincipals().add(Principals.SYSTEM);
				serverSubject.getPublicCredentials().add(getAuthenticator(Principals.SYSTEM));

				// "server" subject is not updatable
				// serverSubject.setReadOnly();

				subjects.add(serverSubject);

				// grant admin access to MeemSpace principal
				accessLevels.put(spaceCert.getSubjectX500Principal(), AccessLevel.ADMINISTER);

				// grant admin access to "system" group
				accessLevels.put(Principals.SYSTEM, AccessLevel.ADMINISTER);
			}
			catch (GeneralSecurityException e) {
				throw new RuntimeException("exception loading key store or certificate factory.", e);
			}
		}

		/**
	     * 
	     */
		private MajiKeyStore loadMajiKeyStore() {

			String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
			if (majitekDirectory == null) {
				throw new RuntimeException("majitek directory not set.");
			}

			String keyStoreName = System.getProperty(KEYSTORE_NAME);
			if (keyStoreName == null) {
				throw new RuntimeException("unable to find property for key store name.");
			}

			String keyStorePasswd = System.getProperty(KEYSTORE_PASSWD);
			if (keyStorePasswd == null) {
				throw new RuntimeException("unable to find property for key store password.");
			}

			char[] storePasswd = keyStorePasswd.toCharArray();

			try {
				// FileInputStream keyStoreFile = new
				// FileInputStream(majitekDirectory + keyStoreName);

				MajiKeyStore keyStore = new MajiKeyStore(majitekDirectory + "/" + keyStoreName, storePasswd);

				return keyStore;
			}
			catch (KeyStoreException e) {
				throw new RuntimeException("Cannot load keystore.", e);
			}
			catch (CertificateException e) {
				throw new RuntimeException("Cannot load keystore.", e);
			}
			catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Cannot load keystore.", e);
			}
			catch (FileNotFoundException e) {
				throw new RuntimeException("Unable to find keystore \"" + majitekDirectory + "/" + keyStoreName + "\".", e);
			}
			catch (IOException e) {
				throw new RuntimeException("Unable to load key store.", e);
			}
		}

		/**
	     * 
	     */
		private MajiKeyStore loadUserKeyStore() {

			String majitekDirectory = System.getProperty(Common.PROPERTY_MAJI_HOME);
			if (majitekDirectory == null) {
				throw new RuntimeException("majitek directory not set.");
			}

			String userKeyStoreName = System.getProperty(MeemCoreRootAuthority.USER_KEYSTORE_NAME);
			if (userKeyStoreName == null) {
				// throw new
				// RuntimeException("unable to find user key store name.");
				return null;
			}

			char[] storePasswd = new char[0];

			String userKeyStorePasswd = System.getProperty(MeemCoreRootAuthority.USER_KEYSTORE_PASSWD);
			if (userKeyStorePasswd != null) {
				storePasswd = userKeyStorePasswd.toCharArray();
			}

			try {
				// FileInputStream keyStoreFile = new
				// FileInputStream(majitekDirectory + userKeyStoreName);

				MajiKeyStore keyStore = new MajiKeyStore(majitekDirectory + userKeyStoreName, storePasswd);

				return keyStore;
			}
			catch (KeyStoreException e) {
				throw new RuntimeException("Cannot load keystore.", e);
			}
			catch (CertificateException e) {
				throw new RuntimeException("Cannot load keystore.", e);
			}
			catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Cannot load keystore.", e);
			}
			catch (FileNotFoundException e) {
				throw new RuntimeException("Unable to find keystore \"" + majitekDirectory + userKeyStoreName + "\".", e);
			}
			catch (IOException e) {
				throw new RuntimeException("Unable to load key store.", e);
			}
		}

		/**
		 * Check the validity date of the passed in certificate, if the
		 * certificate has expired we allow 30 dyas grace before enforcing it -
		 * an error message is generated when the expiration date is reached.
		 * 
		 * @param cert
		 */
		private boolean isValidCert(X509Certificate cert) {
			try {
				cert.checkValidity();
				return true;
			}
			catch (CertificateExpiredException e) {
				try {
					//
					// see if we are in the 30 day grace period
					//
					cert.checkValidity(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * GRACE_PERIOD)));

					logger.log(Level.WARNING,

					"MeemSpace certificate expired on " + cert.getNotAfter() + " please renew before " + GRACE_PERIOD + " days have past.");
					return true;
				}
				catch (Exception ex) {
					if (firstTime == false) {
						firstTime = true;
						logger.log(Level.WARNING, "MeemSpace certificate expired on " + cert.getNotAfter() + " - installation invalid, "
								+ "but TEMPORARILY ignored.  Thank your friendly neighbourhood Maji developer");
					}
					return true;

					/*
					 * logger.log(Level.WARNING,
					 * 
					 * "MeemSpace certificate expired on " + cert.getNotAfter()
					 * + " - installation invalid." ); return false;
					 */
				}
			}
			catch (Exception e) {
				logger.log(Level.WARNING, "Certificate not valid: " + e);
				return false;
			}
		}

		/**
		 * 
		 * Check if the first certificate in the path is signed by the MeemSpace
		 * private key, or whether the certificate of the signer of the first
		 * certificate has been signed by the Space key.
		 * 
		 * @param certPath
		 *            The certificate path to validate.
		 * @return whether the certificate path is valid as far as Maji is
		 *         concerned.
		 * 
		 * @throws InvalidKeyException
		 * @throws CertificateException
		 * @throws NoSuchAlgorithmException
		 * @throws NoSuchProviderException
		 * @throws SignatureException
		 */
		private boolean checkPath(CertPath certPath) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
			List<? extends Certificate> certificates = certPath.getCertificates();

			if (certificates.size() < 2) {
				return false;
			}

			X509Certificate userCert = (X509Certificate) certificates.get(0);

			//
			// first, check if the user certificate been signed by the MeemSpace
			// private key
			//
			try {
				userCert.verify(spaceCert.getPublicKey()); // was the User
															// certificate
															// signed by the
															// MeemSpace
															// certificate
				return isValidCert(userCert);
			}
			catch (SignatureException ex) {
				// User certificate not signed by Space key
			}

			//
			// check the Server certificate was signed by MeemSpace key
			//
			X509Certificate serverCert = (X509Certificate) certificates.get(1);

			serverCert.verify(spaceCert.getPublicKey()); // was the Server cert
															// signed by the
															// MeemSpace cert

			if (!isValidCert(serverCert)) {
				return false;
			}

			// check whether the User certificate was signed by the Server key
			userCert.verify(serverCert.getPublicKey()); // was the User cert signed by the Server cert

			if (!isValidCert(userCert)) {
				return false;
			}

			return true;
		}

		/**
		 * Sort the passed in certificate chain so that it is in the proper
		 * order for storing in a JKS key store - private key certificate first,
		 * root certificate last.
		 * 
		 * @param certs
		 *            unordered list of certificates
		 * @return an ordered list
		 */
		private List<Certificate> sortCerts(List<Certificate> certs) {
			List<Certificate> sorted = new ArrayList<Certificate>(certs.size());

			while (certs.size() > 1) {
				for (int i = 0; i < certs.size(); i++) {
					X509Certificate c = (X509Certificate) certs.get(i);
					boolean subjectFound = false;

					for (int j = 0; j != certs.size(); j++) {
						X509Certificate possibleIssuer = (X509Certificate) certs.get(j);

						// leave the self signed one till last
						if (possibleIssuer.getSubjectX500Principal().equals(possibleIssuer.getIssuerX500Principal())) {
							continue;
						}

						// tag certificates which are the issuers of others.
						if (c.getSubjectX500Principal().equals(possibleIssuer.getIssuerX500Principal())) {
							subjectFound = true;
						}
					}

					if (!subjectFound) {
						sorted.add(c);
						certs.remove(i);
					}
				}
			}
			if (certs.size() > 0) {
				sorted.add(certs.get(0));
			}
			return sorted;
		}

	}

	/**
	 * 
	 */
	private static class PrincipalAuthenticatorImpl implements PrincipalAuthenticator, Serializable {
		private static final long serialVersionUID = -423897872379L;

		private Principal principal;
		private byte[] signature;

		PrincipalAuthenticatorImpl(Principal principal) {
			this.principal = principal;
			this.signature = new byte[1];
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openmaji.implementation.server.meem.core.PrincipalAuthenticator
		 * #getPrincipal()
		 */
		public Principal getPrincipal() {
			return principal;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openmaji.implementation.server.meem.core.PrincipalAuthenticator
		 * #isValid()
		 */
		public boolean isValid() {
			return (this.signature != null);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openmaji.implementation.server.meem.core.PrincipalAuthenticator
		 * #revoke()
		 */
		public void revoke() {
			this.signature = null;
		}
	}

}
