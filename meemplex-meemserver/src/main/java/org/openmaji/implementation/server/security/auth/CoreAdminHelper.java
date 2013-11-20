/*
 * @(#)CoreAdminHelper.java
 *
 * Copyright 2004 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
package org.openmaji.implementation.server.security.auth;

import java.io.ByteArrayInputStream;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;

import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;



import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for manipulating users, passwords, and groups.
 */
public class CoreAdminHelper
{
	private static final Logger logger = Logger.getAnonymousLogger();
	
	static
	{
		if (Security.getProvider("BC") == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	//
	// create the subject key identifier.
	//
	private static SubjectKeyIdentifier createSubjectKeyId(
		PublicKey   pubKey)
	{
		try
		{
			ByteArrayInputStream    bIn = new ByteArrayInputStream(
													pubKey.getEncoded());
			SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
				(ASN1Sequence)new ASN1InputStream(bIn).readObject());

			return new SubjectKeyIdentifier(info);
		}
		catch (Exception e)
		{
			throw new RuntimeException("error creating key");
		}
	}

	//
	// create the authority key identifier.
	//
	private static AuthorityKeyIdentifier createAuthorityKeyId(
		PublicKey       	pubKey,
		X509Principal     name,
		BigInteger       	sNumber)
	{
		try
		{
			ByteArrayInputStream    bIn = new ByteArrayInputStream(
													pubKey.getEncoded());
			SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
													(ASN1Sequence)new ASN1InputStream(bIn).readObject());

			GeneralName             genName = new GeneralName(name);
			ASN1EncodableVector     v = new ASN1EncodableVector();

			v.add(genName);

			return new AuthorityKeyIdentifier(
				info, new GeneralNames(new DERSequence(v)), sNumber);
		}
		catch (Exception e)
		{
			throw new RuntimeException("error creating AuthorityKeyId");
		}
	}
    
	private static void userAdd(
		String			userID,
		char[]			userPass,
		String			userName,
        String          emailAddress,
		Date			expiryDate)
		throws Exception
	{
        if (!userID.toLowerCase().equals(userID)) {
            throw new IllegalArgumentException("username's cannot have mixed case - must be lower case only.");
        }
        
		String keyStorePasswd = System.getProperty(MeemCoreRootAuthority.KEYSTORE_PASSWD);
		if (keyStorePasswd == null) {
			throw new RuntimeException("unable to find property for key store password.");
		}

		X509V3CertificateGenerator  certGen   = new X509V3CertificateGenerator();
		MajiKeyStore                keyStore  = MeemCoreRootAuthority.getMajiKeyStore();
		KeyPairGenerator            kpGen     = KeyPairGenerator.getInstance("RSA");
	    
	    kpGen.initialize(1024);
	    
		// get "server" key
		PrivateKey       signingKey  = (PrivateKey)keyStore.getKey(MeemCoreRootAuthority.KEY_ID, keyStorePasswd.toCharArray());
		Certificate[]    certs       = keyStore.getCertificateChain(MeemCoreRootAuthority.KEY_ID);
		X509Certificate  signingCert = (X509Certificate)certs[0];
		KeyPair          userKey     = kpGen.generateKeyPair();

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(PrincipalUtil.getSubjectX509Principal(signingCert));
		certGen.setNotBefore(new Date());
		certGen.setNotAfter(expiryDate);
		certGen.setPublicKey(userKey.getPublic());
        
        if (emailAddress != null) {
            certGen.setSubjectDN(new X509Principal(new X500Principal("CN=" + userName + ", T=" + userID + ", EMAILADDRESS=" + emailAddress + ", OU=Maji, O=Majitek, C=AU").getEncoded()));
        }
        else {
            certGen.setSubjectDN(new X509Principal(new X500Principal("CN=" + userName + ", T=" + userID + ", OU=Maji, O=Majitek, C=AU").getEncoded()));
        }
		certGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
	    
		certGen.addExtension(
				X509Extensions.SubjectKeyIdentifier,
				false,
				createSubjectKeyId(userKey.getPublic())
			);
                                                                        
		certGen.addExtension(
				X509Extensions.AuthorityKeyIdentifier,
				false,
				createAuthorityKeyId(
						certs[0].getPublicKey(), 
						PrincipalUtil.getSubjectX509Principal(signingCert), 
						signingCert.getSerialNumber()
				)
			);

		X509Certificate newCert = certGen.generateX509Certificate(signingKey);
	    
		Certificate[] chain = new Certificate[certs.length + 1];
	    
		chain[0] = newCert;
		System.arraycopy(certs, 0, chain, 1, certs.length);
		
		//
		// having set up the chain add the user.
		//
		MajiKeyStore userKeyStore = MeemCoreRootAuthority.getUserKeyStore();
				
		try {
			Certificate[] testCerts = userKeyStore.getCertificateChain(userID);
			if (testCerts != null) {
				logger.log(Level.WARNING, "User, \"" + userID + "\" already exists.  The certificate chain might not be updated");
			}
		}
		catch (KeyStoreException e) {
		}
		
		userKeyStore.setKeyEntry(userID, userKey.getPrivate(), userPass, chain);
		
		logger.log(Level.INFO, "User, \"" + userID + "\" added.");
		
		userKeyStore.store();
        
        //
        // store the encrypted password
        //
        byte[]  userPassBytes = new byte[userPass.length];
        for (int i = 0; i != userPass.length; i++) {
            userPassBytes[i] = (byte)userPass[i];
        }
        
        Cipher  cipher = Cipher.getInstance("RSA/NONE/OAEPPadding", "BC");
        
        cipher.init(Cipher.ENCRYPT_MODE, certs[0].getPublicKey());
        
        MeemCoreRootAuthority.getUserPasswordFile().setPassword(userID, cipher.doFinal(userPassBytes));
	}
	
	/**
	 * 
	 */
	private static void systemGroupCheck() 
		throws GeneralSecurityException
	{
		Subject currentSubject = Subject.getSubject(java.security.AccessController.getContext());
		
		if (!MeemCoreRootAuthority.isSystemGroup(currentSubject))
		{
			throw new GeneralSecurityException("Only system group has access to this function.");
		}
	}

	public static String addUser(
		String	userID, 
		String	userName,
        String  emailAddress,
		char[]	userPassword)
		throws Exception
	{
		systemGroupCheck();
		
		long extra = 1000L * 60L * 60L * 24L * 365L;
		long time = System.currentTimeMillis() + extra;

		Date expiryDate = new Date(time);

		userAdd(userID, userPassword, userName, emailAddress, expiryDate);

		return null;
	}

	public static String removeUser(
		String	userID)
		throws Exception
	{
		systemGroupCheck();
		
		MajiKeyStore			userKeyStore = MeemCoreRootAuthority.getUserKeyStore();
		
		userKeyStore.deleteEntry(userID);
		
		userKeyStore.store();
		
        MeemCoreRootAuthority.getUserPasswordFile().remove(userID);
        
		return null;
	}
	
	/**
	 * Return the commonName of the past in X.500 principal.
	 */
	static public String getDNField(
        String          fieldID,
		X500Principal	principal)
	{
		StringTokenizer	st = new StringTokenizer(principal.toString(), "=,");
		
		while (st.hasMoreTokens())
		{
			String	tok = st.nextToken().trim();
			if (tok.equalsIgnoreCase(fieldID))
			{
				return st.nextToken().trim();
			}
		}

		return null;
	}
	
	public static String changePassword(
		String	userID,
		char[]	oldPassword,
		char[]	newPassword)
		throws Exception
	{
		systemGroupCheck();
		
		MajiKeyStore  userKeyStore = MeemCoreRootAuthority.getUserKeyStore();

		Certificate[]	chain = null;
		
		if (userKeyStore.isKeyEntry(userID))
		{
			// NB: Fetching the key will throw an exception if oldPassword is incorrect
//			Key				oldKey =
			userKeyStore.getKey(userID, oldPassword);
			
			 chain = userKeyStore.getCertificateChain(userID);
			
			userKeyStore.deleteEntry(userID);
		}
		
		userKeyStore.store();
		
        X500Principal   principle = ((X509Certificate)chain[0]).getSubjectX500Principal();
        
		addUser(userID, getDNField("CN", principle), getDNField("EMAILADDRESS", principle), newPassword);
		
		return null;
	}
	
    /**
	 * Return the principal associated with the given id.
	 * 
	 * @param id
	 * @throws Exception
	 */
	public static Principal getPrincipal(String id)
		throws Exception
	{	
		MajiKeyStore   userKeyStore     = MeemCoreRootAuthority.getUserKeyStore();
		
		Certificate[]	chain = null;
		
		if (userKeyStore.isKeyEntry(id)) {
			 chain = userKeyStore.getCertificateChain(id);
			
			return ((X509Certificate)chain[0]).getSubjectX500Principal();
		}
		
		return null;
	}
	
	/**
	 * Return the principal associated with the given groupID.
	 * 
	 * @param groupID
	 */
	public static Principal getGroupPrincipal(String groupID)
	{	
		return (Principal)MeemCoreRootAuthority.getUserGroupFile().principals.get(groupID);
	}
	
	/**
	 * Return the userIDs this system knows about.
	 * 
	 * @return an iterator fo userIDs
	 * @throws Exception
	 */
	public static Iterator getUsers()
		throws Exception
	{	
		MajiKeyStore		userKeyStore = MeemCoreRootAuthority.getUserKeyStore();
		
		ArrayList	list = new ArrayList();
		Enumeration	e = userKeyStore.aliases();
		while (e.hasMoreElements()) {
			list.add(e.nextElement());
		}
		return list.iterator();
	}
	
    /**
     * Return an iterator of String arrays containing user details.
     * 
     * @return an iterator of string arrays giving, userID, user name, user email address (null if not present).
     * @throws Exception
     */
    public static Collection getUserDetails()
        throws Exception
    {   
        MajiKeyStore        userKeyStore = MeemCoreRootAuthority.getUserKeyStore();
        
        ArrayList   list = new ArrayList();
        Enumeration e = userKeyStore.aliases();
        while (e.hasMoreElements())
        {
            String[]    details = new String[3];
            String      userID = (String)e.nextElement();

            X509Certificate cert = (X509Certificate)userKeyStore.getCertificate(userID);
            
            details[0] = userID;
            details[1] = getDNField("CN", cert.getSubjectX500Principal());
            details[2] = getDNField("EMAILADDRESS", cert.getSubjectX500Principal());
            
            list.add(details);
        }
        return list;
    }
   
	public static Collection getGroups()
	{
		return MeemCoreRootAuthority.getCurrentGroups();
	}
	
}
