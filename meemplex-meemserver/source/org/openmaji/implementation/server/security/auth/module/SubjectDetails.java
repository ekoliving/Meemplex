/*
 * Created on 9/12/2004
 */
package org.openmaji.implementation.server.security.auth.module;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.LinkedList;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

/**
 * Holds the Principal and Credentials for a Maji Subject.
 * This is used for properly serializing these details.
 * 
 * @author Warren Bloomer
 *
 */
public class SubjectDetails implements Serializable {
	private static final long serialVersionUID = 534540102928363464L;
	
	private String       alias;
	private byte[]       encodedCertificate;
	private byte[]       encodedKey;
	private byte[][]     encodedCertChain;
	
	private String       keyAlgorithm;
//	private String       keyFormat;
	
	private Principal[]  groupPrincipals;
	private Object[]     groupCredentials;

//	private transient Certificate[]         certChain         = null;
	private transient X509Certificate       certificate       = null;
	private transient PrivateKey            privateKey        = null;
	private transient X500PrivateCredential privateCredential = null;
	private transient CertPath              certPath          = null;

	
	/**
	 * 
	 * @param certificates
	 * @param privateKey
	 * @param alias
	 * @param groups
	 * @param groupCredentials
	 */
	public SubjectDetails(
			Certificate[] certificates, 
			PrivateKey privateKey, 
			String alias,
			Principal[] groups,
			Object[] groupCredentials
		) 
		throws CertificateEncodingException
	{
		if ( (certificates == null) || (certificates.length == 0) || !(certificates[0] instanceof X509Certificate) ) {
			throw new SecurityException("No valid certificate chain for \"" + alias + "\"");
		}

		this.alias       = alias;

		this.encodedCertificate = ((X509Certificate)certificates[0]).getEncoded();
		
		this.encodedKey   = privateKey.getEncoded();
		this.keyAlgorithm = privateKey.getAlgorithm();
		//this.keyFormat    = privateKey.getFormat();
		
		this.encodedCertChain = new byte[certificates.length][];
		for (int i=0; i<encodedCertChain.length; i++) {
			encodedCertChain[i] = certificates[i].getEncoded();
		}

		this.groupPrincipals  = groups;
		this.groupCredentials = groupCredentials;
	}
	
	protected X500Principal getPrincipal() 
		throws CertificateException
	{
		X509Certificate certificate = getCertificate();
		return certificate.getSubjectX500Principal();

	}
	
	protected X500PrivateCredential getPrivateCredentials() 
		throws CertificateException, InvalidKeySpecException, NoSuchAlgorithmException
	{
		if (privateCredential == null) {
			X509Certificate certificate = getCertificate();
			PrivateKey      privateKey  = getPrivateKey();
			privateCredential = new X500PrivateCredential(certificate, privateKey, alias);
		}
		return privateCredential;
	}

	protected CertPath getPublicCredentials() 
		throws CertificateException
	{
		
		if (certPath == null) {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			LinkedList<Certificate> certList = new LinkedList<Certificate>();
			
			for (int i=0; i < encodedCertChain.length; i++) {
				ByteArrayInputStream bis = new ByteArrayInputStream(encodedCertChain[i]);
				X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(bis);
	
			    certList.add(certificate);
			}
			certPath =  certificateFactory.generateCertPath(certList);
		}
			    
		return certPath;
	}
	
	protected Principal[] getGroups() {
		return groupPrincipals;
	}
	
	protected Object[] getGroupCredentials() {
		return groupCredentials;
	}
	
	
	private X509Certificate getCertificate() 
		throws CertificateException
	{
		if (certificate == null) {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream bis = new ByteArrayInputStream(encodedCertificate);
			certificate = (X509Certificate) certificateFactory.generateCertificate(bis);
		}
		return certificate;
	}
	
	private PrivateKey getPrivateKey() 
		throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		if (privateKey == null) {
			// assume PKCS8 key format
			EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
			privateKey = keyFactory.generatePrivate(keySpec);
		}
		return privateKey;
	}

//	private void x() {
//		byte[] encodedKey = null;
//		InputStream inStream = null;
//		try {
//			CertificateFactory cf = CertificateFactory.getInstance("X.509");
//			X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
//
//			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encodedKey);
//
//			KeyFactory keyFactory = KeyFactory.getInstance("DSA");
//			PrivateKey privateKey = keyFactory.generatePrivate(privKeySpec);
//
//			X500Principal         principal = cert.getSubjectX500Principal();
//			X500PrivateCredential credential = new X500PrivateCredential(cert, privateKey);
//		}
//		catch (CertificateException ex) {
//			
//		}
//		catch (NoSuchAlgorithmException ex) {
//			
//		}
//		catch (InvalidKeySpecException ex) {
//			
//		}
//	}

}