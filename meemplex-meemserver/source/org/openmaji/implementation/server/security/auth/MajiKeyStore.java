/*
 * Created on 20/01/2005
 */
package org.openmaji.implementation.server.security.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Warren Bloomer
 *
 */
public class MajiKeyStore {

	File   keyStoreFile;
	char[] storePassword;
	long   lastModified = 0;;
	KeyStore keyStore;
	
	MajiKeyStore(String filename, char[] pwd) 
		throws KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, IOException, CertificateException
	{
//		keyStore = KeyStore.getInstance("JKS", "SUN");
		this.keyStoreFile  = new File(filename);
		this.storePassword = new char[pwd.length];
		for (int i=0; i<pwd.length; i++) {
			this.storePassword[i] = pwd[i];
		}

		keyStore = KeyStore.getInstance(KeyStore.getDefaultType());		
		FileInputStream fis = new FileInputStream(filename);
		keyStore.load(fis, pwd);
	}
	
	public synchronized Key getKey(String alias, char[] pwd) 
		throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException
	{
		refresh();
		return keyStore.getKey(alias, pwd);
	}
	
	public synchronized Certificate getCertificate(String alias) 
		throws KeyStoreException
	{
		refresh();
		return keyStore.getCertificate(alias);
	}
	
	public synchronized Certificate[] getCertificateChain(String alias) 
		throws KeyStoreException
	{
		refresh();
		return keyStore.getCertificateChain(alias);
	}
	
	public synchronized boolean isKeyEntry(String alias)
		throws KeyStoreException
	{
		refresh();
		return keyStore.isKeyEntry(alias);
	}
	
	public synchronized void setKeyEntry(String alias, Key key, char[] pwd, Certificate[] certs) 
		throws KeyStoreException
	{
		refresh();
		keyStore.setKeyEntry(alias, key, pwd, certs);
	}
	
	public synchronized void deleteEntry(String alias) 
		throws KeyStoreException
	{
		refresh();
		keyStore.deleteEntry(alias);
	}
	
	public synchronized Enumeration<String> aliases() 
		throws KeyStoreException
	{
		refresh();
		return keyStore.aliases();
	}
	
	public synchronized void store() 
		throws FileNotFoundException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException
	{
		if (isKeystoreUpdated()) {
			logger.log(Level.WARNING, "KeyStore has been updated during current changes.  The last update will be overwritten.");
		}
		FileOutputStream fos = new FileOutputStream(keyStoreFile);
		keyStore.store(fos, storePassword);
	}

	
	/**
	 * Required because other code is still accessing the keystore directly.
	 * This will reload the keystore from the keystore file if it has been modified
	 * since the last load.
	 */
	private synchronized void refresh() {
		// check if the keystore file has been updated
		if (isKeystoreUpdated()) {
			loadKeyStoreFile();
		}
	}
	
	/**
	 * Load the keystore file.
	 */
	private void loadKeyStoreFile() {
		
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			
			FileInputStream fis = new FileInputStream(keyStoreFile);
			keyStore.load(fis, storePassword);
			fis.close();
			lastModified = keyStoreFile.lastModified();
		}
		catch (KeyStoreException ex) {
			logger.log(Level.WARNING, "Could not load keystore", ex);
		}
		catch (FileNotFoundException ex) {
			logger.log(Level.WARNING, "Could not load keystore", ex);
		}
		catch (NoSuchAlgorithmException ex) {
			logger.log(Level.WARNING, "Could not load keystore", ex);
		}
		catch (IOException ex) {
			logger.log(Level.WARNING, "Could not load keystore", ex);
		}
		catch (CertificateException ex) {
			logger.log(Level.WARNING, "Could not load keystore", ex);			
		}
	}
	
	private boolean isKeystoreUpdated() {
		return keyStoreFile.lastModified() > lastModified;
	}

	private static final Logger logger = Logger.getAnonymousLogger();
}
