/*
 * @(#)ClientCertGenTool.java
 *
 * Copyright 2003 by EkoLiving Pty Ltd.  All Rights Reserved.
 *
 * This software is the proprietary information of EkoLiving Pty Ltd.
 * Use is subject to license terms.
 */
 package org.openmaji.implementation.security.utility.cert;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.Date;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * generate a client PKCS12 file, signed using the key contained in the server keystore, client key is stored 
 * with the "friendly name" being the value of the CN attribute.
 */
public class ClientCertGenTool
{	
    public static void main(
        String[]    args)
        throws Exception
    {
    	if (args.length != 4)
    	{
 			System.err.println("usage: ClientCertGenTool clientName clientPasswd signingKeyStore signingKeyStorePasswd");
 			System.err.println("             eg. ClientCertGenTool  \"CN=Client, C=AU\" cpasswd signing.jks spasswd" );
 			System.exit(1);
    	}
    	
    	String	clientName = args[0];
    	char[]	clientPasswd = args[1].toCharArray();
    	String	storeName = args[2];
    	char[]	storePasswd = args[3].toCharArray();
    	
		String 	friendlyName = "Client";
		int		ind = clientName.indexOf("CN=");
		
		if (ind >= 0)
		{
			friendlyName = clientName.substring(ind + 3);
			
			ind = friendlyName.indexOf(',');
			if (ind > 0)
			{
				friendlyName  = friendlyName.substring(0, ind);
			}
		}
		
		//
		// the business end...
		//
		Security.addProvider(new BouncyCastleProvider());
		KeyPairGenerator	kpGen = KeyPairGenerator.getInstance("RSA", "BC");

		kpGen.initialize(1024, new SecureRandom());

        //
        // personal keys
        //
		KeyPair		    kp = kpGen.generateKeyPair();
        PrivateKey     	clPrivKey = kp.getPrivate();
        PublicKey   	clPubKey = kp.getPublic();

        //
        // ca keys
        //
		KeyStore		root = KeyStore.getInstance("JKS");
		
		root.load(new FileInputStream(storeName), storePasswd);
		
		Enumeration e = root.aliases();
		String      keyAlias = null;
                                                                                
		while (e.hasMoreElements())
		{
			String  alias = (String)e.nextElement();
                                                                                
			if (root.isKeyEntry(alias))
			{
				keyAlias = alias;
			}
		}
                                                                                
		if (keyAlias == null)
		{
			System.err.println("can't find a private key!");
			System.exit(0);
		}
                                                                                
		Certificate[]   		chain = root.getCertificateChain(keyAlias);

//        PrivateKey rtPrivKey = (PrivateKey)
		root.getKey(keyAlias, storePasswd);

		//
		//	valid dates
		//
		Date					notBefore = new Date(System.currentTimeMillis() - (1000L * 60 * 60 * 24 * 30));
		Date					notAfter = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365));
		
		long	serial = 1;
		
		//
		// create the certficates
		//        								
		X509Certificate   clCert = CertUtil.createCert(
								clPubKey, 
								BigInteger.valueOf(serial++), 
								clientName,
								notBefore, 
								notAfter, 
								"SHA1WithRSAEncryption",
								clPrivKey, (X509Certificate)chain[0], friendlyName);
						
		//
		// create the client store
		//		
		KeyStore store = KeyStore.getInstance("PKCS12", "BC");

		store.load(null, null);

		Certificate[] clientChain = new Certificate[chain.length + 1];

		System.arraycopy(chain, 0, clientChain, 1, chain.length);
		clientChain[0] = clCert;

		store.setKeyEntry("Client", clPrivKey, null, clientChain);

		FileOutputStream fOut = new FileOutputStream("client.p12");

		store.store(fOut, clientPasswd);
	}
}
